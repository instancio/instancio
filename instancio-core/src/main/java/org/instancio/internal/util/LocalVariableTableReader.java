/*
 * Copyright 2022-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.internal.util;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A minimal class file parser that extracts constructor parameter names
 * from the {@code LocalVariableTable} attribute.
 *
 * <p>Any parsing failure results in an empty result rather than an error.
 *
 * <p>See {@link ConstructorParameterNames} for when the attribute is
 * available and why it can be relied on.
 */
@SuppressWarnings({
        "PMD.AvoidReassigningLoopVariables",
        "PMD.CyclomaticComplexity",
        "PMD.ReturnEmptyCollectionRatherThanNull",
        "PMD.UseProperClassLoader"})
final class LocalVariableTableReader {

    private static final Logger LOG = LoggerFactory.getLogger(LocalVariableTableReader.class);

    private static final int MAGIC = 0xCAFEBABE;
    private static final String CTOR_METHOD_NAME = "<init>";

    /**
     * Returns constructor parameter names keyed by constructor descriptor,
     * e.g. {@code "(Ljava/lang/String;I)V"}. Constructors whose parameter
     * names cannot be determined are absent from the map.
     */
    static Map<String, String[]> getConstructorParameterNames(final Class<?> klass) {
        try (InputStream in = getClassFileStream(klass)) {
            if (in == null) {
                LOG.trace("Could not load the file of {}", klass);
                return Collections.emptyMap();
            }
            return parse(new DataInputStream(new ByteArrayInputStream(in.readAllBytes())));
        } catch (Exception ex) {
            LOG.trace("Failed parsing the file of {}", klass, ex);
            return Collections.emptyMap();
        }
    }

    @Nullable
    private static InputStream getClassFileStream(final Class<?> klass) {
        final String resource = klass.getName().replace('.', '/') + ".class";
        final ClassLoader classLoader = klass.getClassLoader();
        return classLoader == null
                ? ClassLoader.getSystemResourceAsStream(resource)
                : classLoader.getResourceAsStream(resource);
    }

    private static Map<String, String[]> parse(final DataInputStream in) throws IOException {
        if (in.readInt() != MAGIC) {
            return Collections.emptyMap();
        }
        skip(in, 4); // minor_version, major_version

        final String[] constantPool = readConstantPool(in);

        skip(in, 6); // access_flags, this_class, super_class
        skip(in, in.readUnsignedShort() * 2); // interfaces

        final int fieldCount = in.readUnsignedShort();
        for (int i = 0; i < fieldCount; i++) {
            skip(in, 6); // access_flags, name_index, descriptor_index
            skipAttributes(in);
        }

        final Map<String, String[]> results = new HashMap<>();
        final int methodCount = in.readUnsignedShort();

        for (int i = 0; i < methodCount; i++) {
            skip(in, 2); // access_flags
            final String methodName = constantPool[in.readUnsignedShort()];
            final String descriptor = constantPool[in.readUnsignedShort()];

            if (CTOR_METHOD_NAME.equals(methodName)) {
                final String @Nullable [] names = readParameterNamesFromMethod(in, constantPool, descriptor);
                if (names != null) {
                    results.put(descriptor, names);
                }
            } else {
                skipAttributes(in);
            }
        }
        return results;
    }

    /**
     * Reads the constant pool, retaining only UTF-8 entries;
     * other entries are skipped over.
     */
    @SuppressWarnings("java:S6208")
    private static String[] readConstantPool(final DataInputStream in) throws IOException {
        final int count = in.readUnsignedShort();
        final String[] pool = new String[count];

        for (int i = 1; i < count; i++) {
            final int tag = in.readUnsignedByte();
            switch (tag) {
                case 1: // Utf8
                    pool[i] = in.readUTF();
                    break;
                case 7: // Class
                case 8: // String
                case 16: // MethodType
                case 19: // Module
                case 20: // Package
                    skip(in, 2);
                    break;
                case 15: // MethodHandle
                    skip(in, 3);
                    break;
                case 3: // Integer
                case 4: // Float
                case 9: // Fieldref
                case 10: // Methodref
                case 11: // InterfaceMethodref
                case 12: // NameAndType
                case 17: // Dynamic
                case 18: // InvokeDynamic
                    skip(in, 4);
                    break;
                case 5: // Long
                case 6: // Double
                    skip(in, 8);
                    i++; // occupies two constant pool slots
                    break;
                default:
                    throw new IOException("Unexpected constant pool tag: " + tag);
            }
        }
        return pool;
    }

    /**
     * Scans the method's attributes for {@code Code}, then the code
     * attributes for {@code LocalVariableTable}, and maps local variable
     * slots back to constructor parameters.
     */
    private static String @Nullable [] readParameterNamesFromMethod(
            final DataInputStream in,
            final String[] constantPool,
            final String descriptor) throws IOException {

        final int[] slotWidths = getParameterSlotWidths(descriptor);
        // Slot 0 is 'this', followed by one or two slots per parameter
        final int[] slotNameIndexes = new int[1 + sum(slotWidths)];
        boolean lvtFound = false;

        final int attributeCount = in.readUnsignedShort();
        for (int i = 0; i < attributeCount; i++) {
            final String attributeName = constantPool[in.readUnsignedShort()];
            final int attributeLength = in.readInt();

            if ("Code".equals(attributeName)) {
                // Parsed from its own bounded slice: the enclosing stream is
                // advanced by the attribute's declared length regardless of
                // what the contents turn out to be, so that a Code attribute
                // that cannot be parsed as expected cannot desynchronise the
                // reading of subsequent methods (which could otherwise yield
                // incorrect names rather than no names)
                final DataInputStream code = new DataInputStream(
                        new ByteArrayInputStream(in.readNBytes(attributeLength)));

                lvtFound |= readLocalVariableTable(code, constantPool, slotNameIndexes);
            } else {
                skip(in, attributeLength);
            }
        }
        return lvtFound ? mapSlotsToParameters(slotWidths, slotNameIndexes, constantPool) : null;
    }

    /**
     * Reads the {@code Code} attribute's own attributes, recording the name
     * index of each local variable slot that holds a parameter. Returns
     * {@code true} if a {@code LocalVariableTable} was present.
     */
    @SuppressWarnings("PMD.UseVarargs")
    private static boolean readLocalVariableTable(
            final DataInputStream in,
            final String[] constantPool,
            final int[] slotNameIndexes) throws IOException {

        skip(in, 4); // max_stack, max_locals
        skip(in, in.readInt()); // code
        skip(in, in.readUnsignedShort() * 8); // exception_table

        boolean lvtFound = false;
        final int attributeCount = in.readUnsignedShort();

        for (int i = 0; i < attributeCount; i++) {
            final String attributeName = constantPool[in.readUnsignedShort()];
            final int attributeLength = in.readInt();

            if ("LocalVariableTable".equals(attributeName)) {
                lvtFound = true;
                final int entryCount = in.readUnsignedShort();

                for (int j = 0; j < entryCount; j++) {
                    final int startPc = in.readUnsignedShort();
                    skip(in, 2); // length
                    final int nameIndex = in.readUnsignedShort();
                    skip(in, 2); // descriptor_index
                    final int slot = in.readUnsignedShort();

                    // Parameters are in scope from the start of the method.
                    // Slots above the parameters belong to local variables.
                    if (startPc == 0 && slot < slotNameIndexes.length) {
                        slotNameIndexes[slot] = nameIndex;
                    }
                }
            } else {
                skip(in, attributeLength);
            }
        }
        return lvtFound;
    }

    /**
     * Maps parameter positions to local variable slots: slot 0 is {@code this};
     * parameters start at slot 1, with {@code long} and {@code double}
     * occupying two slots.
     */
    @SuppressWarnings("PMD.UseVarargs")
    private static String @Nullable [] mapSlotsToParameters(
            final int[] slotWidths,
            final int[] slotNameIndexes,
            final String[] constantPool) {

        final String[] names = new String[slotWidths.length];
        int slot = 1;

        for (int i = 0; i < slotWidths.length; i++) {
            final int nameIndex = slotNameIndexes[slot];
            final String name = nameIndex == 0 ? null : constantPool[nameIndex];
            if (name == null) {
                return null;
            }
            names[i] = name;
            slot += slotWidths[i];
        }
        return names;
    }

    @SuppressWarnings("PMD.UseVarargs")
    private static int sum(final int[] values) {
        int total = 0;
        for (final int value : values) {
            total += value;
        }
        return total;
    }

    /**
     * Returns the number of local variable slots occupied by each
     * parameter in the given method descriptor.
     */
    private static int[] getParameterSlotWidths(final String descriptor) {
        final int[] widths = new int[descriptor.length()];
        int count = 0;
        int i = 1; // skip '('

        while (descriptor.charAt(i) != ')') {
            char c = descriptor.charAt(i);
            int width = 1;
            final boolean isArray = c == '[';

            while (c == '[') {
                c = descriptor.charAt(++i);
            }
            if (c == 'L') {
                i = descriptor.indexOf(';', i);
            } else if (!isArray && (c == 'J' || c == 'D')) {
                // arrays are references and occupy a single slot
                width = 2;
            }
            widths[count++] = width;
            i++;
        }
        return Arrays.copyOf(widths, count);
    }

    private static void skipAttributes(final DataInputStream in) throws IOException {
        final int count = in.readUnsignedShort();
        for (int i = 0; i < count; i++) {
            skip(in, 2); // attribute_name_index
            skip(in, in.readInt());
        }
    }

    private static void skip(final DataInputStream in, final int numBytes) throws IOException {
        if (in.skipBytes(numBytes) != numBytes) {
            throw new IOException("Unexpected end of class file");
        }
    }

    private LocalVariableTableReader() {
        // non-instantiable
    }
}

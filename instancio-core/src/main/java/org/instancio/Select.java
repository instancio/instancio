/*
 * Copyright 2022 the original author or authors.
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
package org.instancio;

import org.instancio.Selector.SelectorType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A collection of static factory methods selecting fields and classes.
 * <p>
 * Examples:
 * <ul>
 *   <li>{@code field(Example.class, "someField")} - select some field of Example class</li>
 *   <li>{@code all(Example.class)} - select all instances of Example class</li>
 *   <li>{@code all(SelectorGroup...)} - convenience method for combining multiple selector groups</li>
 *   <li>{@code allStrings()} - select all Strings</li>
 *   <li>{@code allInts()} - select all {@code Integer} objects and {@code int} primitives</li>
 * </ul>
 */
public class Select {
    private static final SelectorGroup ALL_BYTES = all(all(byte.class), all(Byte.class));
    private static final SelectorGroup ALL_SHORTS = all(all(short.class), all(Short.class));
    private static final SelectorGroup ALL_INTS = all(all(int.class), all(Integer.class));
    private static final SelectorGroup ALL_LONGS = all(all(long.class), all(Long.class));
    private static final SelectorGroup ALL_FLOATS = all(all(float.class), all(Float.class));
    private static final SelectorGroup ALL_DOUBLES = all(all(double.class), all(Double.class));
    private static final SelectorGroup ALL_BOOLEANS = all(all(boolean.class), all(Boolean.class));
    private static final SelectorGroup ALL_CHARS = all(all(char.class), all(Character.class));
    private static final SelectorGroup ALL_STRINGS = all(String.class);

    private Select() {
        // non-instantiable
    }

    /**
     * Select all instances of the given type, not including subtypes.
     * <p>
     * If the type is a primitive or wrapper, this method only selects the specified type. For example:
     * <ul>
     *   <li>{@code all(int.class)} - selects primitive {@code int} but not {@code Integer}</li>
     *   <li>{@code all(Integer.class)} - selects {@code Integer} wrapper but not primitive {@code int}</li>
     * </ul>
     * <p>
     * In order to select both, primitive {@code int} and wrapper, use the {@link #allInts()}.
     *
     * @param type to select
     * @return a selector group composed of a single type selector
     */
    public static SelectorGroup all(final Class<?> type) {
        return SelectorGroupImpl.forType(type);
    }

    /**
     * Convenience method for combining multiple selections, for example:
     *
     * <pre>{@code
     *   all(
     *      field(Address.class, "city"),
     *      field(Address.class, "state"),
     *      field(Address.class, "country"));
     * }</pre>
     *
     * @param selectorGroups to combine
     * @return a group composed of given arguments
     */
    public static SelectorGroup all(final SelectorGroup... selectorGroups) {
        final List<Selector> selectors = new ArrayList<>();
        for (SelectorGroup group : selectorGroups) {
            selectors.addAll(group.getSelectors());
        }
        return new SelectorGroupImpl(selectors);
    }

    /**
     * Selects a field of the specified class.
     *
     * @param declaringClass class declaring the field
     * @param fieldName      field name to select
     * @return a selector group composed of a single field selector
     */
    public static SelectorGroup field(final Class<?> declaringClass, final String fieldName) {
        return SelectorGroupImpl.forField(declaringClass, fieldName);
    }

    /**
     * Selects a field that belongs to the class being created.
     * <p>
     * Example
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .ignore(field("fullName")) // Person.fullName
     *             .create();
     * }</pre>
     *
     * @param fieldName field name to select
     * @return a selector group composed of a single field selector
     */
    public static SelectorGroup field(final String fieldName) {
        return SelectorGroupImpl.forField(fieldName);
    }

    /**
     * Shorthand for {@code all(String.class)}.
     *
     * @return selector for all Strings
     */
    public static SelectorGroup allStrings() {
        return ALL_STRINGS;
    }

    /**
     * Selects all bytes, primitive and wrapper.
     *
     * @return selector for all bytes
     */
    public static SelectorGroup allBytes() {
        return ALL_BYTES;
    }

    /**
     * Selects all floats, primitive and wrapper.
     *
     * @return selector for all floats
     */
    public static SelectorGroup allFloats() {
        return ALL_FLOATS;
    }

    /**
     * Selects all shorts, primitive and wrapper.
     *
     * @return selector for all shorts
     */
    public static SelectorGroup allShorts() {
        return ALL_SHORTS;
    }

    /**
     * Selects all integers, primitive and wrapper.
     *
     * @return selector for all integers
     */
    public static SelectorGroup allInts() {
        return ALL_INTS;
    }

    /**
     * Selects all longs, primitive and wrapper.
     *
     * @return selector for all longs
     */
    public static SelectorGroup allLongs() {
        return ALL_LONGS;
    }

    /**
     * Selects all doubles, primitive and wrapper.
     *
     * @return selector for all doubles
     */
    public static SelectorGroup allDoubles() {
        return ALL_DOUBLES;
    }

    /**
     * Selects all booleans, primitive and wrapper.
     *
     * @return selector for all booleans
     */
    public static SelectorGroup allBooleans() {
        return ALL_BOOLEANS;
    }


    /**
     * Selects all characters, primitive and wrapper.
     *
     * @return selector for all characters
     */
    public static SelectorGroup allChars() {
        return ALL_CHARS;
    }

    static class SelectorImpl implements Selector {
        private final SelectorType selectorType;
        private final Class<?> targetClass;
        private final String fieldName;

        SelectorImpl(final SelectorType selectorType,
                     @Nullable final Class<?> targetClass,
                     @Nullable final String fieldName) {

            this.selectorType = selectorType;
            this.targetClass = targetClass;
            this.fieldName = fieldName;
        }

        @Override
        public SelectorType selectorType() {
            return selectorType;
        }

        @Override
        public Class<?> getTargetClass() {
            return targetClass;
        }

        @Override
        public String getFieldName() {
            return fieldName;
        }

        @Override
        public final boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof Selector)) return false;
            final Selector that = (Selector) o;
            return selectorType == that.selectorType()
                    && Objects.equals(targetClass, that.getTargetClass())
                    && Objects.equals(fieldName, that.getFieldName());
        }

        @Override
        public final int hashCode() {
            return Objects.hash(selectorType, targetClass, fieldName);
        }
    }

    static class SelectorGroupImpl implements SelectorGroup {
        private final List<Selector> selectors;

        SelectorGroupImpl(final List<Selector> selectors) {
            this.selectors = Collections.unmodifiableList(selectors);
        }

        SelectorGroupImpl(final Selector... selectors) {
            this(Arrays.asList(selectors));
        }

        static SelectorGroup forField(@Nullable final Class<?> targetType, final String fieldName) {
            return new SelectorGroupImpl(new SelectorImpl(SelectorType.FIELD, targetType, fieldName));
        }

        static SelectorGroup forField(final String fieldName) {
            return new SelectorGroupImpl(new SelectorImpl(SelectorType.FIELD, null, fieldName));
        }

        static SelectorGroup forType(final Class<?> targetType) {
            return new SelectorGroupImpl(new SelectorImpl(SelectorType.TYPE, targetType, null));
        }

        @Override
        public List<Selector> getSelectors() {
            return selectors;
        }

        @Override
        public final boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof SelectorGroup)) return false;
            final SelectorGroup selectorGroup = (SelectorGroup) o;
            return Objects.equals(selectors, selectorGroup.getSelectors());
        }

        @Override
        public final int hashCode() {
            return Objects.hash(selectors);
        }
    }

}
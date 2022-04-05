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

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of static factory methods for creating {@link Binding}s.
 * <p>
 * A binding allows targeting a specific class or field.
 * <p>
 * Examples:
 * <ul>
 *   <li>{@code field(Example.class, "someField")} - target some field of Example class</li>
 *   <li>{@code all(Example.class)} - target all instances of Example class</li>
 *   <li>{@code of(Binding...)} - convenience method for targeting multiple bindings</li>
 *   <li>{@code allStrings()} - target all Strings</li>
 *   <li>{@code allInts()} - target all {@code Integer} objects and {@code int} primitives</li>
 * </ul>
 */
public class Bindings {
    private static final Binding ALL_BYTES = of(all(byte.class), all(Byte.class));
    private static final Binding ALL_SHORTS = of(all(short.class), all(Short.class));
    private static final Binding ALL_INTS = of(all(int.class), all(Integer.class));
    private static final Binding ALL_LONGS = of(all(long.class), all(Long.class));
    private static final Binding ALL_FLOATS = of(all(float.class), all(Float.class));
    private static final Binding ALL_DOUBLES = of(all(double.class), all(Double.class));
    private static final Binding ALL_BOOLEANS = of(all(boolean.class), all(Boolean.class));
    private static final Binding ALL_CHARS = of(all(char.class), all(Character.class));
    private static final Binding ALL_STRINGS = all(String.class);

    private Bindings() {
        // non-instantiable
    }

    public static Binding of(final Binding... bindings) {
        final List<Binding.BindingTarget> targets = new ArrayList<>();
        for (Binding b : bindings) {
            targets.addAll(b.getTargets());
        }
        return new Binding(targets);
    }

    /**
     * Creates a binding for the given class's field.
     *
     * @param declaringClass class declaring the field
     * @param fieldName      field name to bind
     * @return binding
     */
    public static Binding field(final Class<?> declaringClass, final String fieldName) {
        return Binding.fieldBinding(declaringClass, fieldName);
    }

    /**
     * Creates a binding for a field that belongs to the class being created.
     * <p>
     * Example
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *             .ignore(field("fullName")) // Person.fullName
     *             .create();
     * }</pre>
     *
     * @param fieldName field name to bind
     * @return binding
     */
    public static Binding field(final String fieldName) {
        return Binding.fieldBinding(fieldName);
    }

    /**
     * Creates a binding for the given type.
     * <p>
     * If the type is a primitive or wrapper, this method will only bind the specified type. For example:
     * <ul>
     *   <li>{@code all(int.class)} - binds primitive {@code int}; does not include {@code Integer}</li>
     *   <li>{@code all(Integer.class)} - binds {@code Integer} wrapper; does not include primitive {@code int}</li>
     * </ul>
     * <p>
     * In order to bind both, primitive {@code int} and wrapper, use the {@link #allInts()}.
     *
     * @param type to bind
     * @return binding
     */
    public static Binding all(final Class<?> type) {
        return Binding.typeBinding(type);
    }

    /**
     * Shorthand for {@code all(String.class)}.
     *
     * @return binding for all Strings
     */
    public static Binding allStrings() {
        return ALL_STRINGS;
    }

    /**
     * Binding for all bytes, primitive and wrapper.
     *
     * @return binding for all bytes
     */
    public static Binding allBytes() {
        return ALL_BYTES;
    }

    /**
     * Binding for all floats, primitive and wrapper.
     *
     * @return binding for all floats
     */
    public static Binding allFloats() {
        return ALL_FLOATS;
    }

    /**
     * Binding for all shorts, primitive and wrapper.
     *
     * @return binding for all shorts
     */
    public static Binding allShorts() {
        return ALL_SHORTS;
    }

    /**
     * Binding for all integers, primitive and wrapper.
     *
     * @return binding for all integers
     */
    public static Binding allInts() {
        return ALL_INTS;
    }

    /**
     * Binding for all longs, primitive and wrapper.
     *
     * @return binding for all longs
     */
    public static Binding allLongs() {
        return ALL_LONGS;
    }

    /**
     * Binding for all doubles, primitive and wrapper.
     *
     * @return binding for all doubles
     */
    public static Binding allDoubles() {
        return ALL_DOUBLES;
    }

    /**
     * Binding for all booleans, primitive and wrapper.
     *
     * @return binding for all booleans
     */
    public static Binding allBooleans() {
        return ALL_BOOLEANS;
    }


    /**
     * Binding for all characters, primitive and wrapper.
     *
     * @return binding for all characters
     */
    public static Binding allChars() {
        return ALL_CHARS;
    }

}

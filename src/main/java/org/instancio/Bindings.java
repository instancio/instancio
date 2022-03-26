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

/**
 * A collection of static factory methods for creating {@link Binding}s.
 * <p>
 * A binding allows targeting a specific class or field.
 */
// TODO add examples
public class Bindings {
    private Bindings() {
        // non-instantiable
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
        return all(String.class);
    }

    /**
     * Binding for all bytes, primitive and wrapper.
     *
     * @return binding for all bytes
     */
    public static Binding allBytes() {
        return all(byte.class);
    }

    /**
     * Binding for all integers, primitive and wrapper.
     *
     * @return binding for all integers
     */
    public static Binding allInts() {
        return all(int.class);
    }

    /**
     * Binding for all longs, primitive and wrapper.
     *
     * @return binding for all longs
     */
    public static Binding allLongs() {
        return all(long.class);
    }

    /**
     * Binding for all doubles, primitive and wrapper.
     *
     * @return binding for all doubles
     */
    public static Binding allDoubles() {
        return all(double.class);
    }

}

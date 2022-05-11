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

import org.instancio.internal.ApiValidator;
import org.instancio.internal.selectors.PrimitiveAndWrapperSelectorImpl;
import org.instancio.internal.selectors.ScopeImpl;
import org.instancio.internal.selectors.SelectorGroupImpl;
import org.instancio.internal.selectors.SelectorImpl;
import org.instancio.internal.selectors.SelectorTargetType;

/**
 * A collection of static factory methods selecting fields and classes.
 * <p>
 * Examples:
 * <ul>
 *   <li>{@code field(Example.class, "someField")} - select some field of Example class</li>
 *   <li>{@code all(Example.class)} - select all instances of Example class</li>
 *   <li>{@code all(GroupableSelector...)} - convenience method for combining multiple selectors</li>
 *   <li>{@code allStrings()} - select all Strings</li>
 *   <li>{@code allInts()} - select all {@code Integer} objects and {@code int} primitives</li>
 * </ul>
 */
public final class Select {

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
     * @return a selector for given class
     */
    public static Selector all(final Class<?> type) {
        ApiValidator.notNull(type, "Class must not be null");
        return new SelectorImpl(SelectorTargetType.CLASS, type, null);
    }

    /**
     * A convenience method for combining multiple selectors.
     *
     * @param selectors to combine
     * @return a group containing given selectors
     */
    public static SelectorGroup all(final GroupableSelector... selectors) {
        ApiValidator.notEmpty(selectors, "Selector group must contain at least one selector");
        return new SelectorGroupImpl(selectors);
    }

    /**
     * Selects a field of the specified class.
     *
     * @param declaringClass class declaring the field
     * @param fieldName      field name to select
     * @return a selector for given field
     */
    public static Selector field(final Class<?> declaringClass, final String fieldName) {
        //noinspection ConstantConditions
        final String className = declaringClass == null ? null : declaringClass.getCanonicalName();
        ApiValidator.validateField(declaringClass, fieldName,
                String.format("Invalid field selector: (%s, %s)", className, fieldName));

        return new SelectorImpl(SelectorTargetType.FIELD, declaringClass, fieldName);
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
     * @return a selector for given field
     */
    public static Selector field(final String fieldName) {
        ApiValidator.notNull(fieldName, "Field name must not be null");
        return new SelectorImpl(SelectorTargetType.FIELD, null, fieldName);
    }

    /**
     * Shorthand for {@code all(String.class)}.
     *
     * @return selector for all Strings
     */
    public static Selector allStrings() {
        return all(String.class);
    }

    /**
     * Selects all bytes, primitive and wrapper.
     *
     * @return selector for all bytes
     */
    public static Selector allBytes() {
        return new PrimitiveAndWrapperSelectorImpl(byte.class, Byte.class);
    }

    /**
     * Selects all floats, primitive and wrapper.
     *
     * @return selector for all floats
     */
    public static Selector allFloats() {
        return new PrimitiveAndWrapperSelectorImpl(float.class, Float.class);
    }

    /**
     * Selects all shorts, primitive and wrapper.
     *
     * @return selector for all shorts
     */
    public static Selector allShorts() {
        return new PrimitiveAndWrapperSelectorImpl(short.class, Short.class);
    }

    /**
     * Selects all integers, primitive and wrapper.
     *
     * @return selector for all integers
     */
    public static Selector allInts() {
        return new PrimitiveAndWrapperSelectorImpl(int.class, Integer.class);
    }

    /**
     * Selects all longs, primitive and wrapper.
     *
     * @return selector for all longs
     */
    public static Selector allLongs() {
        return new PrimitiveAndWrapperSelectorImpl(long.class, Long.class);
    }

    /**
     * Selects all doubles, primitive and wrapper.
     *
     * @return selector for all doubles
     */
    public static Selector allDoubles() {
        return new PrimitiveAndWrapperSelectorImpl(double.class, Double.class);
    }

    /**
     * Selects all booleans, primitive and wrapper.
     *
     * @return selector for all booleans
     */
    public static Selector allBooleans() {
        return new PrimitiveAndWrapperSelectorImpl(boolean.class, Boolean.class);
    }

    /**
     * Selects all characters, primitive and wrapper.
     *
     * @return selector for all characters
     */
    public static Selector allChars() {
        return new PrimitiveAndWrapperSelectorImpl(char.class, Character.class);
    }

    /**
     * Creates a scope for narrowing down a selector's target to a field of the specified class.
     * <p>
     * For example, the following will set all lists within {@code Person.address} object to an empty list.
     *
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *         .set(all(List.class).within(scope(Person.class, "address")), Collections.emptyList())
     *         .create();
     * }</pre>
     *
     * @param targetClass of the scope
     * @param fieldName   declared by the target class
     * @return a scope for fine-tuning a selector
     */
    public static Scope scope(final Class<?> targetClass, final String fieldName) {
        //noinspection ConstantConditions
        final String className = targetClass == null ? null : targetClass.getCanonicalName();
        ApiValidator.validateField(targetClass, fieldName,
                String.format("Invalid scope: (%s, %s)", className, fieldName));

        return new ScopeImpl(targetClass, fieldName);
    }

    /**
     * Creates a selector scope for narrowing down a selector's target to the specified class.
     * <p>
     * For example, assuming a {@code Customer} class that has a {@code CustomerConsent} class.
     * the following will set all booleans within {@code CustomerConsent} to {@code true}.
     *
     * <pre>{@code
     *     Customer customer = Instancio.of(Customer.class)
     *         .set(allBooleans().within(scope(CustomerConsent.class)), true)
     *         .create();
     * }</pre>
     *
     * @param targetClass of the scope
     * @return a scope for fine-tuning a selector
     */
    public static Scope scope(final Class<?> targetClass) {
        ApiValidator.notNull(targetClass, "Scope class must not be null");
        return new ScopeImpl(targetClass, null);
    }

    private Select() {
        // non-instantiable
    }
}
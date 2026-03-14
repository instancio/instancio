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
package org.instancio.kotlin

import java.lang.reflect.Field
import java.util.function.Predicate
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField
import org.instancio.FieldSelectorBuilder
import org.instancio.GetMethodSelector
import org.instancio.GroupableSelector
import org.instancio.PredicateSelector
import org.instancio.Scope
import org.instancio.Select
import org.instancio.Selector
import org.instancio.SelectorGroup
import org.instancio.SetMethodSelector
import org.instancio.TargetSelector
import org.instancio.TypeSelectorBuilder
import org.instancio.documentation.ExperimentalApi
import org.instancio.internal.ApiValidator
import org.instancio.kotlin.internal.KApiValidator.noBackingFieldForProperty

/**
 * Provides Kotlin API for creating selectors.
 *
 * This class provides all the functionality of the [Select] Java API
 * with the addition of Kotlin-specific improvements.
 *
 * @since 6.0.0
 */
object KSelect {

    /**
     * Selects all booleans, primitive and wrapper.
     *
     * @return selector for all booleans
     * @since 6.0.0
     */
    @ExperimentalApi
    fun allBooleans(): Selector {
        return Select.allBooleans()
    }

    /**
     * Selects all bytes, primitive and wrapper.
     *
     * @return selector for all bytes
     * @since 6.0.0
     */
    @ExperimentalApi
    fun allBytes(): Selector {
        return Select.allBytes()
    }

    /**
     * Selects all characters, primitive and wrapper.
     *
     * @return selector for all characters
     * @since 6.0.0
     */
    @ExperimentalApi
    fun allChars(): Selector {
        return Select.allChars()
    }

    /**
     * Selects all doubles, primitive and wrapper.
     *
     * @return selector for all doubles
     * @since 6.0.0
     */
    @ExperimentalApi
    fun allDoubles(): Selector {
        return Select.allDoubles()
    }

    /**
     * Selects all floats, primitive and wrapper.
     *
     * @return selector for all floats
     * @since 6.0.0
     */
    @ExperimentalApi
    fun allFloats(): Selector {
        return Select.allFloats()
    }

    /**
     * Selects all integers, primitive and wrapper.
     *
     * @return selector for all integers
     * @since 6.0.0
     */
    @ExperimentalApi
    fun allInts(): Selector {
        return Select.allInts()
    }

    /**
     * Selects all longs, primitive and wrapper.
     *
     * @return selector for all longs
     * @since 6.0.0
     */
    @ExperimentalApi
    fun allLongs(): Selector {
        return Select.allLongs()
    }

    /**
     * Selects all shorts, primitive and wrapper.
     *
     * @return selector for all shorts
     * @since 6.0.0
     */
    @ExperimentalApi
    fun allShorts(): Selector {
        return Select.allShorts()
    }

    /**
     * Shorthand for `all<String>()`.
     *
     * @return selector for all Strings
     * @since 6.0.0
     */
    @ExperimentalApi
    fun allStrings(): Selector {
        return Select.allStrings()
    }

    /**
     * Selects all instances of the given type [T], **not including** subtypes.
     *
     * When the type is a primitive or its wrapper, only the specified type is selected:
     * - `all<Int>()` - selects `Int` (wrapper) but not primitive `int`
     * - `all<String>()` - selects `String` only
     *
     * To select both the primitive and its wrapper, use `allInts()` and related methods.
     *
     * @param T the type to select
     * @return a selector for the given type
     * @since 6.0.0
     */
    @ExperimentalApi
    inline fun <reified T> all(): Selector {
        return Select.all(T::class.java)
    }

    /**
     * A convenience method for combining multiple selectors into a group.
     * All selectors in the group are applied together as if each had been
     * specified individually.
     *
     * Example:
     * ```kotlin
     * val person = KInstancio.of<Person>()
     *     .withNullable(all(
     *         all<Gender>(),
     *         all<Phone>(),
     *         field(Person::getDateOfBirth)
     *     ))
     *     .create()
     * ```
     *
     * @param selectors one or more selectors to combine
     * @return a group containing the given selectors
     * @since 6.0.0
     */
    @ExperimentalApi
    fun all(vararg selectors: GroupableSelector): SelectorGroup {
        return Select.all(*selectors)
    }

    /**
     * Selects the root object.
     *
     * @return the selector for the root object
     * @since 6.0.0
     */
    @ExperimentalApi
    fun root(): TargetSelector {
        return Select.root()
    }

    /**
     * Selects a field by name declared in the class being created.
     *
     * Example:
     * ```kotlin
     * val person = KInstancio.of<Person>()
     *     .ignore(field("fullName")) // Person.fullName
     *     .create()
     * ```
     *
     * @param fieldName field name to select
     * @return a selector for the given field
     * @since 6.0.0
     */
    @ExperimentalApi
    fun field(fieldName: String): Selector {
        return Select.field(fieldName)
    }

    /**
     * Selects a field by name in the specified class [T]. The name must match exactly.
     *
     * @param T         the class declaring the field
     * @param fieldName field name to select
     * @return a selector for the given field
     * @since 6.0.0
     */
    @ExperimentalApi
    @JvmName("fieldOf")
    inline fun <reified T> field(fieldName: String): Selector {
        return Select.field(T::class.java, fieldName)
    }

    /**
     * Selects a field based on the given getter method reference.
     *
     * Internally, the method reference is resolved to a field selector equivalent
     * to `field<T>(fieldName)` using the following strategy:
     *
     * - Java beans convention with `get` and `is` prefixes.
     * - Property-style convention where the method name matches the field name (e.g. Java records).
     *
     * @param methodReference getter method reference from which the field name will be resolved
     * @param T               type declaring the method
     * @param R               return type of the method
     * @return a field selector matching the given method reference
     * @since 6.0.0
     */
    @ExperimentalApi
    fun <T : Any, R : Any> field(methodReference: GetMethodSelector<T, R>): Selector {
        return Select.field(methodReference)
    }

    /**
     * Selects a field based on the given Kotlin property reference.
     *
     * Example:
     * ```kotlin
     * val person = KInstancio.of<Person>()
     *     .ignore(field(Person::name))
     *     .create()
     * ```
     *
     * @param property the Kotlin property reference identifying the field
     * @param T        type declaring the property
     * @param R        type of the property
     * @return a selector for the field backing the given property
     * @since 6.0.0
     */
    @ExperimentalApi
    fun <T, R> field(property: KProperty1<T, R>): Selector {
        val javaField = ApiValidator.notNull(property.javaField) {
            noBackingFieldForProperty(property, "field selector", "field")
        }
        return Select.field(javaField.declaringClass, javaField.name)
    }

    /**
     * Provides a builder for selecting fields based on [Predicate][java.util.function.Predicate]s.
     * The builder supports matching fields by name, name regex, type, declaring class,
     * and annotation, which can be combined to form logical `AND` conditions.
     *
     * The following example matches all fields named `lastModified`
     * declared anywhere in the `Person` object tree:
     *
     * ```kotlin
     * val person = KInstancio.of<Person>()
     *     .supply(fields().named("lastModified")) { LocalDateTime.now() }
     *     .create()
     * ```
     *
     * Calling `fields()` without any further predicates matches all fields in the object tree.
     *
     * @return predicate selector builder for matching fields
     * @since 6.0.0
     */
    @ExperimentalApi
    fun fields(): FieldSelectorBuilder {
        return Select.fields()
    }

    /**
     * Provides a builder for selecting types based on [Predicate][java.util.function.Predicate]s.
     * The builder supports matching types by name, name regex, supertype,
     * and annotation, which can be combined to form logical `AND` conditions.
     *
     * The following example matches all types annotated with `@Embeddable`:
     *
     * ```kotlin
     * val person = KInstancio.of<Person>()
     *     .set(types().annotated(Embeddable::class.java), null)
     *     .create()
     * ```
     *
     * Calling `types()` without any further predicates matches all types in the object tree.
     *
     * @return predicate selector builder for matching types
     * @since 6.0.0
     */
    @ExperimentalApi
    fun types(): TypeSelectorBuilder {
        return Select.types()
    }

    /**
     * Selects all fields matching the specified [Java Predicate][java.util.function.Predicate].
     *
     * @param predicate for matching fields
     * @return a predicate selector
     * @since 6.0.0
     */
    @ExperimentalApi
    fun fields(predicate: Predicate<Field>): PredicateSelector {
        return Select.fields(predicate)
    }

    /**
     * Selects all types matching the specified [Java Predicate][java.util.function.Predicate].
     *
     * @param predicate for matching types
     * @return a predicate selector
     * @since 6.0.0
     */
    @ExperimentalApi
    fun types(predicate: Predicate<Class<*>>): PredicateSelector {
        return Select.types(predicate)
    }

    /**
     * Selects all fields matching the specified Kotlin predicate lambda.
     *
     * @param predicate lambda for matching fields
     * @return a predicate selector
     * @since 6.0.0
     */
    @ExperimentalApi
    fun fields(predicate: (Field) -> Boolean): PredicateSelector {
        return Select.fields(Predicate(predicate))
    }

    /**
     * Selects all types matching the specified Kotlin predicate lambda.
     *
     * @param predicate lambda for matching types
     * @return a predicate selector
     * @since 6.0.0
     */
    @ExperimentalApi
    fun types(predicate: (Class<*>) -> Boolean): PredicateSelector {
        return Select.types(Predicate(predicate))
    }

    /**
     * Selects a setter by name declared in the class being created.
     * The setter method must have exactly one parameter. Since this method
     * resolves the setter by name only (ignoring the parameter type),
     * it should only be used if there are no overloaded setters.
     *
     * Example:
     * ```kotlin
     * val person = KInstancio.of<Person>()
     *     .ignore(setter("setFullName")) // Person.setFullName
     *     .create()
     * ```
     *
     * **Note:** setter selectors can only be used if
     * `Keys.ASSIGNMENT_TYPE` is set to `AssignmentType.METHOD`.
     *
     * @param methodName the name of the setter method to select
     * @return a selector for the given method
     * @since 6.0.0
     */
    @ExperimentalApi
    fun setter(methodName: String): Selector {
        return Select.setter(methodName)
    }

    /**
     * Selects a setter method by name in the specified class [T].
     * The method must have exactly one parameter. Since this method resolves
     * the setter by name only (ignoring the parameter type),
     * it should only be used if there are no overloaded setters.
     *
     * **Note:** setter selectors can only be used if
     * `Keys.ASSIGNMENT_TYPE` is set to `AssignmentType.METHOD`.
     *
     * @param T          the class declaring the method
     * @param methodName method name to select
     * @return a selector for the given method
     * @since 6.0.0
     */
    @ExperimentalApi
    @JvmName("setterOf")
    inline fun <reified T> setter(methodName: String): Selector {
        return Select.setter(T::class.java, methodName)
    }

    /**
     * Selects a setter method by name and parameter type in the specified class [T].
     *
     * **Note:** setter selectors can only be used if
     * `Keys.ASSIGNMENT_TYPE` is set to `AssignmentType.METHOD`.
     *
     * @param T          the class declaring the method
     * @param P          the parameter type of the setter
     * @param methodName method name to select
     * @return a selector for the given method
     * @since 6.0.0
     */
    @ExperimentalApi
    @JvmName("setterOfWithParamType")
    inline fun <reified T, reified P> setter(methodName: String): Selector {
        return Select.setter(T::class.java, methodName, P::class.java)
    }

    /**
     * Selects a setter method based on the given method reference.
     *
     * This selector resolves the class that declares the setter and
     * the method name from the method reference. Since this method resolves
     * the setter by name only (ignoring the parameter type),
     * it should only be used if there are no overloaded setters.
     *
     * **Note:** setter selectors can only be used if
     * `Keys.ASSIGNMENT_TYPE` is set to `AssignmentType.METHOD`.
     *
     * @param methodReference method reference from which the method will be resolved
     * @param T               type declaring the method
     * @param U               the argument type of the method
     * @return a method selector matching the given method reference
     * @since 6.0.0
     */
    @ExperimentalApi
    fun <T : Any, U : Any> setter(methodReference: SetMethodSelector<T, U>): Selector {
        return Select.setter(methodReference)
    }

    /**
     * Creates a scope that restricts a selector to targets within the specified class [T].
     *
     * For example, the following sets all booleans within `CustomerConsent`
     * to `true`, without affecting booleans in other parts of the object tree:
     *
     * ```kotlin
     * val customer = KInstancio.of<Customer>()
     *     .set(allBooleans().within(scope<CustomerConsent>()), true)
     *     .create()
     * ```
     *
     * @param T the class to use as the scope boundary
     * @return a scope for fine-tuning a selector
     * @since 6.0.0
     */
    @ExperimentalApi
    inline fun <reified T> scope(): Scope {
        return Select.scope(T::class.java)
    }

    /**
     * Creates a scope that restricts a selector to targets within the specified field.
     *
     * For example, the following sets all lists nested within `Person.address`
     * to an empty list, without affecting lists elsewhere in the object tree:
     *
     * ```kotlin
     * val person = KInstancio.of<Person>()
     *     .set(all<List<*>>().within(scope<Person>("address")), emptyList<Any>())
     *     .create()
     * ```
     *
     * @param T         the class declaring the field
     * @param fieldName the field name within the target class
     * @return a scope for fine-tuning a selector
     * @since 6.0.0
     */
    @ExperimentalApi
    inline fun <reified T> scope(fieldName: String): Scope {
        return Select.scope(T::class.java, fieldName)
    }

    /**
     * Creates a scope that restricts a selector to targets within the field
     * identified by the given getter method reference.
     *
     * @param methodReference getter method reference identifying the field
     * @param T               type declaring the method
     * @param R               return type of the method
     * @return a scope for fine-tuning a selector
     * @since 6.0.0
     */
    @ExperimentalApi
    fun <T : Any, R : Any> scope(methodReference: GetMethodSelector<T, R>): Scope {
        return Select.scope(methodReference)
    }

    /**
     * Creates a scope from the given predicate selector, restricting a selector's
     * target to nodes matched by the predicate.
     *
     * @param selector the predicate selector defining the scope boundary
     * @return a scope for fine-tuning a selector
     * @since 6.0.0
     */
    @ExperimentalApi
    fun scope(selector: PredicateSelector): Scope {
        return Select.scope(selector)
    }

    /**
     * Creates a scope that restricts a selector to targets within the field
     * identified by the given Kotlin property reference.
     *
     * Example:
     * ```kotlin
     * val person = KInstancio.of<Person>()
     *     .set(all<List<*>>().within(scope(Person::address)), emptyList<Any>())
     *     .create()
     * ```
     *
     * @param property the Kotlin property reference identifying the field
     * @param T        type declaring the property
     * @param R        type of the property
     * @return a scope for fine-tuning a selector
     * @since 6.0.0
     */
    @ExperimentalApi
    fun <T, R> scope(property: KProperty1<T, R>): Scope {
        val javaField = ApiValidator.notNull(property.javaField) {
            noBackingFieldForProperty(property, "scope", "scope")
        }
        return Select.scope(javaField.declaringClass, javaField.name)
    }
}

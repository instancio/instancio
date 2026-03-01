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
package org.instancio.documentation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A copy of {@code org.jetbrains.annotations.Contract}.
 *
 * <p>Specifies some aspects of the method behavior depending on the
 * arguments. Can be used by tools for advanced data flow analysis.
 * Note that this annotation just describes how the code works and doesn't
 * add any functionality by means of code generation.</p>
 *
 * <p>Method contract has the following syntax:</p>
 *
 * <pre>{@code
 *  contract ::= (clause ';')* clause
 *  clause ::= args '->' effect
 *  args ::= ((arg ',')* arg )?
 *  arg ::= value-constraint
 *  value-constraint ::= '_' | 'null' | '!null' | 'false' | 'true'
 *  effect ::= value-constraint | 'fail' | 'this' | 'new' | 'param<N>'
 * }</pre>
 *
 * <p>The constraints denote the following:</p>
 * <ul>
 *   <li>_ - any value</li>
 *   <li>null - null value</li>
 *   <li>!null - a value statically proved to be not-null</li>
 *   <li>true - true boolean value</li>
 *   <li>false - false boolean value</li>
 * </ul>
 *
 * <p>The additional return values denote the following:</p>
 * <ul>
 *   <li>fail - the method throws an exception if the arguments satisfy the
 *       constraints</li>
 *   <li>new - (supported in IntelliJ IDEA since 2018.2) the method returns a
 *       non-null new object distinct from any existing object. If the method
 *       is pure, the object is not stored and is lost if unused</li>
 *   <li>this - (supported in IntelliJ IDEA since 2018.2) the method returns
 *       its qualifier value (not applicable for static methods)</li>
 *   <li>param1, param2, ... - (supported in IntelliJ IDEA since 2018.2) the
 *       method returns its first (second, ...) parameter value</li>
 * </ul>
 *
 * Examples:
 * {@code @Contract("_, null -> null")} - returns null if the second argument is null<br>
 * {@code @Contract("_, null -> null; _, !null -> !null")} - returns null if
 * the second argument is null and not-null otherwise<br>
 * {@code @Contract("true -> fail")} - a typical {@code assertFalse} method
 * which throws if {@code true} is passed<br>
 * {@code @Contract("_ -> this")} - always returns its qualifier (e.g.
 * {@link StringBuilder#append(String)})<br>
 * {@code @Contract("null -> fail; _ -> param1")} - throws if the first
 * argument is null, otherwise returns it (e.g.
 * {@code Objects.requireNonNull})<br>
 * {@code @Contract("!null, _ -> param1; null, !null -> param2; null, null ->
 * fail")} - returns the first non-null argument, or throws if both are null
 * (e.g. {@code Objects.requireNonNullElse} in Java 9)<br>
 *
 * @since 6.0.0
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Contract {

    /**
     * Contains the contract clauses describing causal relations
     * between call arguments and the returned value
     *
     * @since 6.0.0
     */
    String value() default "";
}

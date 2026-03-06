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
 * Describes the null-safety contract of a method in terms of its arguments
 * and return value. This annotation is used by static analysis tools (such as
 * NullAway) to reason about null-flow without running the code.
 *
 * <p>The annotation has {@link RetentionPolicy#CLASS CLASS} retention and
 * does not affect runtime behaviour.
 *
 * <h2>Syntax</h2>
 *
 * <p>A contract consists of one or more semicolon-separated clauses,
 * each of the form {@code args -> effect}:</p>
 *
 * <pre>{@code
 *  contract          ::= (clause ';')* clause
 *  clause            ::= args '->' effect
 *  args              ::= ((arg ',')* arg)?
 *  arg               ::= value-constraint
 *  value-constraint  ::= '_' | 'null' | '!null' | 'false' | 'true'
 *  effect            ::= value-constraint | 'fail' | 'this' | 'new' | 'param<N>'
 * }</pre>
 *
 * <h2>Argument constraints</h2>
 * <ul>
 *   <li>{@code _}     &ndash; any value (wildcard)</li>
 *   <li>{@code null}  &ndash; the argument is {@code null}</li>
 *   <li>{@code !null} &ndash; the argument is statically known to be non-null</li>
 *   <li>{@code true}  &ndash; the boolean argument is {@code true}</li>
 *   <li>{@code false} &ndash; the boolean argument is {@code false}</li>
 * </ul>
 *
 * <h2>Effect tokens</h2>
 * <ul>
 *   <li>{@code null}    &ndash; the method returns {@code null}</li>
 *   <li>{@code !null}   &ndash; the method returns a non-null value</li>
 *   <li>{@code fail}    &ndash; the method throws an exception</li>
 *   <li>{@code this}    &ndash; the method returns its receiver (for fluent APIs)</li>
 *   <li>{@code new}     &ndash; the method returns a newly allocated, distinct object</li>
 *   <li>{@code param1}, {@code param2}, &hellip; &ndash; the method returns its
 *       first, second, &hellip; argument unchanged</li>
 * </ul>
 *
 * <h2>Examples</h2>
 * <ul>
 *   <li>{@code @Contract("_, null -> null")} &ndash; returns {@code null} when
 *       the second argument is {@code null}</li>
 *   <li>{@code @Contract("_, null -> null; _, !null -> !null")} &ndash; returns
 *       {@code null} if the second argument is {@code null}, non-null otherwise</li>
 *   <li>{@code @Contract("true -> fail")} &ndash; always throws when passed
 *       {@code true} (e.g. a typical {@code assertFalse} helper)</li>
 *   <li>{@code @Contract("_ -> this")} &ndash; always returns the receiver,
 *       e.g. {@link StringBuilder#append(String)}</li>
 *   <li>{@code @Contract("null -> fail; _ -> param1")} &ndash; throws for
 *       {@code null} input, otherwise returns it unchanged,
 *       e.g. {@link java.util.Objects#requireNonNull(Object)}</li>
 *   <li>{@code @Contract("!null, _ -> param1; null, !null -> param2; null, null -> fail")}
 *       &ndash; returns the first non-null argument, or throws if both are
 *       {@code null}, e.g. {@link java.util.Objects#requireNonNullElse(Object, Object)}</li>
 * </ul>
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

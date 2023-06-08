/*
 * Copyright 2022-2023 the original author or authors.
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

import org.instancio.documentation.ExperimentalApi;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.conditional.InternalConditionalGivenAction;
import org.instancio.internal.conditional.InternalConditionalValueOf;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A collection of static factory methods for creating conditional expressions
 * and convenience methods for creating predicates.
 *
 * <p>Provides two flavours of builders:
 *
 * <ul>
 *   <li>{@link #given(TargetSelector, TargetSelector)}</li>
 *   <li>{@link #valueOf(TargetSelector)}</li>
 * </ul>
 *
 * @see Conditional
 * @see InstancioApi#when(Conditional...)
 * @since 3.0.0
 */
@ExperimentalApi
public final class When {

    /**
     * Creates a conditional for a given pair of {@code origin} and
     * {@code destination} selectors. This method allows mapping predicates
     * to destination values, and also supports {@code else} semantics with
     * the following syntax:
     *
     * <pre>{@code
     *   When.given(origin, destination)
     *     .set(originPredicate1, "value1")
     *     .set(originPredicate2, "value2")
     *     .supply(originPredicate3, () -> getValue3())
     *     .generate(originPredicate4, gen -> gen.oneOf("value4A", "value4B"))
     *     .elseSet("other-value");
     * }</pre>
     *
     * <p>The destination will be set a given value only if the corresponding
     * predicate is satisfied. If none of the predicates match, the value from
     * the {@code elseSet} method will be set.
     *
     * <p>Example:
     * <pre>{@code
     * Conditional conditional = When.given(field(Address::getCountry), field(Phone::getCountryCode))
     *     .set(When.isIn("Canada", "USA"), "+1")
     *     .set(When.is("Italy"), "+39")
     *     .set(When.is("Poland"), "+48")
     *     .set(When.is("Germany"), "+49")
     *     .elseSupply(() -> Assertions.fail("unexpected country"));
     * }</pre>
     *
     * @param origin      selector whose target the origin predicate
     *                    will be evaluated against
     * @param destination selector whose targets will be set to a
     *                    given value if the origin predicate is satisfied
     * @return builder for constructing a conditional expression
     * @see #valueOf(TargetSelector)
     * @since 3.0.0
     */
    @ExperimentalApi
    public static ConditionalGivenRequiredAction given(final TargetSelector origin, final TargetSelector destination) {
        ApiValidator.validateConditionalOriginSelector(origin);
        return new InternalConditionalGivenAction(origin, destination);
    }

    /**
     * Creates a conditional for a given {@code origin} and one or more
     * {@code destination} selectors. This method allows mapping an origin
     * to different destinations, but unlike
     * {@link #given(TargetSelector, TargetSelector)} this method does not
     * support {@code else} semantics:
     *
     * <pre>{@code
     * When.valueOf(origin)
     *     .is("foo")
     *     .set(destination1, "value1")
     *     .set(destination2, "value2")
     *     .supply(destination3, () -> getValue3())
     *     .generate(destination4, gen -> gen.oneOf("value4A", "value4B"));
     * }</pre>
     *
     * <p>All destinations will be set to the corresponding values if the predicate
     * is satisfied.
     *
     * <p>Example:
     * <pre>{@code
     * Conditional conditional = When.valueOf(Order::getStatus)
     *     .is(OrderStatus.CANCELLED)
     *     .set(field(Order::getCancellationReason), "Shipping delays")
     *     .generate(field(Order::getCancellationDate), gen -> gen.temporal().localDate().past());
     * }</pre>
     *
     * @param origin whose target the origin predicate will be evaluated against
     * @return builder for constructing a conditional expression
     * @see #given(TargetSelector, TargetSelector)
     * @see #valueOf(Class)
     * @see #valueOf(GetMethodSelector)
     * @since 3.0.0
     */
    @ExperimentalApi
    public static ConditionalValueOf valueOf(final TargetSelector origin) {
        ApiValidator.validateConditionalOriginSelector(origin);
        return new InternalConditionalValueOf(origin);
    }

    /**
     * Creates a conditional for a given {@code origin} and one or more
     * {@code destination} selectors. This is a shorthand API of
     * {@link #valueOf(TargetSelector)} allowing:
     *
     * <pre>{@code
     * When.valueOf(field(Pojo::getValue))
     * }</pre>
     * <p>
     * to be specified as a slightly shorter version:
     *
     * <pre>{@code
     * When.valueOf(Pojo::getValue)
     * }</pre>
     *
     * @param origin a method reference whose matching field
     *               the origin predicate will be evaluated against
     * @param <T>    type declaring the method
     * @param <R>    return type of the method
     * @return builder for constructing a conditional expression
     * @see #valueOf(TargetSelector)
     * @since 3.0.0
     */
    @ExperimentalApi
    public static <T, R> ConditionalValueOf valueOf(final GetMethodSelector<T, R> origin) {
        return new InternalConditionalValueOf(Select.field(origin));
    }

    /**
     * Creates a conditional for a given {@code origin} and one or more
     * {@code destination} selectors. This is a shorthand API of
     * {@link #valueOf(TargetSelector)} allowing:
     *
     * <pre>{@code
     * When.valueOf(all(Integer.class))
     * }</pre>
     * <p>
     * to be specified as a slightly shorter version:
     *
     * <pre>{@code
     * When.valueOf(Integer.class)
     * }</pre>
     *
     * @param type of the origin value the origin predicate
     *             will be evaluated against
     * @param <T>  the type
     * @return builder for constructing a conditional expression
     * @see #valueOf(TargetSelector)
     * @since 3.0.0
     */
    @ExperimentalApi
    public static <T> ConditionalValueOf valueOf(final Class<T> type) {
        return new InternalConditionalValueOf(Select.all(type));
    }

    /**
     * Returns a predicate that compares against given {@code value}
     * using {@link Objects#equals(Object, Object)}.
     *
     * @param value to compare with
     * @param <S>   value type
     * @return predicate that performs an equality check
     * @since 3.0.0
     */
    @ExperimentalApi
    public static <S> Predicate<S> is(final S value) {
        return v -> Objects.equals(v, value);
    }

    /**
     * Returns a predicate that checks whether a value is
     * equal to any element in the {@code values} array
     * using {@link Objects#equals(Object, Object)}.
     *
     * @param values to compare with
     * @param <S>    value type
     * @return predicate that checks if a value is equal to
     * any element of the input array
     * @since 3.0.0
     */
    @SafeVarargs
    @ExperimentalApi
    public static <S> Predicate<S> isIn(final S... values) {
        return value -> {
            for (S v : values) {
                if (Objects.equals(v, value)) {
                    return true;
                }
            }
            return false;
        };
    }

    private When() {
        // non-instantiable
    }
}
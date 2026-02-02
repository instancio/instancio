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
package org.instancio;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.assignment.InternalGivenOrigin;
import org.instancio.internal.assignment.InternalGivenOriginDestinationAction;
import org.instancio.internal.assignment.InternalValueOf;

/**
 * A collection of static factory methods for creating assignments.
 *
 * @see Assignment
 * @see InstancioApi#assign(Assignment...)
 * @since 3.0.0
 */
@ExperimentalApi
public final class Assign {

    /**
     * Creates an assignment builder with a given {@code target} selector.
     * This builder provides two options:
     *
     * <ol>
     *   <li>Assign value directly <b>to</b> the {@code target}</li>
     *   <li>Assign value <b>of</b> the {@code target} to another selector</li>
     * </ol>
     *
     * <h4>1. Assign to {@code target}</h4>
     *
     * <p>The first option is to assign a value directly to the {@code target}
     * using {@code set()}, {@code supply()}, or {@code generate()} methods.
     *
     * <p>Example:
     *
     * <pre>{@code
     * Assignment personName = Assign.valueOf(Person::getName).set("Homer Simpson");
     *
     * Person person = Instancio.of(Person.class)
     *     .assign(personName)
     *     .create();
     * }</pre>
     *
     * <p>This sets the {@code Person.name} field to "Homer Simpson".
     * The above snippet is equivalent to:
     *
     * <pre>{@code
     * Person person = Instancio.of(Person.class)
     *     .set(Select.field(Person::getName), "Homer Simpson")
     *     .create();
     * }</pre>
     *
     * <p>The difference is that {@link InstancioApi#assign(Assignment...)}
     * allows passing multiple assignments dynamically to the method since
     * it accepts a vararg. This provides more flexibility when creating
     * objects in different states.
     *
     * <h4>2. Assign {@code target} to another selector</h4>
     *
     * <p>The second variant of the builder allows assigning the value of
     * the {@code target} to a destination selector:
     *
     * <pre>{@code
     * Assign.valueOf(origin).to(destination)
     * }</pre>
     *
     * <p>The value can be assigned as is, or mapped to another value
     * using the {@code as(Function)} method:
     *
     * <p>Example:
     * <pre>{@code
     * Assignment assignment = Assign.valueOf(field(Address::getCountryCode))
     *      .to(field(Address::getCountryName))
     *      .as((String countryCode) -> getCountryName(countryCode));
     * }</pre>
     *
     * @param target the selector
     * @return builder for constructing an assignment
     * @see #valueOf(GetMethodSelector)
     * @see #valueOf(Class)
     * @since 3.0.0
     */
    @ExperimentalApi
    public static ValueOf valueOf(final TargetSelector target) {
        return new InternalValueOf(target);
    }

    /**
     * Creates an assignment for a given target {@code type}.
     * This is a shorthand API of {@link #valueOf(TargetSelector)} allowing:
     *
     * <pre>{@code
     * Assign.valueOf(all(Integer.class))
     * }</pre>
     *
     * <p>to be specified as a slightly shorter version:
     *
     * <pre>{@code
     * Assign.valueOf(Integer.class)
     * }</pre>
     *
     * @param target the type of the target's value
     * @param <T>    the type of value
     * @return builder for constructing an assignment
     * @see #valueOf(TargetSelector)
     * @since 3.0.0
     */
    @ExperimentalApi
    public static <T> ValueOf valueOf(final Class<T> target) {
        return new InternalValueOf(Select.all(target));
    }

    /**
     * Creates an assignment for a given {@code target} method reference.
     * This is a shorthand API of {@link #valueOf(TargetSelector)} allowing:
     *
     * <pre>{@code
     * Assign.valueOf(field(Pojo::getValue))
     * }</pre>
     *
     * <p>to be specified as a slightly shorter version:
     *
     * <pre>{@code
     * Assign.valueOf(Pojo::getValue)
     * }</pre>
     *
     * @param target the method reference for the target field
     * @param <T>    type declaring the method
     * @param <R>    return type of the method
     * @return builder for constructing an assignment
     * @see #valueOf(TargetSelector)
     * @since 3.0.0
     */
    @ExperimentalApi
    public static <T, R> ValueOf valueOf(final GetMethodSelector<T, R> target) {
        return new InternalValueOf(Select.field(target));
    }

    /**
     * Creates a conditional assigment for a given pair of {@code origin} and
     * {@code destination} selectors. This method allows mapping predicates
     * to destination values, and also supports {@code else} semantics with
     * the following syntax:
     *
     * <pre>{@code
     * Assign.given(origin, destination)
     *     .set(originPredicate1, "value1")
     *     .set(originPredicate2, "value2")
     *     .supply(originPredicate3, () -> getValue3())
     *     .generate(originPredicate4, gen -> gen.oneOf("value4A", "value4B"))
     *     .elseSet("other-value");
     * }</pre>
     *
     * <p>A destination will be set to a given value only if the corresponding
     * predicate is satisfied. If none of the predicates match, the value from
     * the {@code else} branch will be set. Note that the {@code else} branch is
     * optional. If it is not specified, a random value will be generated
     * when none of the predicates match.
     *
     * <p>Example:
     * <pre>{@code
     * Assignment phoneCountryCode = Assign.given(field(Address::getCountry), field(Phone::getCountryCode))
     *     .set(When.isIn("Canada", "USA"), "+1")
     *     .set(When.is("Italy"), "+39")
     *     .set(When.is("Poland"), "+48")
     *     .set(When.is("Germany"), "+49")
     *     .elseSupply(() -> Assertions.fail("unexpected country"));
     *
     * Person person = Instancio.of(Person.class)
     *     .generate(field(Address::getCountry), gen -> gen.oneOf("Canada", "Germany", "Italy", "Poland", "USA"))
     *     .assign(phoneCountryCode)
     *     .create();
     * }</pre>
     *
     * <p>In the above example, the generated country names should match one
     * of the assignment predicates, therefore the assertion failure in
     * {@code elseSupply()} should not be reachable, and could be omitted.
     *
     * @param origin      selector whose target the origin predicate
     *                    will be evaluated against
     * @param destination selector whose targets will be set to a
     *                    given value if the origin predicate is satisfied
     * @return builder for constructing a conditional assignment
     * @see #given(TargetSelector)
     * @since 3.0.0
     */
    @ExperimentalApi
    public static GivenOriginDestination given(final TargetSelector origin, final TargetSelector destination) {
        ApiValidator.validateAssignmentOrigin(origin);
        return new InternalGivenOriginDestinationAction(origin, destination);
    }

    /**
     * Creates a conditional for a given {@code origin} and one or more
     * {@code destination} selectors. This method allows mapping an origin
     * to different destinations, but unlike
     * {@link #given(TargetSelector, TargetSelector)} this method does not
     * support {@code else} semantics:
     *
     * <pre>{@code
     * Assign.given(origin)
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
     * Assignment orderFields = Assign.given(Order::getStatus)
     *     .is(OrderStatus.CANCELLED)
     *     .set(field(Order::getCancellationReason), "Shipping delays")
     *     .generate(field(Order::getCancellationDate), gen -> gen.temporal().localDate().past());
     *
     * List<Order> orders = Instancio.ofList(Order.class)
     *     .size(20)
     *     .assign(orderFields)
     *     .create();
     * }</pre>
     *
     * <p>The above snippet will generate a list of random orders. If an order
     * with a {@code CANCELLED} status is generated, the order will have
     * the expected values as specified by the assignment. It is possible
     * to specify different values for other order statuses. Since the example
     * above does not specify this, for all other order statuses, random values
     * will be generated.
     *
     * @param origin selector whose target the origin predicate will be evaluated against
     * @return builder for constructing a conditional assignment
     * @see #given(TargetSelector, TargetSelector)
     * @see #given(Class)
     * @see #given(GetMethodSelector)
     * @since 3.0.0
     */
    @ExperimentalApi
    public static GivenOrigin given(final TargetSelector origin) {
        ApiValidator.validateAssignmentOrigin(origin);
        return new InternalGivenOrigin(origin);
    }

    /**
     * Creates a conditional assignment for a given {@code origin} method
     * reference and one or more {@code destination} selectors. This is
     * a shorthand API of {@link #given(TargetSelector)} allowing:
     *
     * <pre>{@code
     * Assign.given(field(Pojo::getValue))
     * }</pre>
     *
     * <p>to be specified as a slightly shorter version:
     *
     * <pre>{@code
     * Assign.given(Pojo::getValue)
     * }</pre>
     *
     * @param origin a method reference for the origin value
     * @param <T>    type declaring the method
     * @param <R>    return type of the method
     * @return builder for constructing a conditional assignment
     * @see #given(TargetSelector)
     * @since 3.0.0
     */
    @ExperimentalApi
    public static <T, R> GivenOrigin given(final GetMethodSelector<T, R> origin) {
        return new InternalGivenOrigin(Select.field(origin));
    }

    /**
     * Creates a conditional assignment for a given {@code origin} type.
     * This is a shorthand API of {@link #given(TargetSelector)} allowing:
     *
     * <pre>{@code
     * Assign.given(all(Integer.class))
     * }</pre>
     * <p>
     * to be specified as a slightly shorter version:
     *
     * <pre>{@code
     * Assign.given(Integer.class)
     * }</pre>
     *
     * @param origin type of the origin value
     * @param <T>    the type of value
     * @return builder for constructing a conditional assignment
     * @see #given(TargetSelector)
     * @since 3.0.0
     */
    @ExperimentalApi
    public static <T> GivenOrigin given(final Class<T> origin) {
        return new InternalGivenOrigin(Select.all(origin));
    }

    private Assign() {
        // non-instantiable
    }
}
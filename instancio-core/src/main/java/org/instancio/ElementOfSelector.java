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

/**
 * A selector for targeting specific elements within a generated
 * collection or array.
 *
 * <p>An {@code ElementOfSelector} is created via
 * {@link Select#elementOf(GetMethodSelector)} or
 * {@link Select#elementOf(TargetSelector)} and is built up in two optional
 * stages:
 *
 * <ul>
 *   <li>
 *     An <b>index</b> stage that narrows to specific positions using
 *     {@link #at(int)}, {@link #at(int...) at(int...)},
 *     {@link #range(int, int)}, {@link #first()},
 *     {@link #last()}, or {@link #except(int...)}.
 *     Without it, the selector targets all elements.</li>
 *   <li>
 *     An <b>element</b> stage that narrows to a component within each
 *     selected element via {@link IndexedElementSelector#field(GetMethodSelector)}
 *     or {@link IndexedElementSelector#target(TargetSelector)}.
 *     Without it, the selector targets the element as a whole.</li>
 * </ul>
 *
 * <p>Example: replace the third address with a fixed value, and override
 * the city of the first two addresses:
 *
 * <pre>{@code
 * Person person = Instancio.of(Person.class)
 *     .set(elementOf(Person::getAddresses).at(2), addressFixture)
 *     .set(elementOf(Person::getAddresses).at(0, 1).field(Address::getCity), "Barcelona")
 *     .create();
 * }</pre>
 *
 * <h2>Collection sizing</h2>
 *
 * <p>Index methods that reference a specific position
 * ({@link #at(int) at}, {@link #at(int...) at(int...)},
 * {@link #range(int, int) range}, {@link #first() first}, and
 * {@link #last() last}) widen the generated collection so that all
 * referenced indices are in range. For example, {@code at(5)} guarantees
 * the collection has at least 6 elements.
 *
 * <p>{@link #except(int...) except} does <b>not</b> widen the collection;
 * excluded indices beyond the collection size are silently ignored.
 *
 * <p>If the collection size has been explicitly capped below the required
 * minimum (e.g. via a {@code gen.collection().size(n)} generator spec),
 * an {@link org.instancio.exception.InstancioApiException InstancioApiException}
 * is thrown at generation time.
 *
 * @see Select#elementOf(GetMethodSelector)
 * @see Select#elementOf(TargetSelector)
 * @see IndexedElementSelector
 * @since 6.0.0
 */
@ExperimentalApi
public interface ElementOfSelector extends GroupableSelector {

    /**
     * Selects the element at the given index.
     *
     * <p>Widens the generated collection to at least {@code index + 1} elements.
     *
     * @param index the element index
     * @return a selector for the element at the given index
     * @since 6.0.0
     */
    @ExperimentalApi
    IndexedElementSelector at(int index);

    /**
     * Selects elements at the given indices.
     *
     * <p>Widens the generated collection to at least {@code max(indices) + 1}
     * elements.
     *
     * @param indices the element indices
     * @return a selector for the elements at the given indices
     * @since 6.0.0
     */
    @ExperimentalApi
    IndexedElementSelector at(int... indices);

    /**
     * Selects all elements <i>except</i> those at the given indices.
     *
     * <p>Unlike {@link #at(int...)}, this method does not widen the
     * generated collection. Excluded indices beyond the collection size are
     * silently ignored.
     *
     * @param indices the element indices to exclude
     * @return a selector for all elements other than those at the given indices
     * @since 6.0.0
     */
    @ExperimentalApi
    IndexedElementSelector except(int... indices);

    /**
     * Selects elements in the given inclusive range.
     *
     * <p>Widens the generated collection to at least
     * {@code endInclusive + 1} elements.
     *
     * @param startInclusive the start index (inclusive)
     * @param endInclusive   the end index (inclusive)
     * @return a selector for the elements in the given range
     * @since 6.0.0
     */
    @ExperimentalApi
    IndexedElementSelector range(int startInclusive, int endInclusive);

    /**
     * Selects the first element.
     *
     * <p>Widens the generated collection to at least one element.
     *
     * @return a selector for the first element
     * @since 6.0.0
     */
    @ExperimentalApi
    IndexedElementSelector first();

    /**
     * Selects the last element.
     *
     * <p>Widens the generated collection to at least one element.
     *
     * @return a selector for the last element
     * @since 6.0.0
     */
    @ExperimentalApi
    IndexedElementSelector last();

    /**
     * Selects a field within every element of the container.
     *
     * <p>Equivalent to omitting the index stage and applying
     * {@link IndexedElementSelector#field(GetMethodSelector)} to all
     * elements. For example:
     *
     * <pre>{@code
     * Person person = Instancio.of(Person.class)
     *     .set(elementOf(Person::getAddresses).field(Address::getCountry), "DE")
     *     .create();
     * }</pre>
     *
     * @param methodRef getter method reference for the target field
     * @param <T>       the type declaring the method
     * @param <R>       the return type of the method
     * @return a target selector for the specified field within all elements
     * @since 6.0.0
     */
    @ExperimentalApi
    <T, R> GroupableSelector field(GetMethodSelector<T, R> methodRef);

    /**
     * Targets nodes matched by the given selector within every element of
     * the container.
     *
     * <p>The inner selector is evaluated against each node inside the
     * element subtree, so selectors such as {@link Select#allStrings()}
     * or {@link Select#types()} match anywhere within the elements.
     *
     * @param selector the selector to apply within each element
     * @return a target selector for the matched nodes within all elements
     * @since 6.0.0
     */
    @ExperimentalApi
    GroupableSelector target(TargetSelector selector);
}

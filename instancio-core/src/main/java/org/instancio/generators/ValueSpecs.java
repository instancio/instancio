/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.generators;

import org.instancio.Instancio;
import org.instancio.IntervalSupplier;
import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.specs.BooleanSpec;
import org.instancio.generator.specs.ByteSpec;
import org.instancio.generator.specs.CharacterSpec;
import org.instancio.generator.specs.DoubleSpec;
import org.instancio.generator.specs.EnumSpec;
import org.instancio.generator.specs.FloatSpec;
import org.instancio.generator.specs.HashSpec;
import org.instancio.generator.specs.IntegerSpec;
import org.instancio.generator.specs.IntervalSpec;
import org.instancio.generator.specs.LongSpec;
import org.instancio.generator.specs.NumericSequenceSpec;
import org.instancio.generator.specs.OneOfArraySpec;
import org.instancio.generator.specs.OneOfCollectionSpec;
import org.instancio.generator.specs.ShortSpec;
import org.instancio.generator.specs.ShuffleSpec;
import org.instancio.generator.specs.StringSpec;
import org.instancio.generator.specs.UUIDSpec;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Defines all built-in generators that are available
 * via the {@link Instancio#gen()} method.
 *
 * @since 5.0.0
 */
public interface ValueSpecs extends CommonGeneratorSpecs {

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    BooleanSpec booleans();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    CharacterSpec chars();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    StringSpec string();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    ByteSpec bytes();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    ShortSpec shorts();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    IntegerSpec ints();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    NumericSequenceSpec<Integer> intSeq();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    LongSpec longs();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    NumericSequenceSpec<Long> longSeq();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    FloatSpec floats();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    DoubleSpec doubles();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    <E extends Enum<E>> EnumSpec<E> enumOf(Class<E> enumClass);

    /**
     * Picks a random value from the given choices.
     *
     * @param choices to choose from
     * @param <T>     element type
     * @return API builder reference
     * @since 5.0.0
     */
    @SuppressWarnings("unchecked")
    <T> OneOfArraySpec<T> oneOf(T... choices);

    /**
     * Picks a random value from the given choices.
     *
     * @param choices to choose from
     * @param <T>     element type
     * @return API builder reference
     * @since 5.0.0
     */
    <T> OneOfCollectionSpec<T> oneOf(Collection<T> choices);

    /**
     * A spec for generating intervals of various types, such as
     * numeric and temporal values.
     *
     * <p>Generating intervals requires three inputs:
     *
     * <ul>
     *   <li>The start value of the first interval</li>
     *   <li>A function to calculate the end value based on the last start value</li>
     *   <li>A function to calculate the subsequent start value based on the last end value</li>
     * </ul>
     *
     * <p>To illustrate with an example, we will generate a list of
     * vacations defined as:
     *
     * <pre>{@code
     * record Vacation(LocalDate start, LocalDate end) {}
     * }</pre>
     *
     * <p>The first vacation should start on {@code 2000-01-01}.
     * Each vacation should last between {@code 1-3} weeks.
     * There should be {@code 12-15} months between vacations.
     *
     * <pre>{@code
     * IntervalSupplier<LocalDate> intervals = Instancio.gen()
     *     .intervalStarting(LocalDate.of(2000, 1, 1))
     *     .nextStart((end, random) -> end.plusMonths(random.intRange(12, 15)))
     *     .nextEnd((start, random) -> start.plusWeeks(random.intRange(1, 3)))
     *     .get();
     * }</pre>
     *
     * <p>This method produces an {@link IntervalSupplier} containing
     * {@code start} and {@code end} methods, which return a {@link Supplier}.
     * Each call to {@code start()} and {@code end()} will produce
     * new interval values.
     *
     * <pre>{@code
     * for (int i = 0; i < 4; i++) {
     *     System.out.println(interval.start().get() + " - " + interval.end().get());
     * }
     * // Sample output:
     * // 2000-01-01 - 2000-01-15
     * // 2001-02-15 - 2001-03-08
     * // 2002-03-08 - 2002-03-15
     * // 2003-06-15 - 2003-07-06
     * }</pre>
     *
     * <p>The {@code start()} and {@code end()} can be used to populate
     * {@code Vacation} objects, for example:
     *
     * <pre>{@code
     * List<Vacation> vacations = Instancio.ofList(Vacation.class)
     *     .size(4)
     *     .supply(field(Vacation::start), intervals.start())
     *     .supply(field(Vacation::end), intervals.end())
     *     .create();
     * }</pre>
     *
     * @param startingValue the starting value of the first interval
     * @param <T>           the type of value underlying the interval
     * @return API builder reference
     * @see IntervalSupplier
     * @since 5.0.0
     */
    @ExperimentalApi
    <T> IntervalSpec<T> intervalStarting(T startingValue);

    /**
     * Creates a copy of the specified array and shuffles its elements.
     *
     * @param array containing the elements to shuffle
     * @param <T>   the element type
     * @return API builder reference
     * @since 5.0.0
     */
    @ExperimentalApi
    @SuppressWarnings("unchecked")
    <T> ShuffleSpec<T> shuffle(T... array);

    /**
     * Creates a copy of the specified collection and shuffles its elements.
     *
     * @param collection containing the elements to shuffle
     * @param <T>        the element type
     * @return API builder reference
     * @since 5.0.0
     */
    @ExperimentalApi
    <T> ShuffleSpec<T> shuffle(Collection<T> collection);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    IoSpecs io();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    MathSpecs math();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    NetSpecs net();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    NioSpecs nio();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    TemporalSpecs temporal();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    TextSpecs text();

    /**
     * Generator for {@link java.util.UUID} values.
     *
     * @since 5.0.0
     */
    UUIDSpec uuid();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    ChecksumSpecs checksum();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    IdSpecs id();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    HashSpec hash();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    FinanceSpecs finance();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    SpatialSpecs spatial();
}

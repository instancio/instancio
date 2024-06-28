/*
 * Copyright 2022-2024 the original author or authors.
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

import org.instancio.GeneratorSpecProvider;
import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.specs.ArrayGeneratorSpec;
import org.instancio.generator.specs.BooleanGeneratorSpec;
import org.instancio.generator.specs.CharacterGeneratorSpec;
import org.instancio.generator.specs.CollectionGeneratorSpec;
import org.instancio.generator.specs.EmitGeneratorSpec;
import org.instancio.generator.specs.EnumGeneratorSpec;
import org.instancio.generator.specs.EnumSetGeneratorSpec;
import org.instancio.generator.specs.HashGeneratorSpec;
import org.instancio.generator.specs.MapGeneratorSpec;
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.generator.specs.NumericSequenceGeneratorSpec;
import org.instancio.generator.specs.OneOfArrayGeneratorSpec;
import org.instancio.generator.specs.OneOfCollectionGeneratorSpec;
import org.instancio.generator.specs.OptionalGeneratorSpec;
import org.instancio.generator.specs.StringGeneratorSpec;

import java.util.Collection;

/**
 * Defines all built-in generators available via the
 * {@link InstancioApi#generate(TargetSelector, GeneratorSpecProvider)}.
 *
 * @since 1.0.1
 */
public interface Generators extends CommonGeneratorSpecs {

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1
     */
    @Override
    StringGeneratorSpec string();

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
     */
    @Override
    BooleanGeneratorSpec booleans();

    /**
     * {@inheritDoc}
     *
     * @since 2.0.0
     */
    @Override
    CharacterGeneratorSpec chars();

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1
     */
    @Override
    NumberGeneratorSpec<Byte> bytes();

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1
     */
    @Override
    NumberGeneratorSpec<Short> shorts();

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1
     */
    @Override
    NumberGeneratorSpec<Integer> ints();

    /**
     * {@inheritDoc}
     *
     * @since 2.13.0
     */
    @Override
    NumericSequenceGeneratorSpec<Integer> intSeq();

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1
     */
    @Override
    NumberGeneratorSpec<Long> longs();

    /**
     * {@inheritDoc}
     *
     * @since 2.13.0
     */
    @Override
    NumericSequenceGeneratorSpec<Long> longSeq();

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1
     */
    @Override
    NumberGeneratorSpec<Float> floats();

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1
     */
    @Override
    NumberGeneratorSpec<Double> doubles();

    /**
     * {@inheritDoc}
     */
    @Override
    <E extends Enum<E>> EnumGeneratorSpec<E> enumOf(Class<E> enumClass);

    /**
     * {@inheritDoc}
     */
    @Override
    MathGenerators math();

    /**
     * {@inheritDoc}
     *
     * @since 2.3.0
     */
    @Override
    NetGenerators net();

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1
     */
    @Override
    @SuppressWarnings("unchecked")
    <T> OneOfArrayGeneratorSpec<T> oneOf(T... choices);

    /**
     * {@inheritDoc}
     *
     * @since 1.0.1
     */
    @Override
    <T> OneOfCollectionGeneratorSpec<T> oneOf(Collection<T> choices);

    /**
     * Generator for {@code Optional}.
     *
     * @param <T> the type of value
     * @return API builder reference
     * @since 2.14.0
     */
    <T> OptionalGeneratorSpec<T> optional();

    /**
     * Generator for arrays.
     *
     * @param <T> array component type
     * @return API builder reference
     * @since 1.0.1
     */
    <T> ArrayGeneratorSpec<T> array();

    /**
     * Generator for collections.
     *
     * @param <T> element type
     * @return API builder reference
     * @since 1.0.1
     */
    <T> CollectionGeneratorSpec<T> collection();

    /**
     * Generator for maps.
     *
     * @param <K> key type
     * @param <V> value type
     * @return API builder reference
     * @since 1.0.1
     */
    <K, V> MapGeneratorSpec<K, V> map();

    /**
     * Generator for {@link java.util.EnumSet}.
     *
     * @param enumClass contained by the generated enum set
     * @param <E>       enum type
     * @return API builder reference
     * @since 2.0.0
     */
    <E extends Enum<E>> EnumSetGeneratorSpec<E> enumSet(Class<E> enumClass);

    /**
     * Emits provided items to a selector's target.
     * The main use case for this generator us to customise fields
     * of collection elements.
     *
     * <p>For example, the snippet below generates a list of 7 orders with
     * different statuses: 1 received, 1 shipped, 3 completed, and 2 cancelled.
     *
     * <pre>{@code
     *   List<Order> orders = Instancio.ofList(Order.class)
     *     .size(7)
     *     .generate(field(Order::getStatus), gen -> gen.emit()
     *              .items(OrderStatus.RECEIVED, OrderStatus.SHIPPED)
     *              .item(OrderStatus.COMPLETED, 3)
     *              .item(OrderStatus.CANCELLED, 2))
     *     .create();
     * }</pre>
     *
     * <p>Note that the number of items being emitted is equal to the
     * collection size. If there are not enough items to emit for
     * a given collection size, then by default, random values will be
     * generated. This behaviour can be customised using the following methods:
     *
     * <ul>
     *  <li>{@link EmitGeneratorSpec#whenEmptyEmitNull()}</li>
     *  <li>{@link EmitGeneratorSpec#whenEmptyEmitRandom()} (default behaviour)</li>
     *  <li>{@link EmitGeneratorSpec#whenEmptyRecycle()} (reuse items)</li>
     *  <li>{@link EmitGeneratorSpec#whenEmptyThrowException()}</li>
     * </ul>
     *
     * <p>If the selector target is a group, then each selector will
     * have its own copy of items, for example:
     *
     * <pre>{@code
     * // Given
     * record FooBar(String foo, String bar) {}
     *
     * TargetSelector selectorGroup = Select.all(
     *     field(FooBar::foo),
     *     field(FooBar::bar)
     * );
     *
     * // When
     * FooBar result = Instancio.of(FooBar.class)
     *     .generate(selectorGroup, gen -> gen.emit().items("BAZ"))
     *     .create();
     *
     * // Output: FooBar[foo=BAZ, bar=BAZ]
     * }</pre>
     *
     * <p><b>Warning:</b> special care must be taken to ensure that the
     * selector associated with {@code emit()} matches only the expected target
     * and nothing else. Failure to do so may produce unexpected results.
     *
     * @param <T> the type to emit
     * @return emitting generator
     * @since 2.12.0
     */
    @ExperimentalApi
    <T> EmitGeneratorSpec<T> emit();

    /**
     * Provides access to atomic generators.
     *
     * @return API builder reference
     */
    AtomicGenerators atomic();

    /**
     * {@inheritDoc}
     *
     * @since 2.16.0
     */
    @Override
    ChecksumGenerators checksum();

    /**
     * {@inheritDoc}
     *
     * @since 2.2.0
     */
    @Override
    IoGenerators io();

    /**
     * {@inheritDoc}
     *
     * @since 2.1.0
     */
    @Override
    NioGenerators nio();

    /**
     * {@inheritDoc}
     */
    @Override
    TemporalGenerators temporal();

    /**
     * {@inheritDoc}
     */
    @Override
    TextGenerators text();

    /**
     * {@inheritDoc}
     *
     * @since 2.11.0
     */
    @Override
    IdGenerators id();

    /**
     * {@inheritDoc}
     *
     * @since 2.11.0
     */
    @Override
    HashGeneratorSpec hash();

    /**
     * {@inheritDoc}
     *
     * @since 2.11.0
     */
    @Override
    FinanceGenerators finance();

    /**
     * {@inheritDoc}
     *
     * @since 4.4.0
     */
    @Override
    SpatialGenerators spatial();

}

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

import org.instancio.Instancio;
import org.instancio.generator.specs.BooleanSpec;
import org.instancio.generator.specs.ByteSpec;
import org.instancio.generator.specs.CharacterSpec;
import org.instancio.generator.specs.DoubleSpec;
import org.instancio.generator.specs.EnumSpec;
import org.instancio.generator.specs.FloatSpec;
import org.instancio.generator.specs.HashSpec;
import org.instancio.generator.specs.IntegerSpec;
import org.instancio.generator.specs.LongSpec;
import org.instancio.generator.specs.NumericSequenceSpec;
import org.instancio.generator.specs.OneOfArraySpec;
import org.instancio.generator.specs.OneOfCollectionSpec;
import org.instancio.generator.specs.ShortSpec;
import org.instancio.generator.specs.StringSpec;
import org.instancio.generator.specs.UUIDSpec;

import java.util.Collection;

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
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    @SuppressWarnings("unchecked")
    <T> OneOfArraySpec<T> oneOf(T... choices);

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    <T> OneOfCollectionSpec<T> oneOf(Collection<T> choices);

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

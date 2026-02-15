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
package org.instancio.generators;

import org.instancio.GeneratorSpecProvider;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.generator.specs.BooleanGeneratorSpec;
import org.instancio.generator.specs.CharacterGeneratorSpec;
import org.instancio.generator.specs.EnumGeneratorSpec;
import org.instancio.generator.specs.HashGeneratorSpec;
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.generator.specs.NumericSequenceGeneratorSpec;
import org.instancio.generator.specs.StringGeneratorSpec;

/**
 * Defines specs that are available via both:
 *
 * <ul>
 *   <li>{@link InstancioApi#generate(TargetSelector, GeneratorSpecProvider)}</li>
 *   <li>{@link Instancio#gen()}</li>
 * </ul>
 *
 * @since 5.0.0
 */
public interface CommonGeneratorSpecs {

    /**
     * Generator for {@link String} values.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    StringGeneratorSpec string();

    /**
     * Generator for {@link Boolean} values.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    BooleanGeneratorSpec booleans();

    /**
     * Generator for {@link Character} values.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    CharacterGeneratorSpec chars();

    /**
     * Generator for {@link Byte} values.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    NumberGeneratorSpec<Byte> bytes();

    /**
     * Generator for {@link Short} values.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    NumberGeneratorSpec<Short> shorts();

    /**
     * Generator for {@link Integer} values.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    NumberGeneratorSpec<Integer> ints();

    /**
     * An {@code Integer} sequence generator.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    NumericSequenceGeneratorSpec<Integer> intSeq();

    /**
     * Generator for {@link Long} values.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    NumberGeneratorSpec<Long> longs();

    /**
     * A {@code Long} sequence generator.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    NumericSequenceGeneratorSpec<Long> longSeq();

    /**
     * Generator for {@link Float} values.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    NumberGeneratorSpec<Float> floats();

    /**
     * Generator for {@link Double} values.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    NumberGeneratorSpec<Double> doubles();

    /**
     * Generator for enum values.
     *
     * @param enumClass type of enum to generate
     * @param <E>       enum type
     * @return API builder reference
     * @since 5.0.0
     */
    <E extends Enum<E>> EnumGeneratorSpec<E> enumOf(Class<E> enumClass);

    /**
     * Provides generators for {@code java.math} classes.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    MathGenerators math();

    /**
     * Provides generators for {@code java.net} classes.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    NetGenerators net();

    /**
     * Provides access to checksum generators.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    ChecksumGenerators checksum();

    /**
     * Provides access to IO generators.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    IoGenerators io();

    /**
     * Provides access to NIO generators.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    NioGenerators nio();

    /**
     * Provides access to temporal generators.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    TemporalGenerators temporal();

    /**
     * Provides access to text generators.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    TextGenerators text();

    /**
     * Provides access to identifier generators.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    IdGenerators id();

    /**
     * Generator for various types of hashes.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    HashGeneratorSpec hash();

    /**
     * Provides access to finance-related generators.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    FinanceGenerators finance();

    /**
     * Provides access to spatial data type related generators.
     *
     * @return API builder reference
     * @since 5.0.0
     */
    SpatialGenerators spatial();

}

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
package org.instancio;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.ValueSpec;
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
import org.instancio.generator.specs.ShortSpec;
import org.instancio.generator.specs.StringSpec;
import org.instancio.generator.specs.UUIDSpec;
import org.instancio.generators.ChecksumSpecs;
import org.instancio.generators.FinanceSpecs;
import org.instancio.generators.IdSpecs;
import org.instancio.generators.IoSpecs;
import org.instancio.generators.MathSpecs;
import org.instancio.generators.NetSpecs;
import org.instancio.generators.NioSpecs;
import org.instancio.generators.TemporalSpecs;
import org.instancio.generators.TextSpecs;
import org.instancio.internal.generator.array.OneOfArrayGenerator;
import org.instancio.internal.generator.domain.hash.HashGenerator;
import org.instancio.internal.generator.lang.BooleanGenerator;
import org.instancio.internal.generator.lang.ByteGenerator;
import org.instancio.internal.generator.lang.CharacterGenerator;
import org.instancio.internal.generator.lang.DoubleGenerator;
import org.instancio.internal.generator.lang.EnumGenerator;
import org.instancio.internal.generator.lang.FloatGenerator;
import org.instancio.internal.generator.lang.IntegerGenerator;
import org.instancio.internal.generator.lang.LongGenerator;
import org.instancio.internal.generator.lang.ShortGenerator;
import org.instancio.internal.generator.lang.StringGenerator;
import org.instancio.internal.generator.sequence.IntegerSequenceGenerator;
import org.instancio.internal.generator.sequence.LongSequenceGenerator;
import org.instancio.internal.generator.util.OneOfCollectionGenerator;
import org.instancio.internal.generator.util.UUIDGenerator;

import java.util.Collection;

/**
 * Class for generating simple value types, such as strings, numbers,
 * dates, and so on.
 *
 * <p>Examples:
 * <pre>{@code
 *   String s = Gen.string().length(10).digits().get();
 *   Integer i = Gen.ints().range(0, 100).get();
 *   List<LocalDate> dates = Gen.temporal().localDate().past().list(10);
 * }</pre>
 *
 * @since 2.6.0
 */
@ExperimentalApi
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.ExcessiveImports"})
public final class Gen {

    /**
     * Generates {@link Boolean} values.
     *
     * @return value spec builder
     * @since 2.6.0
     */
    public static BooleanSpec booleans() {
        return new BooleanGenerator();
    }

    /**
     * Generates {@link Character} values.
     *
     * @return value spec builder
     * @since 2.6.0
     */
    public static CharacterSpec chars() {
        return new CharacterGenerator();
    }

    /**
     * Generates {@link String} values.
     *
     * @return value spec builder
     * @since 2.6.0
     */
    public static StringSpec string() {
        return new StringGenerator();
    }

    /**
     * Generates {@link Byte} values.
     *
     * @return value spec builder
     * @since 2.6.0
     */
    public static ByteSpec bytes() {
        return new ByteGenerator();
    }

    /**
     * Generates {@link Short} values.
     *
     * @return value spec builder
     * @since 2.6.0
     */
    public static ShortSpec shorts() {
        return new ShortGenerator();
    }

    /**
     * Generates {@link Integer} values.
     *
     * @return value spec builder
     * @since 2.6.0
     */
    public static IntegerSpec ints() {
        return new IntegerGenerator();
    }

    /**
     * Generates {@link Integer} sequences.
     *
     * @return value spec builder
     * @since 2.13.0
     */
    @ExperimentalApi
    public static NumericSequenceSpec<Integer> intSeq() {
        return new IntegerSequenceGenerator();
    }

    /**
     * Generates {@link Long} values.
     *
     * @return value spec builder
     * @since 2.6.0
     */
    public static LongSpec longs() {
        return new LongGenerator();
    }

    /**
     * Generates {@link Long} sequences.
     *
     * @return value spec builder
     * @since 2.13.0
     */
    @ExperimentalApi
    public static NumericSequenceSpec<Long> longSeq() {
        return new LongSequenceGenerator();
    }

    /**
     * Generates {@link Float} values.
     *
     * @return value spec builder
     * @since 2.6.0
     */
    public static FloatSpec floats() {
        return new FloatGenerator();
    }

    /**
     * Generates {@link Double} values.
     *
     * @return value spec builder
     * @since 2.6.0
     */
    public static DoubleSpec doubles() {
        return new DoubleGenerator();
    }

    /**
     * Generates enum values.
     *
     * @param enumClass the enum class
     * @param <E>       enum type
     * @return value spec builder
     * @since 2.12.0
     */
    public static <E extends Enum<E>> EnumSpec<E> enumOf(final Class<E> enumClass) {
        return new EnumGenerator<>(enumClass);
    }

    /**
     * Picks a random value from the given choices.
     *
     * @param choices to choose from
     * @param <T>     element type
     * @return value spec builder
     * @since 2.6.0
     */
    @SafeVarargs
    public static <T> ValueSpec<T> oneOf(final T... choices) {
        return new OneOfArrayGenerator<T>().oneOf(choices);
    }

    /**
     * Picks a random value from the given choices.
     *
     * @param choices to choose from
     * @param <T>     element type
     * @return value spec builder
     * @since 2.6.0
     */
    public static <T> ValueSpec<T> oneOf(final Collection<T> choices) {
        return new OneOfCollectionGenerator<T>().oneOf(choices);
    }

    /**
     * Provides generators for {@code java.io} classes.
     *
     * @return built-in generators for {@code java.io} classes.
     * @since 2.6.0
     */
    public static IoSpecs io() {
        return new IoSpecs();
    }

    /**
     * Provides generators for {@code java.math} classes.
     *
     * @return built-in generators for {@code java.math} classes.
     * @since 2.6.0
     */
    public static MathSpecs math() {
        return new MathSpecs();
    }

    /**
     * Provides generators for {@code java.net} classes.
     *
     * @return built-in generators for {@code java.net} classes.
     * @since 2.6.0
     */
    public static NetSpecs net() {
        return new NetSpecs();
    }

    /**
     * Provides generators for {@code java.nio} classes.
     *
     * @return built-in generators for {@code java.nio} classes.
     * @since 2.6.0
     */
    public static NioSpecs nio() {
        return new NioSpecs();
    }

    /**
     * Provides generators for {@code java.time} classes.
     *
     * @return built-in generators for {@code java.time} classes.
     * @since 2.6.0
     */
    public static TemporalSpecs temporal() {
        return new TemporalSpecs();
    }

    /**
     * Provides text generators.
     *
     * @return built-in text generators
     * @since 2.6.0
     */
    public static TextSpecs text() {
        return new TextSpecs();
    }

    /**
     * Generates {@code UUID} values.
     *
     * @return value spec builder
     * @since 3.2.0
     */
    public static UUIDSpec uuid() {
        return new UUIDGenerator();
    }

    /**
     * Provides generators for checksum-valid numbers.
     *
     * @return built-in checksum generators
     * @since 2.16.0
     */
    public static ChecksumSpecs checksum() {
        return new ChecksumSpecs();
    }

    /**
     * Provides identifier generators.
     *
     * @return built-in identifier generators
     * @since 2.11.0
     */
    public static IdSpecs id() {
        return new IdSpecs();
    }

    /**
     * Generates various types of hashes.
     *
     * @return API builder reference
     * @since 2.11.0
     */
    public static HashSpec hash() {
        return new HashGenerator();
    }

    /**
     * Provides finance-related generators.
     *
     * @return built-in finance-related generators
     * @since 2.11.0
     */
    public static FinanceSpecs finance() {
        return new FinanceSpecs();
    }

    private Gen() {
        // non-instantiable
    }
}

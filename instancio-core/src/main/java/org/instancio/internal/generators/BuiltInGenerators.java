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
package org.instancio.internal.generators;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.ArrayGeneratorSpec;
import org.instancio.generator.specs.BooleanSpec;
import org.instancio.generator.specs.ByteSpec;
import org.instancio.generator.specs.CharacterSpec;
import org.instancio.generator.specs.CollectionGeneratorSpec;
import org.instancio.generator.specs.DoubleSpec;
import org.instancio.generator.specs.EmitGeneratorSpec;
import org.instancio.generator.specs.EnumSetGeneratorSpec;
import org.instancio.generator.specs.EnumSpec;
import org.instancio.generator.specs.FloatSpec;
import org.instancio.generator.specs.HashSpec;
import org.instancio.generator.specs.IntegerSpec;
import org.instancio.generator.specs.IntervalSpec;
import org.instancio.generator.specs.LongSpec;
import org.instancio.generator.specs.MapGeneratorSpec;
import org.instancio.generator.specs.NumericSequenceSpec;
import org.instancio.generator.specs.OneOfArraySpec;
import org.instancio.generator.specs.OneOfCollectionSpec;
import org.instancio.generator.specs.OptionalGeneratorSpec;
import org.instancio.generator.specs.ShortSpec;
import org.instancio.generator.specs.ShuffleSpec;
import org.instancio.generator.specs.StringSpec;
import org.instancio.generator.specs.UUIDSpec;
import org.instancio.generators.AtomicGenerators;
import org.instancio.generators.ChecksumSpecs;
import org.instancio.generators.FinanceSpecs;
import org.instancio.generators.Generators;
import org.instancio.generators.IdSpecs;
import org.instancio.generators.IoSpecs;
import org.instancio.generators.MathSpecs;
import org.instancio.generators.NetSpecs;
import org.instancio.generators.NioSpecs;
import org.instancio.generators.SpatialSpecs;
import org.instancio.generators.TemporalSpecs;
import org.instancio.generators.TextSpecs;
import org.instancio.generators.ValueSpecs;
import org.instancio.internal.generator.array.ArrayGenerator;
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
import org.instancio.internal.generator.misc.EmitGenerator;
import org.instancio.internal.generator.misc.IntervalGenerator;
import org.instancio.internal.generator.sequence.IntegerSequenceGenerator;
import org.instancio.internal.generator.sequence.LongSequenceGenerator;
import org.instancio.internal.generator.shuffle.ShuffleGenerator;
import org.instancio.internal.generator.util.CollectionGeneratorSpecImpl;
import org.instancio.internal.generator.util.EnumSetGenerator;
import org.instancio.internal.generator.util.MapGeneratorSpecImpl;
import org.instancio.internal.generator.util.OneOfCollectionGenerator;
import org.instancio.internal.generator.util.OptionalGenerator;
import org.instancio.internal.generator.util.UUIDGenerator;

import java.util.Collection;

@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.ExcessiveImports"})
public final class BuiltInGenerators implements Generators, ValueSpecs {

    private final GeneratorContext context;

    public BuiltInGenerators(final GeneratorContext context) {
        this.context = context;
    }

    @Override
    public StringSpec string() {
        return new StringGenerator(context);
    }

    @Override
    public BooleanSpec booleans() {
        return new BooleanGenerator(context);
    }

    @Override
    public CharacterSpec chars() {
        return new CharacterGenerator(context);
    }

    @Override
    public ByteSpec bytes() {
        return new ByteGenerator(context);
    }

    @Override
    public ShortSpec shorts() {
        return new ShortGenerator(context);
    }

    @Override
    public IntegerSpec ints() {
        return new IntegerGenerator(context);
    }

    @Override
    public NumericSequenceSpec<Integer> intSeq() {
        return new IntegerSequenceGenerator(context);
    }

    @Override
    public LongSpec longs() {
        return new LongGenerator(context);
    }

    @Override
    public NumericSequenceSpec<Long> longSeq() {
        return new LongSequenceGenerator(context);
    }

    @Override
    public FloatSpec floats() {
        return new FloatGenerator(context);
    }

    @Override
    public DoubleSpec doubles() {
        return new DoubleGenerator(context);
    }

    @Override
    public <E extends Enum<E>> EnumSpec<E> enumOf(final Class<E> enumClass) {
        return new EnumGenerator<>(context, enumClass);
    }

    @Override
    public MathSpecs math() {
        return new MathSpecsImpl(context);
    }

    @Override
    public NetSpecs net() {
        return new NetSpecsImpl(context);
    }

    @SafeVarargs
    @Override
    public final <T> OneOfArraySpec<T> oneOf(T... choices) { // NOPMD
        return new OneOfArrayGenerator<T>(context).oneOf(choices);
    }

    @Override
    public <T> OneOfCollectionSpec<T> oneOf(Collection<T> choices) {
        return new OneOfCollectionGenerator<T>(context).oneOf(choices);
    }

    @Override
    @SafeVarargs
    public final <T> ShuffleSpec<T> shuffle(final T... array) {
        // Available only via Instancio.gen()
        return new ShuffleGenerator<T>(context).shuffle(array);
    }

    @Override
    public <T> ShuffleSpec<T> shuffle(final Collection<T> collection) {
        // Available only via Instancio.gen()
        return new ShuffleGenerator<T>(context).shuffle(collection);
    }

    @Override
    public <T> OptionalGeneratorSpec<T> optional() {
        return new OptionalGenerator<>(context);
    }

    @Override
    public <T> ArrayGeneratorSpec<T> array() {
        return new ArrayGenerator<>(context);
    }

    @Override
    public <T> CollectionGeneratorSpec<T> collection() {
        return new CollectionGeneratorSpecImpl<>(context);
    }

    @Override
    public <E extends Enum<E>> EnumSetGeneratorSpec<E> enumSet(final Class<E> enumClass) {
        return new EnumSetGenerator<>(context, enumClass);
    }

    @Override
    public <K, V> MapGeneratorSpec<K, V> map() {
        return new MapGeneratorSpecImpl<>(context);
    }

    @Override
    public <T> EmitGeneratorSpec<T> emit() {
        return new EmitGenerator<>(context);
    }

    @Override
    public <T> IntervalSpec<T> intervalStarting(final T startingValue) {
        return new IntervalGenerator<>(context, startingValue);
    }

    // AtomicSpecs not available (yet)
    @Override
    public AtomicGenerators atomic() {
        return new AtomicSpecsImpl(context);
    }

    @Override
    public ChecksumSpecs checksum() {
        return new ChecksumSpecsImpl(context);
    }

    @Override
    public IoSpecs io() {
        return new IoSpecsImpl(context);
    }

    @Override
    public NioSpecs nio() {
        return new NioSpecsImpl(context);
    }

    @Override
    public TemporalSpecs temporal() {
        return new TemporalSpecsImpl(context);
    }

    @Override
    public TextSpecs text() {
        return new TextSpecsImpl(context);
    }

    @Override
    public IdSpecs id() {
        return new IdSpecsImpl(context);
    }

    @Override
    public HashSpec hash() {
        return new HashGenerator(context);
    }

    @Override
    public FinanceSpecs finance() {
        return new FinanceSpecsImpl(context);
    }

    @Override
    public SpatialSpecs spatial() {
        return new SpatialSpecsImpl(context);
    }

    @Override
    public UUIDSpec uuid() {
        // not exposed via Generators interface since there's nothing to customise
        return new UUIDGenerator(context);
    }
}

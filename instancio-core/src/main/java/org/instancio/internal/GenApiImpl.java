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
package org.instancio.internal;

import org.instancio.InstancioGenApi;
import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
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
import org.instancio.generators.ChecksumSpecs;
import org.instancio.generators.FinanceSpecs;
import org.instancio.generators.IdSpecs;
import org.instancio.generators.IoSpecs;
import org.instancio.generators.MathSpecs;
import org.instancio.generators.NetSpecs;
import org.instancio.generators.NioSpecs;
import org.instancio.generators.SpatialSpecs;
import org.instancio.generators.TemporalSpecs;
import org.instancio.generators.TextSpecs;
import org.instancio.internal.generators.BuiltInGenerators;
import org.instancio.settings.Keys;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;
import org.instancio.support.Global;
import org.instancio.support.ThreadLocalSettings;

import java.util.Collection;

@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.ExcessiveImports"})
public final class GenApiImpl implements InstancioGenApi {

    private Settings settings = Global.getPropertiesFileSettings()
            .merge(ThreadLocalSettings.getInstance().get());

    @Override
    public InstancioGenApi withSettings(final Settings settings) {
        ApiValidator.notNull(settings, "'settings' must not be null");
        this.settings = this.settings.merge(settings);
        return this;
    }

    @Override
    public <V> InstancioGenApi withSetting(final SettingKey<V> key, final V value) {
        settings.set(key, value);
        return this;
    }

    private BuiltInGenerators generators() {
        final Random random = RandomHelper.resolveRandom(settings.get(Keys.SEED), null);
        return new BuiltInGenerators(new GeneratorContext(settings, random));
    }

    @Override
    public BooleanSpec booleans() {
        return generators().booleans();
    }

    @Override
    public CharacterSpec chars() {
        return generators().chars();
    }

    @Override
    public StringSpec string() {
        return generators().string();
    }

    @Override
    public ByteSpec bytes() {
        return generators().bytes();
    }

    @Override
    public ShortSpec shorts() {
        return generators().shorts();
    }

    @Override
    public IntegerSpec ints() {
        return generators().ints();
    }

    @Override
    public NumericSequenceSpec<Integer> intSeq() {
        return generators().intSeq();
    }

    @Override
    public LongSpec longs() {
        return generators().longs();
    }

    @Override
    public NumericSequenceSpec<Long> longSeq() {
        return generators().longSeq();
    }

    @Override
    public FloatSpec floats() {
        return generators().floats();
    }

    @Override
    public DoubleSpec doubles() {
        return generators().doubles();
    }

    @Override
    public <E extends Enum<E>> EnumSpec<E> enumOf(final Class<E> enumClass) {
        return generators().enumOf(enumClass);
    }

    @SafeVarargs
    @Override
    public final <T> OneOfArraySpec<T> oneOf(final T... choices) {
        return generators().oneOf(choices);
    }

    @Override
    public <T> OneOfCollectionSpec<T> oneOf(final Collection<T> choices) {
        return generators().oneOf(choices);
    }

    @Override
    public IoSpecs io() {
        return generators().io();
    }

    @Override
    public MathSpecs math() {
        return generators().math();
    }

    @Override
    public NetSpecs net() {
        return generators().net();
    }

    @Override
    public NioSpecs nio() {
        return generators().nio();
    }

    @Override
    public TemporalSpecs temporal() {
        return generators().temporal();
    }

    @Override
    public TextSpecs text() {
        return generators().text();
    }

    @Override
    public UUIDSpec uuid() {
        return generators().uuid();
    }

    @Override
    public ChecksumSpecs checksum() {
        return generators().checksum();
    }

    @Override
    public IdSpecs id() {
        return generators().id();
    }

    @Override
    public HashSpec hash() {
        return generators().hash();
    }

    @Override
    public FinanceSpecs finance() {
        return generators().finance();
    }

    @Override
    public SpatialSpecs spatial() {
        return generators().spatial();
    }
}

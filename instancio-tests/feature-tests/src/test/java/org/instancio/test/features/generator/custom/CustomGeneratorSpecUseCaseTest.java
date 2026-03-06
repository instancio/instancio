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
package org.instancio.test.features.generator.custom;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.documentation.Initializer;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.Hints;
import org.instancio.internal.util.ObjectUtils;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

/**
 * Use case: support user-defined generator specs.
 *
 * <p>This test is a POC demonstrating a custom generator spec similar
 * to the built-in specs. The custom spec supports randomised initial
 * state and allows overriding generation parameters via spec methods.
 */
@FeatureTag({Feature.GENERATOR, Feature.SETTINGS})
@ExtendWith(InstancioExtension.class)
class CustomGeneratorSpecUseCaseTest {

    /**
     * Custom spec for a list of integers. Supports creating a list
     * of a certain size comprised of either positive or negative integers.
     */
    private interface IntegerListSpec extends GeneratorSpec<List<Integer>> {

        IntegerListSpec positive(int size);

        IntegerListSpec negative(int size);
    }

    private static final SettingKey<Integer> KEY_MIN_SIZE = Keys.ofType(Integer.class).create();
    private static final SettingKey<Integer> KEY_MAX_SIZE = Keys.ofType(Integer.class).create();

    private static class IntegerListSpecImpl implements IntegerListSpec, Generator<List<Integer>> {

        private Integer minSize;
        private Integer maxSize;
        private boolean isPositive = true;

        private Random random;
        private Settings settings;

        @Initializer
        @Override
        @SuppressWarnings("NullAway")
        public void init(final @NonNull GeneratorContext context) {
            random = context.random();
            settings = context.getSettings();

            // User may have invoked positive() or negative() method,
            // which would initialise the size. If that's the case,
            // the size should not be overwritten with Setting values.
            minSize = ObjectUtils.defaultIfNull(minSize, settings.get(KEY_MIN_SIZE));
            maxSize = ObjectUtils.defaultIfNull(maxSize, settings.get(KEY_MAX_SIZE));
        }

        @Override
        public List<Integer> generate(final @NonNull Random random) {
            final int size = random.intRange(minSize, maxSize);
            final List<Integer> list = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                final int n = isPositive
                        ? random.intRange(1, 100)
                        : random.intRange(-100, -1);

                list.add(n);
            }
            return list;
        }

        @Override
        public IntegerListSpec positive(final int size) {
            this.minSize = size;
            this.maxSize = size;
            this.isPositive = true;
            return this;
        }

        @Override
        public IntegerListSpec negative(final int size) {
            this.minSize = size;
            this.maxSize = size;
            this.isPositive = false;
            return this;
        }

        @Nullable
        @Override
        public Hints hints() {
            // Ensure Random and Settings are not null
            // when hints() is invoked by the engine
            // (i.e. init() must be called before hints())
            assertThat(random).isNotNull();
            assertThat(settings).isNotNull();

            return null; // no hints required
        }
    }

    // Used via reflection
    @SuppressWarnings("unused")
    private static final class ListHolder {
        private @Nullable List<Integer> list1;
        private @Nullable List<Integer> list2;
    }

    private static IntegerListSpec integerList() {
        return new IntegerListSpecImpl();
    }

    @WithSettings
    private final Settings settings = Settings.create()
            .set(KEY_MIN_SIZE, Constants.MIN_SIZE)
            .set(KEY_MAX_SIZE, Constants.MAX_SIZE)
            .lock();

    @Test
    void customGenerator() {
        final ListHolder result = Instancio.of(ListHolder.class)
                .generate(all(List.class), integerList())
                .create();

        final int expectedMin = settings.get(KEY_MIN_SIZE);
        final int expectedMax = settings.get(KEY_MAX_SIZE);

        assertThat(result.list1).hasSizeBetween(expectedMin, expectedMax);
        assertThat(result.list2).hasSizeBetween(expectedMin, expectedMax);
    }

    @Test
    void withSettingsOverride() {
        final int size = 10;
        final ListHolder result = Instancio.of(ListHolder.class)
                .generate(all(List.class), integerList())
                .withSettings(Settings.create()
                        .set(KEY_MIN_SIZE, size)
                        .set(KEY_MAX_SIZE, size))
                .create();

        assertThat(result.list1)
                .hasSameSizeAs(result.list2)
                .hasSize(size);
    }

    @Test
    void customiseValuesViaSpecMethods() {
        final ListHolder result = Instancio.of(ListHolder.class)
                .generate(field("list1"), integerList().positive(1))
                .generate(field("list2"), integerList().negative(2))
                .create();

        assertThat(result.list1).hasSize(1).allMatch(n -> n > 0);
        assertThat(result.list2).hasSize(2).allMatch(n -> n < 0);
    }
}

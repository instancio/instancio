/*
 * Copyright 2022-2023 the original author or authors.
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
import org.instancio.Model;
import org.instancio.Random;
import org.instancio.TypeToken;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.LongHolderWithDefaults;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allLongs;
import static org.instancio.Select.field;
import static org.instancio.test.support.pojo.basic.LongHolderWithDefaults.PRIMITIVE;
import static org.instancio.test.support.pojo.basic.LongHolderWithDefaults.WRAPPER;

/**
 * Tests for POJO with initialised fields.
 * <p>
 * Verifies OVERWRITE_EXISTING_VALUES enabled|disabled
 * with all AfterGenerate values + ignored() fields.
 */
@FeatureTag({
        Feature.GENERATOR,
        Feature.IGNORE,
        Feature.AFTER_GENERATE,
        Feature.OVERWRITE_EXISTING_VALUES,
        Feature.SET
})
class CustomGeneratorWithInitialisedFieldsTest {

    private static final long SELECTOR_OVERRIDE = -12345L;

    private static final Settings OVERWRITES_DISABLED = Settings.create()
            .set(Keys.OVERWRITE_EXISTING_VALUES, false);

    private static Generator<?> generator(final AfterGenerate afterGenerate) {
        return new Generator<Object>() {
            @Override
            public Object generate(final Random random) {
                return new LongHolderWithDefaults();
            }

            @Override
            public Hints hints() {
                return Hints.afterGenerate(afterGenerate);
            }
        };
    }

    private static Model<Item<LongHolderWithDefaults>> baseModel(final AfterGenerate afterGenerate) {
        return Instancio.of(new TypeToken<Item<LongHolderWithDefaults>>() {})
                .supply(all(LongHolderWithDefaults.class), generator(afterGenerate))
                .toModel();
    }

    @Nested
    class WithOverwriteExistingValuesEnabledTest {

        @EnumSource(
                value = AfterGenerate.class,
                names = {"DO_NOT_MODIFY", "APPLY_SELECTORS", "POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
        @ParameterizedTest
        void initialisedFieldShouldNotBeModified(final AfterGenerate afterGenerate) {
            assertInitialisedFieldsNotModified(Instancio.create(baseModel(afterGenerate)));
        }

        @EnumSource(
                value = AfterGenerate.class,
                names = "POPULATE_ALL")
        @ParameterizedTest
        void initialisedFieldShouldBeOverwritten(final AfterGenerate afterGenerate) {
            assertInitialisedFieldsOverwritten(Instancio.create(baseModel(afterGenerate)));
        }

        @EnumSource(
                value = AfterGenerate.class,
                names = {"APPLY_SELECTORS", "POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES", "POPULATE_ALL"})
        @ParameterizedTest
        void initialisedFieldShouldBeOverwrittenBySetSelector(final AfterGenerate afterGenerate) {
            assertSelectorOverrideValues(Instancio.of(baseModel(afterGenerate))
                    .set(allLongs(), SELECTOR_OVERRIDE)
                    .create());
        }

        @EnumSource(
                value = AfterGenerate.class,
                names = {"APPLY_SELECTORS", "POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES", "POPULATE_ALL"})
        @ParameterizedTest
        void initialisedFieldShouldNotBeOverwrittenBySetSelectorIfIgnoreIsSpecified(final AfterGenerate afterGenerate) {
            assertInitialisedFieldsNotModified(Instancio.of(baseModel(afterGenerate))
                    .ignore(field(LongHolderWithDefaults.class, "primitive"))
                    .ignore(field(LongHolderWithDefaults.class, "wrapper"))
                    .set(allLongs(), SELECTOR_OVERRIDE)
                    .lenient()
                    .create());
        }

        @EnumSource(
                value = AfterGenerate.class,
                names = "DO_NOT_MODIFY")
        @ParameterizedTest
        void initialisedFieldShouldNotBeOverwrittenBySetSelector(final AfterGenerate afterGenerate) {
            assertInitialisedFieldsNotModified(Instancio.of(baseModel(afterGenerate))
                    .set(allLongs(), SELECTOR_OVERRIDE)
                    .lenient()
                    .create());
        }
    }

    /**
     * If OVERWRITE_EXISTING_VALUES is disabled, initialised fields should NOT be
     * overwritten by the engine or via selectors, regardless of AfterGenerate value.
     */
    @Nested
    @ExtendWith(InstancioExtension.class)
    class WithOverwriteExistingValuesDisabledTest {

        @EnumSource(value = AfterGenerate.class)
        @ParameterizedTest
        void initialisedFieldShouldBeModified(final AfterGenerate afterGenerate) {
            assertInitialisedFieldsNotModified(Instancio.of(baseModel(afterGenerate))
                    .withSettings(OVERWRITES_DISABLED)
                    .create());
        }

        @EnumSource(value = AfterGenerate.class)
        @ParameterizedTest
        void initialisedFieldShouldBeOverwrittenBySetSelector(final AfterGenerate afterGenerate) {
            assertInitialisedFieldsNotModified(Instancio.of(baseModel(afterGenerate))
                    .withSettings(OVERWRITES_DISABLED)
                    .set(allLongs(), SELECTOR_OVERRIDE)
                    .lenient()
                    .create());
        }
    }

    private static void assertInitialisedFieldsNotModified(final Item<LongHolderWithDefaults> result) {
        assertThat(result.getValue().getPrimitive()).isEqualTo(PRIMITIVE);
        assertThat(result.getValue().getWrapper()).isEqualTo(WRAPPER);
    }

    private static void assertInitialisedFieldsOverwritten(final Item<LongHolderWithDefaults> result) {
        assertThat(result.getValue().getPrimitive()).isNotEqualTo(PRIMITIVE);
        assertThat(result.getValue().getWrapper()).isNotEqualTo(WRAPPER);
    }

    private static void assertSelectorOverrideValues(final Item<LongHolderWithDefaults> result) {
        assertThat(result.getValue().getPrimitive()).isEqualTo(SELECTOR_OVERRIDE);
        assertThat(result.getValue().getWrapper()).isEqualTo(SELECTOR_OVERRIDE);
    }
}

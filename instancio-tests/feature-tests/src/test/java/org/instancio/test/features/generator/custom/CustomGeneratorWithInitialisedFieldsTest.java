/*
 * Copyright 2022 the original author or authors.
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
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.PopulateAction;
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
 * with all populate actions + ignored() fields.
 */
@FeatureTag({
        Feature.GENERATOR,
        Feature.IGNORE,
        Feature.POPULATE_ACTION,
        Feature.OVERWRITE_EXISTING_VALUES,
        Feature.SET
})
class CustomGeneratorWithInitialisedFieldsTest {

    private static final long SELECTOR_OVERRIDE = -12345L;

    private static final Settings OVERWRITES_DISABLED = Settings.create()
            .set(Keys.OVERWRITE_EXISTING_VALUES, false);

    private static Generator<?> generator(final PopulateAction action) {
        return new Generator<Object>() {
            @Override
            public Object generate(final Random random) {
                return new LongHolderWithDefaults();
            }

            @Override
            public Hints hints() {
                return Hints.withPopulateAction(action);
            }
        };
    }

    private static Model<Item<LongHolderWithDefaults>> baseModel(final PopulateAction action) {
        return Instancio.of(new TypeToken<Item<LongHolderWithDefaults>>() {})
                .supply(all(LongHolderWithDefaults.class), generator(action))
                .toModel();
    }

    @Nested
    class WithOverwriteExistingValuesEnabledTest {

        @EnumSource(
                value = PopulateAction.class,
                names = {"NONE", "APPLY_SELECTORS", "NULLS", "NULLS_AND_DEFAULT_PRIMITIVES"})
        @ParameterizedTest
        void initialisedFieldShouldNotBeModified(final PopulateAction action) {
            assertInitialisedFieldsNotModified(Instancio.create(baseModel(action)));
        }

        @EnumSource(
                value = PopulateAction.class,
                names = "ALL")
        @ParameterizedTest
        void initialisedFieldShouldBeOverwritten(final PopulateAction action) {
            assertInitialisedFieldsOverwritten(Instancio.create(baseModel(action)));
        }

        @EnumSource(
                value = PopulateAction.class,
                names = {"APPLY_SELECTORS", "NULLS", "NULLS_AND_DEFAULT_PRIMITIVES", "ALL"})
        @ParameterizedTest
        void initialisedFieldShouldBeOverwrittenBySetSelector(final PopulateAction action) {
            assertSelectorOverrideValues(Instancio.of(baseModel(action))
                    .set(allLongs(), SELECTOR_OVERRIDE)
                    .create());
        }

        @EnumSource(
                value = PopulateAction.class,
                names = {"APPLY_SELECTORS", "NULLS", "NULLS_AND_DEFAULT_PRIMITIVES", "ALL"})
        @ParameterizedTest
        void initialisedFieldShouldBeNotOverwrittenBySetSelectorIfIgnoreIsSpecified(final PopulateAction action) {
            assertInitialisedFieldsNotModified(Instancio.of(baseModel(action))
                    .ignore(field(LongHolderWithDefaults.class, "primitive"))
                    .ignore(field(LongHolderWithDefaults.class, "wrapper"))
                    .set(allLongs(), SELECTOR_OVERRIDE)
                    .create());
        }

        @EnumSource(
                value = PopulateAction.class,
                names = "NONE")
        @ParameterizedTest
        void initialisedFieldShouldNotBeOverwrittenBySetSelector(final PopulateAction action) {
            assertInitialisedFieldsNotModified(Instancio.of(baseModel(action))
                    .set(allLongs(), SELECTOR_OVERRIDE)
                    .lenient()
                    .create());
        }
    }

    /**
     * If OVERWRITE_EXISTING_VALUES is disabled, initialised fields should NOT be
     * overwritten by the engine or via selectors, regardless of populate action.
     */
    @Nested
    @ExtendWith(InstancioExtension.class)
    class WithOverwriteExistingValuesDisabledTest {

        @EnumSource(value = PopulateAction.class)
        @ParameterizedTest
        void initialisedFieldShouldBeModified(final PopulateAction action) {
            assertInitialisedFieldsNotModified(Instancio.of(baseModel(action))
                    .withSettings(OVERWRITES_DISABLED)
                    .create());
        }

        @EnumSource(value = PopulateAction.class)
        @ParameterizedTest
        void initialisedFieldShouldBeOverwrittenBySetSelector(final PopulateAction action) {
            assertInitialisedFieldsNotModified(Instancio.of(baseModel(action))
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

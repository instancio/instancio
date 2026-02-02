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
import org.instancio.Model;
import org.instancio.generator.AfterGenerate;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.initialised.Container.OuterPojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.fields;
import static org.instancio.test.support.conditions.Conditions.RANDOM_INTEGER;
import static org.instancio.test.support.conditions.Conditions.RANDOM_STRING;
import static org.instancio.test.support.pojo.initialised.Container.INITIAL_INT;
import static org.instancio.test.support.pojo.initialised.Container.INITIAL_STRING;
import static org.instancio.test.support.pojo.initialised.Container.OVERWRITTEN_BY_SELECTOR;
import static org.instancio.test.support.pojo.initialised.Container.RANDOM_POJO;
import static org.instancio.test.support.pojo.initialised.Container.REFERENCE_FIELD_REGEX;
import static org.instancio.test.support.pojo.initialised.Container.SELECTOR_INT;
import static org.instancio.test.support.pojo.initialised.Container.SELECTOR_STRING;

/**
 * Tests all possible combinations of:
 *
 * <ul>
 *   <li>{@link org.instancio.settings.Keys#OVERWRITE_EXISTING_VALUES} and</li>
 *   <li>{@link AfterGenerate} set via {@link Keys#AFTER_GENERATE_HINT} setting</li>
 * </ul>
 *
 * <p>Expectations:
 *
 * <ul>
 *   <li>{@link Keys#AFTER_GENERATE_HINT} is only applicable to custom generators.
 *       It should not affect the built-in generators and how an object
 *       is populated by the engine.</li>
 *   <li>{@link Keys#OVERWRITE_EXISTING_VALUES} should prevent initialised
 *       fields from being overwritten by the engine, but intialised fields
 *       can still be modified via selectors.</li>
 * </ul>
 *
 * <p>These tests use the {@link OuterPojo} class,
 * which is half-populated and contains nested objects.
 *
 * @see org.instancio.test.features.generator.custom.AfterGenerateAndOverwriteExistingValuesWithCustomGeneratorTest
 */
@FeatureTag({Feature.AFTER_GENERATE, Feature.OVERWRITE_EXISTING_VALUES})
@ExtendWith(InstancioExtension.class)
class AfterGenerateAndOverwriteExistingValuesTest {

    private Model<OuterPojo> model(final boolean overwriteExistingValues, final AfterGenerate afterGenerate) {
        return Instancio.of(OuterPojo.class)
                .ignore(fields().matching(REFERENCE_FIELD_REGEX))
                .withSettings(Settings.create()
                        .set(Keys.OVERWRITE_EXISTING_VALUES, overwriteExistingValues)
                        .set(Keys.AFTER_GENERATE_HINT, afterGenerate))
                .toModel();
    }

    @Nested
    class OverwriteFieldsEnabledTest {

        private final boolean overWriteExistingValues = true;

        /**
         * With overwrite enabled, the behaviour is the same for all
         * {@link AfterGenerate} values - overwrite with random data.
         */
        @EnumSource(AfterGenerate.class)
        @ParameterizedTest
        void overwriteEnabled_withoutSelector(final AfterGenerate afterGenerate) {
            final OuterPojo result = Instancio.create(model(overWriteExistingValues, afterGenerate));

            assertThat(result).is(RANDOM_POJO);
            assertThat(result.getList1()).hasSizeGreaterThan(1).are(RANDOM_POJO);
            assertThat(result.getList2()).hasSizeGreaterThan(1)
                    .isNotSameAs(result.get_initialList2())
                    .are(RANDOM_POJO);

            assertThat(result.getP1()).satisfies(RANDOM_POJO);

            assertThat(result.getP2())
                    .as("new instance")
                    .isNotSameAs(result.get_initialP2())
                    .satisfies(RANDOM_POJO);
        }

        /**
         * With overwrite enabled, the behaviour is the same for all
         * {@link AfterGenerate} values - overwrite with random data.
         * Selectors can be applied to customise the object.
         */
        @EnumSource(AfterGenerate.class)
        @ParameterizedTest
        void overwriteEnabled_withSelector(final AfterGenerate afterGenerate) {
            final OuterPojo result = Instancio.of(model(overWriteExistingValues, afterGenerate))
                    .set(allInts(), SELECTOR_INT)
                    .set(allStrings(), SELECTOR_STRING)
                    .lenient()
                    .create();

            assertThat(result).is(OVERWRITTEN_BY_SELECTOR);
            assertThat(result.getList1()).hasSizeGreaterThan(1).are(OVERWRITTEN_BY_SELECTOR);
            assertThat(result.getList2()).hasSizeGreaterThan(1)
                    .isNotSameAs(result.get_initialList2())
                    .are(OVERWRITTEN_BY_SELECTOR);

            assertThat(result.getP1()).satisfies(OVERWRITTEN_BY_SELECTOR);

            assertThat(result.getP2())
                    .as("new instance")
                    .isNotSameAs(result.get_initialP2())
                    .satisfies(OVERWRITTEN_BY_SELECTOR);
        }
    }

    @Nested
    class OverwriteFieldsDisabledTest {

        private final boolean overWriteExistingValues = false;

        @EnumSource(AfterGenerate.class)
        @ParameterizedTest
        void overwriteDisabled_withoutSelector(final AfterGenerate afterGenerate) {
            final OuterPojo result = Instancio.create(model(overWriteExistingValues, afterGenerate));

            assertThat(result.getN1()).is(RANDOM_INTEGER);
            assertThat(result.getS1()).is(RANDOM_STRING);
            assertThat(result.getN2()).isEqualTo(INITIAL_INT);
            assertThat(result.getS2()).isEqualTo(INITIAL_STRING);
            assertThat(result.getList1()).hasSizeGreaterThan(1).are(RANDOM_POJO);
            assertThat(result.getList2()).hasSize(1)
                    .isSameAs(result.get_initialList2())
                    .allSatisfy(p -> {
                        assertThat(p.getN1()).is(RANDOM_INTEGER);
                        assertThat(p.getS1()).is(RANDOM_STRING);
                        assertThat(p.getN2()).isEqualTo(INITIAL_INT);
                        assertThat(p.getS2()).isEqualTo(INITIAL_STRING);
                    });

            assertThat(result.getP1()).satisfies(RANDOM_POJO);

            assertThat(result.getP2()).isSameAs(result.get_initialP2());
            assertThat(result.getP2().getN1()).is(RANDOM_INTEGER);
            assertThat(result.getP2().getS1()).is(RANDOM_STRING);
            assertThat(result.getP2().getN2()).isEqualTo(INITIAL_INT);
            assertThat(result.getP2().getS2()).isEqualTo(INITIAL_STRING);
        }


        @EnumSource(AfterGenerate.class)
        @ParameterizedTest
        void overwriteDisabled_withSelector(final AfterGenerate afterGenerate) {
            final OuterPojo result = Instancio.of(model(overWriteExistingValues, afterGenerate))
                    .set(allInts(), SELECTOR_INT)
                    .set(allStrings(), SELECTOR_STRING)
                    .lenient()
                    .create();

            assertThat(result).is(OVERWRITTEN_BY_SELECTOR);
            assertThat(result.getList1()).hasSizeGreaterThan(1).are(OVERWRITTEN_BY_SELECTOR);
            assertThat(result.getList2()).hasSize(1)
                    .isSameAs(result.get_initialList2())
                    .are(OVERWRITTEN_BY_SELECTOR);

            assertThat(result.getP1()).satisfies(OVERWRITTEN_BY_SELECTOR);

            assertThat(result.getP2())
                    .isSameAs(result.get_initialP2())
                    .satisfies(OVERWRITTEN_BY_SELECTOR);
        }
    }
}
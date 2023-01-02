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
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.StringAndPrimitiveFields;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({
        Feature.GENERATE,
        Feature.GENERATOR,
        Feature.ON_COMPLETE,
        Feature.AFTER_GENERATE,
        Feature.PREDICATE_SELECTOR,
        Feature.SELECTOR,
        Feature.SET,
        Feature.SUPPLY
})
@ExtendWith(InstancioExtension.class)
class AfterGenerateTest {

    // Initial values
    private static final String ONE = "one";
    private static final String TWO = "two";
    private static final String THREE = "three";
    private static final int INT_ONE = -1;
    private static final int INT_TWO = -2;
    private static final int INT_THREE = -3;

    // Overrides
    private static final String OVERRIDE_ONE = "override-one";
    private static final String OVERRIDE_TWO_VIA_CALLBACK = "override-two";
    private static final int OVERRIDE_INT_ONE = -111;
    private static final int OVERRIDE_INT_TWO_VIA_CALLBACK = -222;

    // Half of the tests verify with overwrites enabled, the other half with overwrites disabled
    private static final Settings DISABLE_OVERWRITES = Settings.create()
            .set(Keys.OVERWRITE_EXISTING_VALUES, false)
            .lock();

    private static class StringAndPrimitiveFieldsGenerator implements Generator<StringAndPrimitiveFields> {
        private final AfterGenerate afterGenerate;

        private StringAndPrimitiveFieldsGenerator(final AfterGenerate afterGenerate) {
            this.afterGenerate = afterGenerate;
        }

        @Override
        public StringAndPrimitiveFields generate(final Random random) {
            // init all fields except "four" and "intFour"
            return StringAndPrimitiveFields.builder()
                    .one(ONE).two(TWO).three(THREE)
                    .intOne(INT_ONE).intTwo(INT_TWO).intThree(INT_THREE)
                    .build();
        }

        @Override
        public Hints hints() {
            return Hints.afterGenerate(afterGenerate);
        }
    }

    private Model<StringAndPrimitiveFields> createModelWithSelectors(final AfterGenerate afterGenerate) {
        return Instancio.of(StringAndPrimitiveFields.class)
                .supply(all(StringAndPrimitiveFields.class), new StringAndPrimitiveFieldsGenerator(afterGenerate))
                .generate(field(StringFields.class, "one"), gen -> gen.text().pattern(OVERRIDE_ONE))
                .generate(field("intOne"), gen -> gen.ints().range(OVERRIDE_INT_ONE, OVERRIDE_INT_ONE))
                .onComplete(all(StringAndPrimitiveFields.class), (StringAndPrimitiveFields result) -> {
                    result.setTwo(OVERRIDE_TWO_VIA_CALLBACK);
                    result.setIntTwo(OVERRIDE_INT_TWO_VIA_CALLBACK);
                })
                .lenient()
                .toModel();
    }

    private static StringAndPrimitiveFields create(final AfterGenerate afterGenerate) {
        return Instancio.of(StringAndPrimitiveFields.class)
                .supply(all(StringAndPrimitiveFields.class), new StringAndPrimitiveFieldsGenerator(afterGenerate))
                .create();
    }

    @Nested
    class WithoutSelectorsTest {

        @Test
        @DisplayName("DO_NOT_MODIFY: object created by the generator is not modified")
        void doNotModify() {
            final StringAndPrimitiveFields result = create(AfterGenerate.DO_NOT_MODIFY);

            assertCustomValuesNotModified(result);
            assertThat(result.getFour()).isNull();
            assertThat(result.getIntFour()).isZero();
        }

        @Test
        @DisplayName("APPLY_SELECTORS: object created by the generator is not modified")
        void applySelectors() {
            final StringAndPrimitiveFields result = create(AfterGenerate.APPLY_SELECTORS);

            assertCustomValuesNotModified(result);
            assertThat(result.getFour()).isNull();
            assertThat(result.getIntFour()).isZero();
        }

        @Test
        @DisplayName("POPULATE_NULLS: only null fields should be populated; non-null fields should not be modified")
        void populateNulls() {
            final StringAndPrimitiveFields result = create(AfterGenerate.POPULATE_NULLS);

            assertCustomValuesNotModified(result);
            assertThat(result.getFour()).as("should be populated").isNotNull();
            assertThat(result.getIntFour()).isZero();
        }

        @Test
        @DisplayName("POPULATE_NULLS_AND_DEFAULT_PRIMITIVES: only null fields and primitives with default values " +
                "should be populated; non-null fields and non-default primitives should not be modified")
        void populateNullsAndDefaultPrimitives() {
            final StringAndPrimitiveFields result = create(AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES);

            assertCustomValuesNotModified(result);
            assertThat(result.getFour()).as("should be populated").isNotNull();
            assertThat(result.getIntFour()).as("should be populated").isNotZero();
        }

        @Test
        @DisplayName("POPULATE_ALL: all fields will be populated; all fields will be overwritten")
        void populateAll() {
            final StringAndPrimitiveFields result = create(AfterGenerate.POPULATE_ALL);

            assertThat(result.getOne()).isNotEqualTo(ONE);
            assertThat(result.getTwo()).isNotEqualTo(TWO);
            assertThat(result.getThree()).isNotEqualTo(THREE);
            assertThat(result.getFour()).isNotNull();

            assertThat(result.getIntOne()).isNotEqualTo(INT_ONE);
            assertThat(result.getIntTwo()).isNotEqualTo(INT_TWO);
            assertThat(result.getIntThree()).isNotEqualTo(INT_THREE);
            assertThat(result.getIntFour()).isNotZero();
        }
    }

    @Nested
    class WithSelectorsTest {

        private StringAndPrimitiveFields create(final AfterGenerate afterGenerate) {
            return Instancio.create(createModelWithSelectors(afterGenerate));
        }

        @Test
        @DisplayName("DO_NOT_MODIFY: object created by the generator cannot be customised")
        void doNotModify() {
            final StringAndPrimitiveFields result = create(AfterGenerate.DO_NOT_MODIFY);

            assertThat(result.getOne()).isEqualTo(ONE);
            assertThat(result.getTwo())
                    .as("onComplete should still be called")
                    .isEqualTo(OVERRIDE_TWO_VIA_CALLBACK);
            assertThat(result.getThree()).isEqualTo(THREE);
            assertThat(result.getFour()).isNull();

            assertThat(result.getIntOne()).isEqualTo(INT_ONE);
            assertThat(result.getIntTwo())
                    .as("onComplete should still be called")
                    .isEqualTo(OVERRIDE_INT_TWO_VIA_CALLBACK);
            assertThat(result.getIntThree()).isEqualTo(INT_THREE);
            assertThat(result.getIntFour()).isZero();
        }

        @Test
        @DisplayName("APPLY_SELECTORS: object created by the generator can be customised")
        void applySelectors() {
            final StringAndPrimitiveFields result = create(AfterGenerate.APPLY_SELECTORS);

            assertThat(result.getOne()).isEqualTo(OVERRIDE_ONE);
            assertThat(result.getTwo()).isEqualTo(OVERRIDE_TWO_VIA_CALLBACK);
            assertThat(result.getThree()).isEqualTo(THREE);
            assertThat(result.getFour()).isNull();

            assertThat(result.getIntOne()).isEqualTo(OVERRIDE_INT_ONE);
            assertThat(result.getIntTwo()).isEqualTo(OVERRIDE_INT_TWO_VIA_CALLBACK);
            assertThat(result.getIntThree()).isEqualTo(INT_THREE);
            assertThat(result.getIntFour()).isZero();
        }

        @Test
        @DisplayName("POPULATE_NULLS: object created by the generator can be customised")
        void populateNulls() {
            final StringAndPrimitiveFields result = create(AfterGenerate.POPULATE_NULLS);

            assertThat(result.getOne()).isEqualTo(OVERRIDE_ONE);
            assertThat(result.getTwo()).isEqualTo(OVERRIDE_TWO_VIA_CALLBACK);
            assertThat(result.getThree()).isEqualTo(THREE);
            assertThat(result.getFour()).isNotNull();

            assertThat(result.getIntOne()).isEqualTo(OVERRIDE_INT_ONE);
            assertThat(result.getIntTwo()).isEqualTo(OVERRIDE_INT_TWO_VIA_CALLBACK);
            assertThat(result.getIntThree()).isEqualTo(INT_THREE);
            assertThat(result.getIntFour()).isZero();
        }

        @Test
        @DisplayName("POPULATE_NULLS_AND_DEFAULT_PRIMITIVES: object created by the generator can be customised")
        void populateNullsAndPrimitives() {
            final StringAndPrimitiveFields result = create(AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES);

            assertThat(result.getOne()).isEqualTo(OVERRIDE_ONE);
            assertThat(result.getTwo()).isEqualTo(OVERRIDE_TWO_VIA_CALLBACK);
            assertThat(result.getThree()).isEqualTo(THREE);
            assertThat(result.getFour()).isNotNull();

            assertThat(result.getIntOne()).isEqualTo(OVERRIDE_INT_ONE);
            assertThat(result.getIntTwo()).isEqualTo(OVERRIDE_INT_TWO_VIA_CALLBACK);
            assertThat(result.getIntThree()).isEqualTo(INT_THREE);
            assertThat(result.getIntFour()).isNotZero();
        }

        @Test
        @DisplayName("POPULATE_ALL: object created by the generator can be customised")
        void populateAll() {
            final StringAndPrimitiveFields result = create(AfterGenerate.POPULATE_ALL);

            System.out.println(result);
            assertThat(result.getOne()).isEqualTo(OVERRIDE_ONE);
            assertThat(result.getTwo()).isEqualTo(OVERRIDE_TWO_VIA_CALLBACK);
            assertThat(result.getThree()).isNotEqualTo(THREE);
            assertThat(result.getFour()).isNotNull();

            assertThat(result.getIntOne()).isNotEqualTo(INT_ONE);
            assertThat(result.getIntTwo()).isEqualTo(OVERRIDE_INT_TWO_VIA_CALLBACK);
            assertThat(result.getIntThree()).isNotEqualTo(INT_THREE);
            assertThat(result.getIntFour()).isNotZero();
        }
    }

    @Nested
    class WithoutSelectorsAndWithOverwriteExistingValuesDisabledTest {

        @WithSettings
        private final Settings settings = DISABLE_OVERWRITES;

        @Test
        @DisplayName("DO_NOT_MODIFY: object created by the generator is not modified")
        void none() {
            final StringAndPrimitiveFields result = create(AfterGenerate.DO_NOT_MODIFY);

            assertCustomValuesNotModified(result);
            assertThat(result.getFour()).isNull();
            assertThat(result.getIntFour()).isZero();
        }

        @Test
        @DisplayName("APPLY_SELECTORS: object created by the generator is not modified")
        void applySelectors() {
            final StringAndPrimitiveFields result = create(AfterGenerate.APPLY_SELECTORS);

            assertCustomValuesNotModified(result);
            assertThat(result.getFour()).isNull();
            assertThat(result.getIntFour()).isZero();
        }

        @Test
        @DisplayName("POPULATE_NULLS: only null fields should be populated; non-null fields should not be modified")
        void populateNulls() {
            final StringAndPrimitiveFields result = create(AfterGenerate.POPULATE_NULLS);

            assertCustomValuesNotModified(result);
            assertThat(result.getFour()).as("should be populated").isNotNull();
            assertThat(result.getIntFour()).isZero();
        }

        @Test
        @DisplayName("POPULATE_NULLS_AND_DEFAULT_PRIMITIVES: only null fields and primitives with default values " +
                "should be populated; non-null fields and non-default primitives should not be modified")
        void populateNullsAndPrimitives() {
            final StringAndPrimitiveFields result = create(AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES);

            assertCustomValuesNotModified(result);
            assertThat(result.getFour()).as("should be populated").isNotNull();
            assertThat(result.getIntFour()).as("should be populated").isNotZero();
        }

        @Test
        @DisplayName("POPULATE_ALL: all fields will be populated but existing values will NOT be overwritten")
        void populateAll() {
            final StringAndPrimitiveFields result = create(AfterGenerate.POPULATE_ALL);

            assertCustomValuesNotModified(result);
            assertThat(result.getFour()).isNotNull();
            assertThat(result.getIntFour()).isNotZero();
        }
    }

    @Nested
    class WithSelectorsAndOverwriteExistingValuesDisabledTest {

        @WithSettings
        private final Settings settings = DISABLE_OVERWRITES;

        private StringAndPrimitiveFields create(final AfterGenerate afterGenerate) {
            return Instancio.create(createModelWithSelectors(afterGenerate));
        }

        @Test
        @DisplayName("DO_NOT_MODIFY: object created by the generator cannot be customised")
        void doNotModify() {
            final StringAndPrimitiveFields result = create(AfterGenerate.DO_NOT_MODIFY);

            assertCustomFieldsAreModifiedOnlyViaCallbacks(result);
            assertThat(result.getFour()).isNull();
            assertThat(result.getIntFour()).isZero();
        }

        @Test
        @DisplayName("APPLY_SELECTORS: generated values can be customised, but custom values not overwritten")
        void applySelectors() {
            final StringAndPrimitiveFields result = create(AfterGenerate.APPLY_SELECTORS);

            assertCustomFieldsAreModifiedOnlyViaCallbacks(result);
            assertThat(result.getFour()).isNull();
            assertThat(result.getIntFour()).isZero();
        }

        @Test
        @DisplayName("POPULATE_NULLS: generated values can be customised, but custom values not overwritten")
        void populateNulls() {
            final StringAndPrimitiveFields result = create(AfterGenerate.POPULATE_NULLS);

            assertCustomFieldsAreModifiedOnlyViaCallbacks(result);
            assertThat(result.getFour()).isNotNull();
            assertThat(result.getIntFour()).isZero();
        }

        @Test
        @DisplayName("POPULATE_NULLS_AND_DEFAULT_PRIMITIVES: generated values can be customised, but custom values not overwritten")
        void populateNullsAndDefaultPrimitives() {
            final StringAndPrimitiveFields result = create(AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES);

            assertCustomFieldsAreModifiedOnlyViaCallbacks(result);
            assertThat(result.getFour()).isNotNull();
            assertThat(result.getIntFour()).isNotZero();
        }

        @Test
        @DisplayName("POPULATE_ALL: generated values can be customised, but custom values not overwritten")
        void populateAll() {
            final StringAndPrimitiveFields result = create(AfterGenerate.POPULATE_ALL);

            assertCustomFieldsAreModifiedOnlyViaCallbacks(result);
            assertThat(result.getFour()).isNotNull();
            assertThat(result.getIntFour()).isNotZero();
        }
    }

    @Nested
    class CustomiseUsingPredicateSelectorTest {

        private final Model<StringAndPrimitiveFields> model = Instancio.of(StringAndPrimitiveFields.class)
                .supply(all(StringAndPrimitiveFields.class),
                        new StringAndPrimitiveFieldsGenerator(AfterGenerate.APPLY_SELECTORS))
                .set(fields().ofType(String.class), "override")
                .set(fields().ofType(int.class), -1)
                .toModel();

        @Test
        void overwriteExistingValuesDisabled() {
            final StringAndPrimitiveFields result = Instancio.of(model)
                    .withSettings(DISABLE_OVERWRITES)
                    .create();

            assertCustomValuesNotModified(result);
            assertThat(result.getFour()).isEqualTo("override");
            assertThat(result.getIntFour()).isEqualTo(-1);
        }

        @Test
        void overwriteExistingValuesEnabled() {
            final StringAndPrimitiveFields result = Instancio.create(model);
            assertThatObject(result).hasAllFieldsOfTypeEqualTo(String.class, "override");
            assertThatObject(result).hasAllFieldsOfTypeEqualTo(int.class, -1);
        }
    }

    private static void assertCustomValuesNotModified(final StringAndPrimitiveFields result) {
        assertThat(result.getOne()).isEqualTo(ONE);
        assertThat(result.getTwo()).isEqualTo(TWO);
        assertThat(result.getThree()).isEqualTo(THREE);
        assertThat(result.getIntOne()).isEqualTo(INT_ONE);
        assertThat(result.getIntTwo()).isEqualTo(INT_TWO);
        assertThat(result.getIntThree()).isEqualTo(INT_THREE);
    }

    private static void assertCustomFieldsAreModifiedOnlyViaCallbacks(final StringAndPrimitiveFields result) {
        assertThat(result.getOne()).isEqualTo(ONE);
        assertThat(result.getTwo())
                .as("onComplete should still be called")
                .isEqualTo(OVERRIDE_TWO_VIA_CALLBACK);
        assertThat(result.getThree()).isEqualTo(THREE);

        assertThat(result.getIntOne()).isEqualTo(INT_ONE);
        assertThat(result.getIntTwo())
                .as("onComplete should still be called")
                .isEqualTo(OVERRIDE_INT_TWO_VIA_CALLBACK);
        assertThat(result.getIntThree()).isEqualTo(INT_THREE);
    }
}

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
import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.PopulateAction;
import org.instancio.test.support.pojo.misc.StringAndPrimitiveFields;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({
        Feature.GENERATE,
        Feature.GENERATOR,
        Feature.ON_COMPLETE,
        Feature.POPULATE_ACTION,
        Feature.PREDICATE_SELECTOR,
        Feature.SELECTOR,
        Feature.SET,
        Feature.SUPPLY
})
class GeneratorPopulateActionTest {
    private static final String ONE = "one";
    private static final String TWO = "two";
    private static final String THREE = "three";
    private static final int INT_ONE = 1;
    private static final int INT_TWO = 2;
    private static final int INT_THREE = 3;

    private static class StringAndPrimitiveFieldsGenerator implements Generator<StringAndPrimitiveFields> {
        private final PopulateAction populateAction;

        private StringAndPrimitiveFieldsGenerator(final PopulateAction populateAction) {
            this.populateAction = populateAction;
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
            return Hints.withPopulateAction(populateAction);
        }
    }

    private final Generator<?> generatorActionApplySelectors = new StringAndPrimitiveFieldsGenerator(PopulateAction.APPLY_SELECTORS);
    private final Generator<?> generatorActionPopulateNone = new StringAndPrimitiveFieldsGenerator(PopulateAction.NONE);
    private final Generator<?> generatorActionPopulateNulls = new StringAndPrimitiveFieldsGenerator(PopulateAction.NULLS);
    private final Generator<?> generatorActionPopulateNullsAndPrimitives = new StringAndPrimitiveFieldsGenerator(PopulateAction.NULLS_AND_DEFAULT_PRIMITIVES);
    private final Generator<?> generatorActionPopulateAll = new StringAndPrimitiveFieldsGenerator(PopulateAction.ALL);

    @Nested
    class PopulateActionTest {

        private StringAndPrimitiveFields create(final Generator<?> generator) {
            return Instancio.of(StringAndPrimitiveFields.class)
                    .supply(all(StringAndPrimitiveFields.class), generator)
                    .create();
        }

        @Test
        @DisplayName("Action NONE: object created by the generator is not modified")
        void noneAction() {
            final StringAndPrimitiveFields result = create(generatorActionApplySelectors);

            assertThat(generatorActionPopulateNone.hints().populateAction())
                    .isEqualTo(PopulateAction.NONE);

            assertThat(result.getOne()).isEqualTo(ONE);
            assertThat(result.getTwo()).isEqualTo(TWO);
            assertThat(result.getThree()).isEqualTo(THREE);
            assertThat(result.getFour()).isNull();

            assertThat(result.getIntOne()).isEqualTo(INT_ONE);
            assertThat(result.getIntTwo()).isEqualTo(INT_TWO);
            assertThat(result.getIntThree()).isEqualTo(INT_THREE);
            assertThat(result.getIntFour()).isZero();
        }

        @Test
        @DisplayName("Action APPLY_SELECTORS: object created by the generator is not modified")
        void applySelectorsAction() {
            final StringAndPrimitiveFields result = create(generatorActionApplySelectors);

            assertThat(generatorActionApplySelectors.hints().populateAction())
                    .isEqualTo(PopulateAction.APPLY_SELECTORS);

            assertThat(result.getOne()).isEqualTo(ONE);
            assertThat(result.getTwo()).isEqualTo(TWO);
            assertThat(result.getThree()).isEqualTo(THREE);
            assertThat(result.getFour()).isNull();

            assertThat(result.getIntOne()).isEqualTo(INT_ONE);
            assertThat(result.getIntTwo()).isEqualTo(INT_TWO);
            assertThat(result.getIntThree()).isEqualTo(INT_THREE);
            assertThat(result.getIntFour()).isZero();
        }

        @Test
        @DisplayName("Action NULLS: only null fields should be populated; non-null fields should not be modified")
        void populateNullsAction() {
            final StringAndPrimitiveFields result = create(generatorActionPopulateNulls);

            assertThat(generatorActionPopulateNulls.hints().populateAction())
                    .isEqualTo(PopulateAction.NULLS);

            assertThat(result.getOne()).isEqualTo(ONE);
            assertThat(result.getTwo()).isEqualTo(TWO);
            assertThat(result.getThree()).isEqualTo(THREE);
            assertThat(result.getFour()).as("should be populated").isNotNull();

            assertThat(result.getIntOne()).isEqualTo(INT_ONE);
            assertThat(result.getIntTwo()).isEqualTo(INT_TWO);
            assertThat(result.getIntThree()).isEqualTo(INT_THREE);
            assertThat(result.getIntFour()).isZero();
        }

        @Test
        @DisplayName("Action NULLS_AND_PRIMITIVES: only null fields and primitives with default values " +
                "should be populated; non-null fields and non-default primitives should not be modified")
        void populateNullsAndPrimitivesAction() {
            final StringAndPrimitiveFields result = create(generatorActionPopulateNullsAndPrimitives);

            assertThat(generatorActionPopulateNullsAndPrimitives.hints().populateAction())
                    .isEqualTo(PopulateAction.NULLS_AND_DEFAULT_PRIMITIVES);

            assertThat(result.getOne()).isEqualTo(ONE);
            assertThat(result.getTwo()).isEqualTo(TWO);
            assertThat(result.getThree()).isEqualTo(THREE);
            assertThat(result.getFour()).as("should be populated").isNotNull();

            assertThat(result.getIntOne()).isEqualTo(INT_ONE);
            assertThat(result.getIntTwo()).isEqualTo(INT_TWO);
            assertThat(result.getIntThree()).isEqualTo(INT_THREE);
            assertThat(result.getIntFour()).as("should be populated").isNotZero();
        }

        @Test
        @DisplayName("Action POPULATE: all fields will be populated; all fields will be overwritten")
        void populateAction() {
            final StringAndPrimitiveFields result = create(generatorActionPopulateAll);

            assertThat(generatorActionPopulateAll.hints().populateAction())
                    .isEqualTo(PopulateAction.ALL);

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

    /**
     * Customising objects returned by custom generator using generate()/onComplete().
     */
    @Nested
    class CustomisingObjectsReturnedByGeneratorTest {

        private final String OVERRIDE_ONE = "override-one";
        private final String OVERRIDE_TWO = "override-two";
        private final int OVERRIDE_INT_ONE = -1;
        private final int OVERRIDE_INT_TWO = -2;

        private StringAndPrimitiveFields create(final Generator<?> generator) {
            return Instancio.of(StringAndPrimitiveFields.class)
                    .supply(all(StringAndPrimitiveFields.class), generator)
                    .generate(field(StringFields.class, "one"), gen -> gen.text().pattern(OVERRIDE_ONE))
                    .generate(field("intOne"), gen -> gen.ints().range(OVERRIDE_INT_ONE, OVERRIDE_INT_ONE))
                    .onComplete(all(StringAndPrimitiveFields.class), (StringAndPrimitiveFields result) -> {
                        result.setTwo(OVERRIDE_TWO);
                        result.setIntTwo(OVERRIDE_INT_TWO);
                    })
                    .lenient()
                    .create();
        }

        @Test
        @DisplayName("Action NONE: object created by the generator cannot be customised")
        void noneAction() {
            final StringAndPrimitiveFields result = create(generatorActionPopulateNone);

            assertThat(result.getOne()).isEqualTo(ONE);
            assertThat(result.getTwo())
                    .as("onComplete should still be called")
                    .isEqualTo(OVERRIDE_TWO);
            assertThat(result.getThree()).isEqualTo(THREE);
            assertThat(result.getFour()).isNull();

            assertThat(result.getIntOne()).isEqualTo(INT_ONE);
            assertThat(result.getIntTwo())
                    .as("onComplete should still be called")
                    .isEqualTo(OVERRIDE_INT_TWO);
            assertThat(result.getIntThree()).isEqualTo(INT_THREE);
            assertThat(result.getIntFour()).isZero();
        }

        @Test
        @DisplayName("Action APPLY_SELECTORS: object created by the generator can be customised")
        void applySelectorsAction() {
            final StringAndPrimitiveFields result = create(generatorActionApplySelectors);

            assertThat(result.getOne()).isEqualTo(OVERRIDE_ONE);
            assertThat(result.getTwo()).isEqualTo(OVERRIDE_TWO);
            assertThat(result.getThree()).isEqualTo(THREE);
            assertThat(result.getFour()).isNull();

            assertThat(result.getIntOne()).isEqualTo(OVERRIDE_INT_ONE);
            assertThat(result.getIntTwo()).isEqualTo(OVERRIDE_INT_TWO);
            assertThat(result.getIntThree()).isEqualTo(INT_THREE);
            assertThat(result.getIntFour()).isZero();
        }

        @Test
        @DisplayName("Action NULLS: object created by the generator can be customised")
        void populateNullsAction() {
            final StringAndPrimitiveFields result = create(generatorActionPopulateNulls);

            assertThat(result.getOne()).isEqualTo(OVERRIDE_ONE);
            assertThat(result.getTwo()).isEqualTo(OVERRIDE_TWO);
            assertThat(result.getThree()).isEqualTo(THREE);
            assertThat(result.getFour()).isNotNull();

            assertThat(result.getIntOne()).isEqualTo(OVERRIDE_INT_ONE);
            assertThat(result.getIntTwo()).isEqualTo(OVERRIDE_INT_TWO);
            assertThat(result.getIntThree()).isEqualTo(INT_THREE);
            assertThat(result.getIntFour()).isZero();
        }

        @Test
        @DisplayName("Action NULLS_AND_PRIMITIVES: object created by the generator can be customised")
        void populateNullsAndPrimitivesAction() {
            final StringAndPrimitiveFields result = create(generatorActionPopulateNullsAndPrimitives);

            assertThat(result.getOne()).isEqualTo(OVERRIDE_ONE);
            assertThat(result.getTwo()).isEqualTo(OVERRIDE_TWO);
            assertThat(result.getThree()).isEqualTo(THREE);
            assertThat(result.getFour()).isNotNull();

            assertThat(result.getIntOne()).isEqualTo(OVERRIDE_INT_ONE);
            assertThat(result.getIntTwo()).isEqualTo(OVERRIDE_INT_TWO);
            assertThat(result.getIntThree()).isEqualTo(INT_THREE);
            assertThat(result.getIntFour()).isNotZero();
        }

        @Test
        @DisplayName("Action POPULATE: object created by the generator can be customised")
        void populateAction() {
            final StringAndPrimitiveFields result = create(generatorActionPopulateAll);

            assertThat(result.getOne()).isEqualTo(OVERRIDE_ONE);
            assertThat(result.getTwo()).isEqualTo(OVERRIDE_TWO);
            assertThat(result.getThree()).isNotEqualTo(THREE);
            assertThat(result.getFour()).isNotNull();

            assertThat(result.getIntOne()).isNotEqualTo(INT_ONE);
            assertThat(result.getIntTwo()).isNotEqualTo(INT_TWO);
            assertThat(result.getIntThree()).isNotEqualTo(INT_THREE);
            assertThat(result.getIntFour()).isNotZero();
        }
    }

    @Test
    void customiseObjectUsingPredicateSelector() {
        final StringAndPrimitiveFields result = Instancio.of(StringAndPrimitiveFields.class)
                .supply(all(StringAndPrimitiveFields.class), generatorActionApplySelectors)
                .set(fields().ofType(String.class), "override")
                .set(fields().ofType(int.class), -1)
                .create();

        assertThatObject(result).hasAllFieldsOfTypeEqualTo(String.class, "override");
        assertThatObject(result).hasAllFieldsOfTypeEqualTo(int.class, -1);
    }
}

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
package org.instancio.test.features.generator;

import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;

/**
 * Tests for specifying supply() and generate() on the same target.
 */
@FeatureTag({
        Feature.SELECTOR,
        Feature.SELECTOR_PRECEDENCE,
        Feature.SET,
        Feature.GENERATE,
        Feature.SUPPLY})
class GenerateSupplyPrecedenceTest {

    private static final String SHOULD_WIN = "should-win";
    private static final String WRONG = "wrong-answer";

    private static Stream<Arguments> selectors() {
        return Stream.of(Arguments.of(
                // regular
                allStrings(),
                field("value"),
                // predicate
                types().of(String.class),
                fields().named("value")
        ));
    }

    @Nested
    @DisplayName("Given the same selector, supply() takes precedence over generate(), regardless of which comes first")
    class SameSelectorTest {

        @ParameterizedTest
        @MethodSource("org.instancio.test.features.generator.GenerateSupplyPrecedenceTest#selectors")
        void supplyFirst(final TargetSelector selector) {
            final StringHolder result = Instancio.of(StringHolder.class)
                    // supply first
                    .supply(selector, () -> SHOULD_WIN)
                    .generate(selector, gen -> gen.text().pattern(WRONG))
                    .create();

            assertThat(result.getValue()).isEqualTo(SHOULD_WIN);
        }

        @ParameterizedTest
        @MethodSource("org.instancio.test.features.generator.GenerateSupplyPrecedenceTest#selectors")
        void generateFirst(final TargetSelector selector) {
            final StringHolder result = Instancio.of(StringHolder.class)
                    // generate first
                    .generate(selector, gen -> gen.text().pattern(WRONG))
                    .supply(selector, () -> SHOULD_WIN)
                    .create();

            assertThat(result.getValue()).isEqualTo(SHOULD_WIN);
        }
    }

    /**
     * Regular selector always wins over predicate selector,
     * regardless of which method is used, generate() or supply().
     */
    @Nested
    class RegularAndPredicateSelectorsTest {

        @Test
        @DisplayName("Field: supply() with regular selector takes precedence over generate() with predicate selector")
        void supplyWithRegularFieldSelector() {
            final StringHolder result = Instancio.of(StringHolder.class)
                    .supply(field("value"), () -> SHOULD_WIN)
                    .generate(fields().named("value"), gen -> gen.text().pattern(WRONG))
                    .lenient()
                    .create();

            assertThat(result.getValue()).isEqualTo(SHOULD_WIN);
        }

        @Test
        @DisplayName("Field: supply() with predicate selector has LOWER precedence than generate() with regular selector")
        void supplyWithPredicateFieldSelector() {
            final StringHolder result = Instancio.of(StringHolder.class)
                    .supply(fields().named("value"), () -> WRONG)
                    .generate(field("value"), gen -> gen.text().pattern(SHOULD_WIN))
                    .lenient()
                    .create();

            assertThat(result.getValue()).isEqualTo(SHOULD_WIN);
        }

        @Test
        @DisplayName("Type: generate() with regular selector takes precedence over supply() with predicate selector")
        void generateWithRegularTypeSelector() {
            final StringHolder result = Instancio.of(StringHolder.class)
                    .generate(all(String.class), gen -> gen.text().pattern(SHOULD_WIN))
                    .supply(types().of(String.class), () -> WRONG)
                    .lenient()
                    .create();

            assertThat(result.getValue()).isEqualTo(SHOULD_WIN);
        }

        @Test
        @DisplayName("Type: generate() with predicate selector has LOWER precedence than supply() with regular selector")
        void generateWithPredicateTypeSelector() {
            final StringHolder result = Instancio.of(StringHolder.class)
                    .generate(types().of(String.class), gen -> gen.text().pattern(WRONG))
                    .supply(all(String.class), () -> SHOULD_WIN)
                    .lenient()
                    .create();

            assertThat(result.getValue()).isEqualTo(SHOULD_WIN);
        }
    }
}

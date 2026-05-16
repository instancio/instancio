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
package org.instancio.test.features.generator;

import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;

/**
 * Tests for specifying supply() and generate() on the same target.
 * Given the same selector, supply() takes precedence over generate(),
 * regardless of which comes first
 */
@FeatureTag({
        Feature.SELECTOR,
        Feature.SELECTOR_PRECEDENCE,
        Feature.SET,
        Feature.GENERATE,
        Feature.SUPPLY})
@ExtendWith(InstancioExtension.class)
class GenerateSupplyPrecedenceTest {

    private static final String WINNING_VALUE = "winning-value";
    private static final String LOSING_VALUE = "losing-value";

    private static final List<TargetSelector> SELECTORS = List.of(
            // regular
            allStrings(),
            field("value"),
            // predicate
            types().of(String.class),
            fields().named("value")
    );

    @ParameterizedTest
    @FieldSource("SELECTORS")
    void supplyFirst(final TargetSelector selector) {
        final StringHolder result = Instancio.of(StringHolder.class)
                // supply first
                .supply(selector, () -> WINNING_VALUE)
                .generate(selector, gen -> gen.text().pattern(LOSING_VALUE))
                .create();

        assertThat(result.getValue()).isEqualTo(WINNING_VALUE);
    }

    @ParameterizedTest
    @FieldSource("SELECTORS")
    void generateFirst(final TargetSelector selector) {
        final StringHolder result = Instancio.of(StringHolder.class)
                // generate first
                .generate(selector, gen -> gen.text().pattern(LOSING_VALUE))
                .supply(selector, () -> WINNING_VALUE)
                .create();

        assertThat(result.getValue()).isEqualTo(WINNING_VALUE);
    }
}

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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.AbcListHolder.Nested;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.elementOf;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@FeatureTag({Feature.ELEMENT_OF_SELECTOR, Feature.GENERATOR, Feature.SUPPLY})
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorSupplyGenerateTest {

    private static final StringsAbc EXPECTED_ABC = StringsAbc.builder()
            .a("_a_")
            .b("_b_")
            .c("_c_")
            .build();

    @Test
    void supplyNonRandomWholeElement() {
        final TargetSelector elements = all(
                elementOf(AbcListHolder::getAbcElements1).at(1),
                elementOf(Nested::getAbcElements2).at(2)
        );

        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .supply(elements, () -> EXPECTED_ABC)
                .create();

        final String[] subtrees = {"abcElements1[1]", "nested.abcElements2[2]"};

        assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, subtrees);
    }

    @org.junit.jupiter.api.Nested
    class SupplyAndGenerateField {
        private static final String PREFIX = "prefix_";
        private static final TargetSelector ELEMENT_FIELD =
                elementOf(Nested::getAbcElements1).field(StringsGhi::getH);

        private static void assertResult(final AbcListHolder result) {
            final String subtree = "nested.abcElements1[*].def.ghi.h";

            assertThatGraph(result)
                    .includingSubtrees(subtree)
                    .allValuesOfTypeSatisfy(String.class, value ->
                            assertThat(value).startsWith(PREFIX));

            assertThatGraph(result)
                    .excludingSubtrees(subtree)
                    .allValuesOfTypeSatisfy(String.class, value ->
                            assertThat(value).doesNotStartWith(PREFIX));
        }

        @Test
        void supplyRandomElementField() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .supply(ELEMENT_FIELD, random -> PREFIX + random.alphanumeric(5))
                    .create();

            assertResult(result);
        }

        @Test
        void generateElementField() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .generate(ELEMENT_FIELD, gen -> gen.string().prefix(PREFIX))
                    .create();

            assertResult(result);
        }
    }
}

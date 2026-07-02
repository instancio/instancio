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

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.record.StringsAbcRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@SuppressWarnings("NullAway")
@FeatureTag({Feature.ASSIGN, Feature.ELEMENT_OF_SELECTOR})
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorAssignSpecialTypesTest {

    private static final int SIZE = 5;
    private static final int LAST_INDEX = SIZE - 1;
    private static final String EXPECTED_STRING = "_value_";

    @Nested
    class CyclicElementTest {
        //@formatter:off
        // NOTE: the POJOs are superficially similar, however they exercises different branches
        private static @Data class CyclicElement { String name; CyclicElement child; } // subtree contains a cycle
        private static @Data class CyclicHolder { List<CyclicElement> elements; }
        private static @Data class NestedListElement { String name; List<String> tags; } // subtree contains a nested collection
        private static @Data class NestedListHolder { List<NestedListElement> elements; }
        //@formatter:on

        @Test
        void cyclicChild_inElementSubtree() {
            final TargetSelector origin = elementOf(CyclicHolder::getElements)
                    .first()
                    .field(CyclicElement::getName);

            final TargetSelector destination = elementOf(CyclicHolder::getElements)
                    .last()
                    .field(CyclicElement::getName);

            final CyclicHolder result = Instancio.of(CyclicHolder.class)
                    .size(field(CyclicHolder::getElements), SIZE)
                    .set(origin, EXPECTED_STRING)
                    .assign(valueOf(origin).to(destination))
                    .create();

            assertThat(result.getElements()).hasSize(SIZE);
            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "elements[0,%s]".formatted(LAST_INDEX));
        }

        @Test
        void elementWithNestedListField() {
            final TargetSelector origin = elementOf(NestedListHolder::getElements)
                    .first()
                    .field(NestedListElement::getName);

            final TargetSelector destination = elementOf(NestedListHolder::getElements)
                    .last()
                    .field(NestedListElement::getName);

            final NestedListHolder result = Instancio.of(NestedListHolder.class)
                    .size(field(NestedListHolder::getElements), SIZE)
                    .set(origin, EXPECTED_STRING)
                    .assign(valueOf(origin).to(destination))
                    .create();

            assertThat(result.getElements()).hasSize(SIZE);
            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "elements[0,%s].name".formatted(LAST_INDEX));
        }
    }

    @Nested
    class RecordComponent {

        @Test
        void recordComponentDestination() {
            @Data
            class RecordHolder {
                private List<StringsAbcRecord> records;
            }

            final TargetSelector origin = elementOf(RecordHolder::getRecords).first().field(StringsAbcRecord::a);
            final TargetSelector destination = elementOf(RecordHolder::getRecords).last().field(StringsAbcRecord::a);

            final int size = 10;
            final RecordHolder result = Instancio.of(RecordHolder.class)
                    .size(field(RecordHolder::getRecords), size)
                    .set(origin, EXPECTED_STRING)
                    .assign(valueOf(origin).to(destination))
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "records[0,%s].a".formatted(size - 1));
        }

    }
}

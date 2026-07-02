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
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.instancio.Assign.valueOf;
import static org.instancio.Select.elementOf;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@FeatureTag({Feature.ASSIGN, Feature.ELEMENT_OF_SELECTOR})
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorAssignWholeToSubFieldTest {

    private static final int SIZE = 5;
    private static final int LAST_INDEX = SIZE - 1;
    private static final String EXPECTED_STRING = "_value_";

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.COLLECTION_MIN_SIZE, SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, SIZE);

    private static @Data class StringAndHolderLists {
        private List<String> strings;
        private List<StringHolder> holders;
    }

    private static StringAndHolderLists assign(final TargetSelector origin, final TargetSelector destination) {
        return Instancio.of(StringAndHolderLists.class)
                .set(origin, EXPECTED_STRING)
                .assign(valueOf(origin).to(destination))
                .create();
    }

    /**
     * Whole {@code String} element copied into a {@code StringHolder.value} sub-field.
     */
    @Nested
    class WholeElementToSubField {

        @ValueSource(ints = {0, 1, 2, 3, 4})
        @ParameterizedTest
        void atIndex(final int index) {
            final TargetSelector origin = elementOf(StringAndHolderLists::getStrings).at(index);
            final TargetSelector destination = elementOf(StringAndHolderLists::getHolders).at(index)
                    .field(StringHolder::getValue);

            assertThatGraph(assign(origin, destination)).hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                    "strings[%s]".formatted(index),
                    "holders[%s].value".formatted(index),
            });
        }

        @Test
        void acrossDifferentIndices() {
            final TargetSelector origin = elementOf(StringAndHolderLists::getStrings).first();
            final TargetSelector destination = elementOf(StringAndHolderLists::getHolders).last()
                    .field(StringHolder::getValue);

            assertThatGraph(assign(origin, destination)).hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                    "strings[0]",
                    "holders[%s].value".formatted(LAST_INDEX),
            });
        }

        @Test
        void oneOriginToRangeOfDestinations() {
            final TargetSelector origin = elementOf(StringAndHolderLists::getStrings).at(0);
            final TargetSelector destination = elementOf(StringAndHolderLists::getHolders).range(1, 3)
                    .field(StringHolder::getValue);

            assertThatGraph(assign(origin, destination)).hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                    "strings[0]",
                    "holders[1,2,3].value",
            });
        }

        @Test
        void withMapper() {
            final TargetSelector origin = elementOf(StringAndHolderLists::getStrings).first();
            final TargetSelector destination = elementOf(StringAndHolderLists::getHolders).first()
                    .field(StringHolder::getValue);

            final String mapped = "_mapped_";
            final StringAndHolderLists result = Instancio.of(StringAndHolderLists.class)
                    .set(origin, EXPECTED_STRING)
                    .assign(valueOf(origin).to(destination).as((String o) -> mapped))
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "strings[0]")
                    .hasValuesEqualToExactlyIn(mapped, "holders[0].value");
        }
    }

    /**
     * {@code StringHolder.value} sub-field copied into a whole {@code String} element.
     */
    @Nested
    class SubFieldToWholeElement {

        @ValueSource(ints = {0, 1, 2, 3, 4})
        @ParameterizedTest
        void atIndex(final int index) {
            final TargetSelector origin = elementOf(StringAndHolderLists::getHolders)
                    .at(index)
                    .field(StringHolder::getValue);

            final TargetSelector destination = elementOf(StringAndHolderLists::getStrings).at(index);

            assertThatGraph(assign(origin, destination)).hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                    "holders[%s].value".formatted(index),
                    "strings[%s]".formatted(index),
            });
        }

        @Test
        void withMapper() {
            final TargetSelector origin = elementOf(StringAndHolderLists::getHolders).last()
                    .field(StringHolder::getValue);
            final TargetSelector destination = elementOf(StringAndHolderLists::getStrings).last();

            final String mapped = "_mapped_";
            final StringAndHolderLists result = Instancio.of(StringAndHolderLists.class)
                    .set(origin, EXPECTED_STRING)
                    .assign(valueOf(origin).to(destination).as((String o) -> mapped))
                    .create();

            assertThatGraph(result)
                    .hasValuesEqualToExactlyIn(EXPECTED_STRING, "holders[%s].value".formatted(LAST_INDEX))
                    .hasValuesEqualToExactlyIn(mapped, "strings[%s]".formatted(LAST_INDEX));
        }
    }

}

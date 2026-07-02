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
import org.instancio.SelectorGroup;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.elementOf;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@FeatureTag({Feature.ELEMENT_OF_SELECTOR, Feature.SELECT_GROUP})
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorGroupTest {

    private static final String EXPECTED_STRING = "_value_";
    private static final StringsAbc EXPECTED_ABC = new StringsAbc();

    @Test
    void elementOfGroup() {
        final SelectorGroup group = all(
                elementOf(AbcListHolder::getAbcElements1),
                elementOf(AbcListHolder::getAbcElements2));

        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .set(group, EXPECTED_ABC)
                .create();

        final String[] subtrees = {"abcElements1", "abcElements2"};

        assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, subtrees);
    }

    @Test
    void elementOfAtGroup() {
        final SelectorGroup group = all(
                elementOf(AbcListHolder::getAbcElements1).at(1),
                elementOf(AbcListHolder::getAbcElements2).at(2));

        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .set(group, EXPECTED_ABC)
                .create();

        final String[] subtrees = {"abcElements1[1]", "abcElements2[2]"};

        assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_ABC, subtrees);
    }

    @Test
    void elementOfGroupField() {
        final SelectorGroup group = all(
                elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getC),
                elementOf(AbcListHolder::getAbcElements2).field(StringsAbc::getC));

        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .set(group, EXPECTED_STRING)
                .create();

        final String[] subtrees = {"abcElements1[*].c", "abcElements2[*].c"};

        assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, subtrees);
    }

    @Test
    void elementOfGroupTarget() {
        final SelectorGroup group = all(
                elementOf(AbcListHolder::getAbcElements1).target(allStrings()),
                elementOf(AbcListHolder::getAbcElements2).target(allStrings()));

        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .set(group, EXPECTED_STRING)
                .create();

        final String[] subtrees = {"abcElements1", "abcElements2"};

        assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, subtrees);
    }

    @Test
    void elementOfGroupAtTarget() {
        final SelectorGroup group = all(
                elementOf(AbcListHolder::getAbcElements1).at(1).target(allStrings()),
                elementOf(AbcListHolder::getAbcElements2).at(2).target(allStrings()));

        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .set(group, EXPECTED_STRING)
                .create();

        final String[] subtrees = {"abcElements1[1]", "abcElements2[2]"};

        assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, subtrees);
    }

}

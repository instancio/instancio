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
package org.instancio.test.guava.collect;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.Data;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.instancio.Select.elementOf;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@ExtendWith(InstancioExtension.class)
class GuavaElementOfSelectorTest {

    private static final String EXPECTED = "_value_";

    private static @Data class Holder {
        private ImmutableList<StringHolder> list;
        private ImmutableSet<String> set;
        private HashMultiset<StringHolder> multiset;
    }

    @Test
    void immutableList_wholeElementByIndex() {
        final StringHolder expected = new StringHolder(EXPECTED);

        final Holder result = Instancio.of(Holder.class)
                .set(elementOf(Holder::getList).at(2), expected)
                .create();

        assertThatGraph(result).hasValuesEqualToExactlyIn(expected, "list[2]");
    }

    @Test
    void immutableList_elementFieldOfAllElements() {
        final Holder result = Instancio.of(Holder.class)
                .set(elementOf(Holder::getList).field(StringHolder::getValue), EXPECTED)
                .create();

        assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED, "list[*].value");
    }

    @Test
    void immutableSet_firstWholeElement() {
        final Holder result = Instancio.of(Holder.class)
                .set(elementOf(Holder::getSet).first(), EXPECTED)
                .create();

        assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED, "set[0]");
    }

    @Test
    void multiset_elementFieldOfAllElements() {
        final Holder result = Instancio.of(Holder.class)
                .set(elementOf(Holder::getMultiset).field(StringHolder::getValue), EXPECTED)
                .create();

        assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED, "multiset[*].value");
    }
}

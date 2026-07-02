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
import org.instancio.settings.Keys;
import org.instancio.test.support.pojo.collections.lists.ListInteger;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.elementOf;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@FeatureTag({Feature.ELEMENT_OF_SELECTOR, Feature.WITH_NULLABLE, Feature.ASSIGN})
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorWithNullableTest {

    @Test
    void withNullableMultipleElementFields() {
        final int size = 100;

        final List<AbcListHolder> result = Instancio.ofList(AbcListHolder.class)
                .size(size)
                .withNullable(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getA))
                .withNullable(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getB))
                .create();

        assertThat(result)
                .hasSize(size)
                .map(it -> it.getAbcElements1().get(0))
                .map(StringsAbc::getA)
                .containsNull();

        assertThat(result)
                .hasSize(size)
                .map(it -> it.getAbcElements1().get(0))
                .map(StringsAbc::getB)
                .containsNull();
    }

    @Test
    void withNullableElementField() {
        final int size = 100;

        final List<AbcListHolder> result = Instancio.ofList(AbcListHolder.class)
                .size(size)
                .withNullable(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getA))
                .create();

        assertThat(result)
                .hasSize(size)
                .map(it -> it.getAbcElements1().get(0))
                .map(StringsAbc::getA)
                .containsNull();

        // All strings except `abcElements1[0].a` should be non-blank
        assertThatGraph(result)
                .excludingSubtrees("**.abcElements1[0].a")
                .allValuesOfTypeSatisfy(String.class, val -> assertThat(val).isNotBlank());
    }

    @Test
    void setNullElement_isHonouredWhenElementsNotNullable() {
        final ListInteger result = Instancio.of(ListInteger.class)
                .withSetting(Keys.COLLECTION_ELEMENTS_NULLABLE, false)
                .set(elementOf(ListInteger::getList).at(1), null)
                .create();

        assertThatGraph(result)
                .hasValuesOfTypeEqualToExactlyIn(Integer.class, null, "list[1]");
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void copiedNullElement_isHonouredWhenElementsNotNullable() {
        final TargetSelector origin = elementOf(ListInteger::getList).at(4);
        final TargetSelector destination = elementOf(ListInteger::getList).at(0);

        final ListInteger result = Instancio.of(ListInteger.class)
                .withSetting(Keys.COLLECTION_ELEMENTS_NULLABLE, false)
                .set(origin, null)
                .assign(valueOf(origin).to(destination))
                .create();

        assertThatGraph(result)
                .hasValuesOfTypeEqualToExactlyIn(Integer.class, null, "list[0,4]");
    }

    @Test
    void assignNullElement_isHonouredWhenElementsNotNullable() {
        final int size = 10;

        final List<AbcListHolder> result = Instancio.ofList(AbcListHolder.class)
                .size(size)
                .withSetting(Keys.COLLECTION_ELEMENTS_NULLABLE, false)
                .assign(valueOf(elementOf(AbcListHolder::getAbcElements1).at(2)).set(null))
                .create();

        assertThat(result).hasSize(size).allSatisfy(holder -> {
            assertThatGraph(holder)
                    .hasValuesOfTypeEqualToExactlyIn(StringsAbc.class, null, "abcElements1[2]");
        });
    }
}

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
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.AbcListHolder.Nested;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.elementOf;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@FeatureTag({Feature.ELEMENT_OF_SELECTOR, Feature.FILTER})
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorFilterTest {

    @Test
    void filterElementField() {
        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .generate(allStrings(), gen -> gen.string().digits().length(5))
                .filter(elementOf(Nested::getAbcElements2).first().field(StringsDef::getE),
                        (String s) -> Integer.parseInt(s) % 2 == 0)
                .create();

        assertThatGraph(result)
                .includingSubtrees("nested.abcElements2[0].def.e")
                .allValuesOfTypeSatisfy(String.class, value ->
                        assertThat(Integer.parseInt(value) % 2).isZero());
    }

    @Test
    void multipleElementOfFilters() {
        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .generate(allStrings(), gen -> gen.string().digits().length(5))
                .filter(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getA),
                        (String s) -> Integer.parseInt(s) % 2 == 0)
                .filter(elementOf(Nested::getAbcElements2).first().field(StringsDef::getE),
                        (String s) -> Integer.parseInt(s) % 3 == 0)
                .create();

        assertThatGraph(result)
                .includingSubtrees("abcElements1[0].a")
                .allValuesOfTypeSatisfy(String.class, value ->
                        assertThat(Integer.parseInt(value) % 2).isZero());

        assertThatGraph(result)
                .includingSubtrees("nested.abcElements2[0].def.e")
                .allValuesOfTypeSatisfy(String.class, value ->
                        assertThat(Integer.parseInt(value) % 3).isZero());
    }

    @Test
    void filterElementField_maxAttemptsExceeded() {
        final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                .filter(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getA),
                        (String s) -> s.equals("will-never-match"));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("filter() predicate rejected too many generated values");
    }
}

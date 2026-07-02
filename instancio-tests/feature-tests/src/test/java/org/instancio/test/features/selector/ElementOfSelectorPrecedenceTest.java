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

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.Api;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@FeatureTag(Feature.ELEMENT_OF_SELECTOR)
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorPrecedenceTest {

    @DisplayName("set(Select.elementOf()) > set(Select.field())")
    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void elementOfWithSet_shouldTakePrecedenceOverRegularFieldSelectorWithSet() {
        final String setValue = "foo";
        final String elements1AValue = "elements1A";
        final String elements2AValue = "elements2A";
        final int elements2AIndex = Instancio.gen().ints().range(0, 10).get();

        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                // Selector precedence should not depend on order of the methods
                .set(elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getA), elements1AValue)
                .set(field(StringsAbc::getA), setValue)
                .set(elementOf(AbcListHolder::getAbcElements2).at(elements2AIndex).field(StringsAbc::getA), elements2AValue)
                .create();

        assertThatGraph(result).hasValuesEqualToExactlyIn(elements1AValue, "abcElements1[*].a");
        assertThatGraph(result).hasValuesEqualToExactlyIn(elements2AValue, "abcElements2[%s].a".formatted(elements2AIndex));
        assertThatGraph(result)
                .excludingSubtrees("abcElements1[*].a", "abcElements2[%s].a".formatted(elements2AIndex))
                .includingSubtrees("**.a")
                .hasAllValuesEqualTo(setValue);
    }

    @DisplayName("generate(Select.elementOf()) > set(Select.field())")
    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void elementOfWithGenerate_shouldTakePrecedenceOverRegularFieldSelectorWithSet() {
        final String setValue = "foo";
        final String elements1AValue = "elements1A";
        final String elements2AValue = "elements2A";
        final int elements2AIndex = Instancio.gen().ints().range(0, 10).get();

        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                // Selector precedence should not depend on order of the methods
                .generate(elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getA), gen -> gen.oneOf(elements1AValue))
                .set(field(StringsAbc::getA), setValue)
                .generate(elementOf(AbcListHolder::getAbcElements2).at(elements2AIndex).field(StringsAbc::getA), gen -> gen.oneOf(elements2AValue))
                .create();

        assertThatGraph(result).hasValuesEqualToExactlyIn(elements1AValue, "abcElements1[*].a");
        assertThatGraph(result).hasValuesEqualToExactlyIn(elements2AValue, "abcElements2[%s].a".formatted(elements2AIndex));
        assertThatGraph(result)
                .excludingSubtrees("abcElements1[*].a", "abcElements2[%s].a".formatted(elements2AIndex))
                .includingSubtrees("**.a")
                .hasAllValuesEqualTo(setValue);
    }

    @DisplayName("generate(Select.elementOf()) > assign(Select.field())")
    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void elementOfWithGenerate_shouldTakePrecedenceOverRegularFieldSelectorWithAssign() {
        final String setValue = "foo";
        final String elements1AValue = "elements1A";
        final String elements2AValue = "elements2A";
        final int elements2AIndex = Instancio.gen().ints().range(0, 10).get();

        // Selector precedence should not depend on order of the methods
        //noinspection CodeBlock2Expr
        final InstancioApi<AbcListHolder> api = Api.shuffleApiOrder(AbcListHolder.class,
                a -> {
                    a.assign(Assign.valueOf(StringsAbc::getA).set(setValue));
                },
                a -> {
                    a.generate(elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getA),
                            gen -> gen.oneOf(elements1AValue));
                },
                a -> {
                    a.generate(elementOf(AbcListHolder::getAbcElements2).at(elements2AIndex).field(StringsAbc::getA),
                            gen -> gen.oneOf(elements2AValue));
                });

        final AbcListHolder result = api.create();

        assertThatGraph(result).hasValuesEqualToExactlyIn(elements1AValue, "abcElements1[*].a");
        assertThatGraph(result).hasValuesEqualToExactlyIn(elements2AValue, "abcElements2[%s].a".formatted(elements2AIndex));
        assertThatGraph(result)
                .excludingSubtrees("abcElements1[*].a", "abcElements2[%s].a".formatted(elements2AIndex))
                .includingSubtrees("**.a")
                .hasAllValuesEqualTo(setValue);
    }
}

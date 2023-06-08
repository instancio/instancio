/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.test.features.conditional;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.When.valueOf;

@FeatureTag(Feature.CONDITIONAL)
@ExtendWith(InstancioExtension.class)
class ConditionalPrecedenceTest {

    private static final String REGULAR_SET = "value-from-set";
    private static final String CONDITIONAL_SET = "from-from-conditional";

    @Test
    void conditionalShouldHaveHigherPrecedenceThanSet1() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .set(field(StringsGhi::getH), REGULAR_SET)
                .when(valueOf(StringsGhi::getG)
                        .satisfies(s -> true)
                        .set(field(StringsGhi::getH), CONDITIONAL_SET))
                .create();

        assertThat(result.h).isEqualTo(CONDITIONAL_SET);
    }

    @Test
    void conditionalShouldHaveHigherPrecedenceThanSet2() {
        final String conditionalMatch = "foo";
        final String conditionalNonMatch = "bar";

        final List<StringsGhi> results = Instancio.ofList(StringsGhi.class)
                .size(100)
                .set(field(StringsGhi::getH), REGULAR_SET)
                .generate(field(StringsGhi::getG), gen -> gen.oneOf(conditionalMatch, conditionalNonMatch))
                .when(valueOf(StringsGhi::getG).is(conditionalMatch).set(field(StringsGhi::getH), CONDITIONAL_SET))
                .create();

        assertThat(results)
                .extracting(StringsGhi::getG)
                .containsOnly(conditionalMatch, conditionalNonMatch);

        assertThat(results).allSatisfy(result -> {
            final String expectedResult = result.g.equals(conditionalMatch)
                    ? CONDITIONAL_SET
                    : REGULAR_SET;

            assertThat(result.h).isEqualTo(expectedResult);
        });
    }
}

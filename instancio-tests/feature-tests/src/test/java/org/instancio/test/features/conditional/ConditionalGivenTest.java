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

import org.instancio.Conditional;
import org.instancio.ConditionalGivenAction;
import org.instancio.Gen;
import org.instancio.Instancio;
import org.instancio.When;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag(Feature.CONDITIONAL)
@ExtendWith(InstancioExtension.class)
class ConditionalGivenTest {

    private static final String ODD = "odd";
    private static final String EVEN = "even";

    private static ConditionalGivenAction whenGiven() {
        return When.given(field(StringsGhi::getG), field(StringsGhi::getI))
                .set((String s) -> s.length() % 2 == 0, EVEN);
    }

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(whenGiven().elseSet(ODD)),
                Arguments.of(whenGiven().elseSupply(() -> ODD)),
                Arguments.of(whenGiven().elseSupply(random -> random.oneOf(ODD))),
                Arguments.of(whenGiven().elseGenerate(Gen.text().pattern(ODD))),
                Arguments.of(whenGiven().elseGenerate(gen -> gen.text().pattern(ODD)))
        );
    }

    @MethodSource("args")
    @ParameterizedTest()
    void verifyConditional(final Conditional conditional) {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .when(conditional)
                .create();

        final String expected = result.g.length() % 2 == 0 ? EVEN : ODD;
        assertThat(result.i).isEqualTo(expected);
    }
}

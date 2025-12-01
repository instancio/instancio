/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.test.features.assign;

import org.instancio.Assign;
import org.instancio.Assignment;
import org.instancio.GetMethodSelector;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.given;
import static org.instancio.Select.field;

@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignPrecedenceTest {

    private static final String VALUE_FROM_SET = "value-from-set";
    private static final String VALUE_FROM_ASSIGNMENT = "value-from-assignment";

    /**
     * Given multiple assignments to the same destination,
     * the last assignment should take precedence.
     */
    @Test
    void lastAssignmentWins() {
        final GetMethodSelector<StringsGhi, String> h = StringsGhi::getH;

        final Assignment[] assignments = {
                Assign.valueOf(StringsGhi::getI).to(h),
                Assign.valueOf(StringsAbc::getA).to(h),
                Assign.valueOf(StringsDef::getF).to(h),
                Assign.valueOf(StringsAbc::getC).to(h),
                Assign.valueOf(StringsDef::getD).to(h) // should win!
        };

        final StringsAbc result = Instancio.of(StringsAbc.class)
                .assign(assignments)
                .create();

        assertThat(result.getDef().getGhi().getH())
                .isNotNull()
                .isEqualTo(result.getDef().getD());
    }

    @Test
    void assignmentShouldHaveHigherPrecedenceThanSet1() {
        final StringsGhi result = Instancio.of(StringsGhi.class)
                .set(field(StringsGhi::getH), VALUE_FROM_SET)
                .assign(given(StringsGhi::getG)
                        .satisfies(s -> true)
                        .set(field(StringsGhi::getH), VALUE_FROM_ASSIGNMENT))
                .create();

        assertThat(result.h).isEqualTo(VALUE_FROM_ASSIGNMENT);
    }

    @Test
    void assignmentShouldHaveHigherPrecedenceThanSet2() {
        final String conditionalMatch = "foo";
        final String conditionalNonMatch = "bar";

        final List<StringsGhi> results = Instancio.ofList(StringsGhi.class)
                .size(100)
                .set(field(StringsGhi::getH), VALUE_FROM_SET)
                .generate(field(StringsGhi::getG), gen -> gen.oneOf(conditionalMatch, conditionalNonMatch))
                .assign(given(StringsGhi::getG)
                        .is(conditionalMatch)
                        .set(field(StringsGhi::getH), VALUE_FROM_ASSIGNMENT))
                .create();

        assertThat(results)
                .extracting(StringsGhi::getG)
                .containsOnly(conditionalMatch, conditionalNonMatch);

        assertThat(results).allSatisfy(result -> {
            final String expectedResult = result.g.equals(conditionalMatch)
                    ? VALUE_FROM_ASSIGNMENT
                    : VALUE_FROM_SET;

            assertThat(result.h).isEqualTo(expectedResult);
        });
    }
}

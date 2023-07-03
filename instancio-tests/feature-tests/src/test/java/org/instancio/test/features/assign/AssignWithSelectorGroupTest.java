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
package org.instancio.test.features.assign;

import org.instancio.Assign;
import org.instancio.Assignment;
import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag({Feature.ASSIGN, Feature.SELECTOR, Feature.SELECT_GROUP})
@ExtendWith(InstancioExtension.class)
class AssignWithSelectorGroupTest {

    private static final String EXPECTED = "foo";

    private static Stream<Arguments> args() {
        final TargetSelector origin = field(StringsAbc::getC);
        final TargetSelector destination = all(
                field(StringsAbc::getA),
                field(StringsAbc::getB),
                field(StringsDef::getD),
                field(StringsDef::getF),
                field(StringsGhi::getI));

        final Predicate<?> predicate = value -> true;

        return Stream.of(
                Arguments.of(Assign.given(origin).satisfies(predicate).set(destination, EXPECTED)),
                Arguments.of(Assign.given(origin, destination).set(predicate, EXPECTED)),
                Arguments.of(Assign.valueOf(origin).to(destination).as(o -> EXPECTED))
        );
    }

    @MethodSource("args")
    @ParameterizedTest
    @DisplayName("Should set all destination selectors to the expected value")
    void destinationGroup(final Assignment assignment) {
        final StringsAbc result = Instancio.of(StringsAbc.class)
                .assign(assignment)
                .create();

        assertThat(result.a).isEqualTo(EXPECTED);
        assertThat(result.b).isEqualTo(EXPECTED);
        assertThat(result.def.d).isEqualTo(EXPECTED);
        assertThat(result.def.f).isEqualTo(EXPECTED);
        assertThat(result.def.ghi.i).isEqualTo(EXPECTED);

        // remaining
        assertThat(result.c).isNotEqualTo(EXPECTED);
        assertThat(result.def.e).isNotEqualTo(EXPECTED);
        assertThat(result.def.ghi.g).isNotEqualTo(EXPECTED);
        assertThat(result.def.ghi.h).isNotEqualTo(EXPECTED);
    }
}

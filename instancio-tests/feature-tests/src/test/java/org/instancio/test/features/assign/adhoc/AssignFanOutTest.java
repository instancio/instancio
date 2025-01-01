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
package org.instancio.test.features.assign.adhoc;

import lombok.Data;
import org.instancio.Assign;
import org.instancio.Assignment;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.instancio.Select.allInts;
import static org.instancio.Select.allStrings;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignFanOutTest {

    private static final String EXPECTED_VALUE = "1";

    private static final List<Assignment> assignments = Arrays.asList(
            Assign.given(int.class).is(1).set(allStrings(), EXPECTED_VALUE),
            Assign.valueOf(int.class).to(allStrings()).as(Object::toString));

    @FieldSource("assignments")
    @ParameterizedTest
    void bottomUp(final Assignment assignment) {
        //@formatter:off
        @Data class L4 { String a, b, c; int i; }
        @Data class L3 { String a, b, c; L4 l4; }
        @Data class L2 { String a, b, c; L3 l3; }
        @Data class L1 { String a, b, c; L2 l2; }
        @Data class L0 { String a, b, c; L1 l1; }
        //@formatter:on

        final L0 result = Instancio.of(L0.class)
                .set(allInts(), 1)
                .assign(assignment)
                .create();

        Stream.of(result, result.l1, result.l1.l2, result.l1.l2.l3, result.l1.l2.l3.l4)
                .forEach(obj -> assertThatObject(obj).hasAllFieldsOfTypeEqualTo(String.class, EXPECTED_VALUE));
    }

    @FieldSource("assignments")
    @ParameterizedTest
    void centreOut(final Assignment assignment) {
        //@formatter:off
        @Data class L4 { String a, b, c; }
        @Data class L3 { String a, b, c; L4 l4; }
        @Data class L2 { String a, b, c; int i; L3 l3; }
        @Data class L1 { String a, b, c; L2 l2; }
        @Data class L0 { String a, b, c; L1 l1; }
        //@formatter:on

        final L0 result = Instancio.of(L0.class)
                .set(allInts(), 1)
                .assign(assignment)
                .create();

        Stream.of(result, result.l1, result.l1.l2, result.l1.l2.l3, result.l1.l2.l3.l4)
                .forEach(obj -> assertThatObject(obj).hasAllFieldsOfTypeEqualTo(String.class, EXPECTED_VALUE));
    }
}

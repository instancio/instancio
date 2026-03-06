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
package org.instancio.test.features.assignmenttype.field;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Select;
import org.instancio.Selector;
import org.instancio.SetMethodSelector;
import org.instancio.TargetSelector;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith.FieldAssignmentOnly
@FeatureTag({Feature.ASSIGNMENT_TYPE, Feature.ASSIGNMENT_TYPE_METHOD})
@ExtendWith(InstancioExtension.class)
class FieldAssignmentUsingSetterSelectorTest {

    private static Stream<Arguments> selectors() {
        final SetMethodSelector<StringHolder, String> setValueMethod = StringHolder::setValue;
        final Selector selectMethod = Select.setter(setValueMethod);

        return Stream.of(
                Arguments.of(Select.all(selectMethod)),
                Arguments.of(Select.setter(StringHolder.class, "setValue")),
                Arguments.of(Select.setter(StringHolder.class, "setValue", String.class)),
                Arguments.of(selectMethod),
                Arguments.of(setValueMethod),
                Arguments.of(selectMethod.atDepth(1)),
                Arguments.of(selectMethod.within(selectMethod.toScope())),
                Arguments.of(selectMethod.within(Select.scope(StringHolder.class))),
                Arguments.of(selectMethod.within(Select.scope(StringHolder.class, "value")))
        );
    }

    @MethodSource("selectors")
    @ParameterizedTest
    void shouldThrowErrorUsingSetterSelector(final TargetSelector selector) {
        final InstancioApi<StringHolder> api = Instancio.of(StringHolder.class)
                .set(selector, "foo");

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("setter() selector cannot be used with AssignmentType.FIELD");
    }
}

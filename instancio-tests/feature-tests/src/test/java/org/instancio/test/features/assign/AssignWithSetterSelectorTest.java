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
import org.instancio.Instancio;
import org.instancio.Selector;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.OnSetMethodUnmatched;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.dynamic.DynPhone;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.Select.setter;

@RunWith.MethodAssignmentOnly
@FeatureTag({Feature.ASSIGN, Feature.ASSIGNMENT_TYPE_METHOD})
@ExtendWith(InstancioExtension.class)
class AssignWithSetterSelectorTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
            .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE)
            .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.FAIL);

    private static Stream<Arguments> oneAndTwoSelectors() {
        return Stream.of(
                Arguments.of(field(StringFields::getOne), field(StringFields::getTwo)),
                Arguments.of(field(StringFields::getOne), setter(StringFields::setTwo)),
                Arguments.of(setter(StringFields::setOne), field(StringFields::getTwo)),
                Arguments.of(setter(StringFields::setOne), setter(StringFields::setTwo)));
    }

    @MethodSource("oneAndTwoSelectors")
    @ParameterizedTest
    void assignValueOfOneToTwo(final Selector one, final Selector two) {
        final StringFields result = Instancio.of(StringFields.class)
                .assign(Assign.valueOf(one).to(two))
                .create();

        assertThat(result.getOne()).isEqualTo(result.getTwo());
    }

    @Test
    void assignViaSetterConditionally() {
        final StringFields result = Instancio.of(StringFields.class)
                .set(setter(StringFields::setOne), "one")
                .assign(Assign.given(StringFields::getOne)
                        .is("one")
                        .set(setter(StringFields::setTwo), "two"))
                .create();

        assertThat(result.getOne()).isEqualTo("one");
        assertThat(result.getTwo()).isEqualTo("two");
    }

    @Test
    void assignDynamicPojo() {
        final DynPhone result = Instancio.of(DynPhone.class)
                .assign(Assign
                        .valueOf(setter(DynPhone::setCountryCode))
                        .to(setter(DynPhone::setNumber)))
                .create();

        assertThat(result.getNumber()).isEqualTo(result.getCountryCode());
    }
}

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
package org.instancio.test.protobuf;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.test.prototobuf.Proto;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.scope;
import static org.instancio.Select.setter;
import static org.instancio.Select.types;

@ExtendWith(InstancioExtension.class)
class ProtoSelectorTest {

    @Test
    void allClassWithEnum() {
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .set(all(Proto.Gender.class), Proto.Gender.FEMALE)
                .create();

        assertThat(result.getGender()).isEqualTo(Proto.Gender.FEMALE);
    }

    @Test
    void targetingObjectMatchesStrings() {
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .set(all(Object.class), "any")
                .create();

        // Internally proto strings are declared as Object
        assertThat(result.getName())
                .isEqualTo(result.getNickname().getValue())
                .isEqualTo("any");
    }

    @Test
    void fieldSelectorByName(@Given final String value) {
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .set(field("name_"), value)
                .create();

        assertThat(result.getName()).isEqualTo(value);
    }

    @Test
    void fieldSelectorWithScope(@Given final String value) {
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .set(field(Proto.Phone::getNumber).within(scope(Proto.Address.class)), value)
                .create();

        result.getAddress().getPhoneNumbersList().forEach(phone ->
                assertThat(phone.getNumber()).isEqualTo(value)
        );
    }

    @Test
    void primitiveAndWrapperSelector(@Given final int value) {
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .set(allInts(), value)
                .create();

        assertThat(result.getAge()).isEqualTo(value);
    }

    @Test
    void predicateBuilderFieldSelector(@Given final String value) {
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .set(fields().matching("name_|city_|number_"), value)
                .create();

        assertThat(result.getName()).isEqualTo(value);
        assertThat(result.getAddress().getCity()).isEqualTo(value);
        assertThat(result.getAddress().getPhoneNumbersList()).isNotEmpty().allSatisfy(phone ->
                assertThat(phone.getNumber()).isEqualTo(value)
        );
    }

    @Test
    void predicateBuilderTypeSelector() {
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .set(types().of(Proto.Phone.class), Proto.Phone.getDefaultInstance())
                .create();

        assertThat(result.getAddress().getPhoneNumbersList()).isNotEmpty()
                .allSatisfy(phone -> assertThat(phone).isEqualTo(Proto.Phone.getDefaultInstance()));
    }

    @Test
    void predicateTypeEqualsString(@Given final String value) {
        final Proto.Phone result = Instancio.of(Proto.Phone.class)
                .set(types(t -> t == String.class), value)
                .create();

        assertThat(result.getCountryCode())
                .isEqualTo(result.getNumber())
                .isEqualTo(value);
    }

    /**
     * Verify selector with regular POJO works in presence of Instancio protobuf SPIs.
     */
    @EnumSource(AssignmentType.class)
    @ParameterizedTest
    void regularPojo_withFieldAndMethodAssignmentType(
            final AssignmentType assignmentType,
            @Given final String expected) {

        //@formatter:off
        class Pojo {
            private String value;
            String getValue() { return value; }
            void setValue(String value) { this.value = value; }
        }
        //@formatter:on

        // ProtoGetterMethodFieldResolver.resolveField() returns null for non-proto classes,
        // delegating to the default resolver. This test exercises that early-return branch.
        final Pojo result = Instancio.of(Pojo.class)
                .withSetting(Keys.ASSIGNMENT_TYPE, assignmentType)
                .set(field(Pojo::getValue), expected)
                .create();

        assertThat(result.getValue()).isEqualTo(expected);
    }

    @FeatureTag(Feature.UNSUPPORTED)
    @Test
    void setterSelector() {
        final InstancioApi<Proto.Person> api = Instancio.of(Proto.Person.class)
                .withSetting(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                .set(setter(Proto.Person.Builder::setName), "any")
                .verbose();

        assertThatThrownBy(api::create).isExactlyInstanceOf(UnusedSelectorException.class);
    }
}

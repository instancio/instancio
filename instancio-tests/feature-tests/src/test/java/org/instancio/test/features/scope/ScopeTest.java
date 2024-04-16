/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.test.features.scope;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.pojo.person.PersonHolder;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.PhoneType;
import org.instancio.test.support.pojo.person.PhoneWithType;
import org.instancio.test.support.pojo.person.RichPerson;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.scope;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.SELECTOR, Feature.SCOPE})
@ExtendWith(InstancioExtension.class)
class ScopeTest {

    @Test
    @DisplayName("Selectors specified last have higher precedence")
    void selectorsWithScopePrecedence() {
        final PersonHolder result = Instancio.of(PersonHolder.class)
                .set(allStrings(), null)
                .set(allStrings().within(scope(RichPerson.class, "phone")), "foo")
                .set(allStrings().within(scope(RichPerson.class, "address1"), scope(Phone.class)), "bar")
                .create();

        // foo and bar
        assertThatObject(result.getRichPerson().getPhone()).hasAllFieldsOfTypeEqualTo(String.class, "foo");

        assertThat(result.getRichPerson().getAddress1().getPhoneNumbers()).allSatisfy(phone ->
                assertThatObject(phone).hasAllFieldsOfTypeEqualTo(String.class, "bar"));

        // all other strings are null
        assertThat(result.getPerson().getAddress().getPhoneNumbers()).allSatisfy(phone ->
                assertThatObject(phone).hasAllFieldsOfTypeSetToNull(String.class));

        assertThatObject(result.getRichPerson().getAddress1()).hasAllFieldsOfTypeSetToNull(String.class);

        assertThat(result.getRichPerson().getAddress2().getPhoneNumbers()).allSatisfy(phone ->
                assertThatObject(phone).hasAllFieldsOfTypeSetToNull(String.class));
    }

    @Test
    void fieldScope() {
        final String expected = "foo";
        final StringFields result = Instancio.of(StringFields.class)
                .set(all(
                        allStrings().within(scope(StringFields::getOne)),
                        allStrings().within(scope(StringFields::getFour))
                ), expected)
                .create();

        assertThat(result.getOne())
                .isEqualTo(expected)
                .isEqualTo(result.getFour())
                .isNotEqualTo(result.getTwo())
                .isNotEqualTo(result.getThree());
    }

    @Test
    void scopeWithSubtype() {
        final List<Phone> results = Instancio.ofList(Phone.class)
                .size(Constants.SAMPLE_SIZE_DD)
                .subtype(all(Phone.class), PhoneWithType.class)
                .set(all(PhoneType.class).within(scope(PhoneWithType.class)), PhoneType.OTHER)
                .create();

        assertThat(results)
                .hasOnlyElementsOfType(PhoneWithType.class)
                .allSatisfy(result -> {
                    PhoneWithType phoneWithType = (PhoneWithType) result;
                    assertThat(phoneWithType.getPhoneType()).isEqualTo(PhoneType.OTHER);
                });
    }

    @Test
    void shouldAllowConsecutiveScopesToMatchAGivenNode() {
        final StringHolder result = Instancio.of(StringHolder.class)
                .set(allStrings().within(
                                scope(StringHolder.class),
                                scope(StringHolder.class),
                                scope(String.class),
                                scope(String.class)),
                        "foo")
                .create();

        assertThat(result.getValue()).isEqualTo("foo");
    }
}

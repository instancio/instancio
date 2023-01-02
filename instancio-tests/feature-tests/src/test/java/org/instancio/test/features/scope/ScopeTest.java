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
package org.instancio.test.features.scope;

import org.instancio.Instancio;
import org.instancio.test.support.pojo.person.PersonHolder;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.RichPerson;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.scope;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.SELECTOR, Feature.SCOPE})
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
}

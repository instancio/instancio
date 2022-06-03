/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.test.features.oncomplete;

import org.instancio.Instancio;
import org.instancio.Scope;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.person.PersonHolder;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.RichPerson;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.scope;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({
        Feature.ON_COMPLETE,
        Feature.SCOPE,
        Feature.SELECTOR
})
@ExtendWith(InstancioExtension.class)
class OnCompleteWithScopeNumberOfCallbackInvocationsTest {

    private static final String FOO = "foo";
    private static final int COLLECTION_SIZE = 3;
    private static final int EXPECTED_CALLBACKS_FOR_ONE_PHONE_INSTANCE = 2;
    private static final int EXPECTED_CALLBACKS_FOR_PHONE_LIST =
            COLLECTION_SIZE * EXPECTED_CALLBACKS_FOR_ONE_PHONE_INSTANCE;

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.COLLECTION_MIN_SIZE, COLLECTION_SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, COLLECTION_SIZE);

    @Test
    void onCompleteThenSet() {
        final AtomicInteger callback1Count = new AtomicInteger();
        final AtomicInteger callback2Count = new AtomicInteger();

        final Scope[] scope1 = {scope(RichPerson.class, "phone")};
        final Scope[] scope2 = {scope(RichPerson.class, "address1"), scope(Phone.class)};

        final PersonHolder result = Instancio.of(PersonHolder.class)
                .onComplete(allStrings().within(scope1), (String s) -> {
                    callback1Count.incrementAndGet();
                    assertThat(s).isEqualTo(FOO);
                })
                .onComplete(allStrings().within(scope2), (String s) -> {
                    callback2Count.incrementAndGet();
                    assertThat(s).isEqualTo(FOO);
                })
                .set(allStrings(), null)
                .set(allStrings().within(scope1), FOO)
                .set(allStrings().within(scope2), FOO)
                .create();

        assertThat(callback1Count.get()).isEqualTo(EXPECTED_CALLBACKS_FOR_ONE_PHONE_INSTANCE);
        assertThat(callback2Count.get()).isEqualTo(EXPECTED_CALLBACKS_FOR_PHONE_LIST);
        assertAllStringsNullExceptFoos(result);
    }

    @Test
    void setThenOnComplete() {
        final AtomicInteger callback1Count = new AtomicInteger();
        final AtomicInteger callback2Count = new AtomicInteger();

        final Scope[] scope1 = {scope(RichPerson.class, "phone")};
        final Scope[] scope2 = {scope(RichPerson.class, "address1"), scope(Phone.class)};

        final PersonHolder result = Instancio.of(PersonHolder.class)
                .set(allStrings(), null)
                .set(allStrings().within(scope1), FOO)
                .set(allStrings().within(scope2), FOO)
                .onComplete(allStrings().within(scope1), (String s) -> {
                    callback1Count.incrementAndGet();
                    assertThat(s).isEqualTo(FOO);
                })
                .onComplete(allStrings().within(scope2), (String s) -> {
                    callback2Count.incrementAndGet();
                    assertThat(s).isEqualTo(FOO);
                })
                .create();

        assertThat(callback1Count.get()).isEqualTo(EXPECTED_CALLBACKS_FOR_ONE_PHONE_INSTANCE);
        assertThat(callback2Count.get()).isEqualTo(EXPECTED_CALLBACKS_FOR_PHONE_LIST);
        assertAllStringsNullExceptFoos(result);
    }

    @Test
    void setThenOnCompleteWithGroup() {
        final AtomicInteger callbackCount = new AtomicInteger();
        final Scope[] scope1 = {scope(RichPerson.class, "phone")};
        final Scope[] scope2 = {scope(RichPerson.class, "address1"), scope(Phone.class)};

        final PersonHolder result = Instancio.of(PersonHolder.class)
                .set(allStrings(), null)
                .set(all(
                        allStrings().within(scope1),
                        allStrings().within(scope2)), FOO)
                .onComplete(all(
                        allStrings().within(scope1),
                        allStrings().within(scope2)), (String s) -> {

                    assertThat(s).isEqualTo(FOO);
                    callbackCount.incrementAndGet();
                })
                .create();

        // RichPerson.phone + RichPerson.address1.phoneNumbers list
        assertThat(callbackCount.get()).isEqualTo(
                EXPECTED_CALLBACKS_FOR_PHONE_LIST + EXPECTED_CALLBACKS_FOR_ONE_PHONE_INSTANCE);
        assertAllStringsNullExceptFoos(result);
    }

    /**
     * <ul>
     *   <li>All strings 'foo' in RichPerson.phone</li>
     *   <li>All strings 'foo' in RichPerson.address1.phoneNumbers</li>
     *   <li>All other string null</li>
     * </ul>
     */
    private void assertAllStringsNullExceptFoos(final PersonHolder result) {
        // null fields
        assertThatObject(result.getPerson()).hasAllFieldsOfTypeSetToNull(String.class);
        assertThatObject(result.getPerson().getAddress()).hasAllFieldsOfTypeSetToNull(String.class);
        assertThatObject(result.getRichPerson()).hasAllFieldsOfTypeSetToNull(String.class);
        assertThatObject(result.getRichPerson().getAddress1()).hasAllFieldsOfTypeSetToNull(String.class);
        assertThatObject(result.getRichPerson().getAddress2()).hasAllFieldsOfTypeSetToNull(String.class);
        assertPhoneFieldsContainOnly(result.getPerson().getAddress().getPhoneNumbers(), null);
        assertPhoneFieldsContainOnly(result.getRichPerson().getAddress2().getPhoneNumbers(), null);

        // "foo" fields
        assertPhoneFieldsContainOnly(result.getRichPerson().getPhone(), FOO);
        assertPhoneFieldsContainOnly(result.getRichPerson().getAddress1().getPhoneNumbers(), FOO);
    }

    private void assertPhoneFieldsContainOnly(final List<Phone> phones, final String expected) {
        assertThat(phones).isNotEmpty().allSatisfy(p -> assertPhoneFieldsContainOnly(p, expected));
    }

    private void assertPhoneFieldsContainOnly(final Phone phone, final String expected) {
        assertThat(phone.getCountryCode()).isEqualTo(expected);
        assertThat(phone.getNumber()).isEqualTo(expected);
    }
}

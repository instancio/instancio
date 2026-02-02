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
package org.instancio.test.features.oncomplete;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.scope;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({
        Feature.GENERATOR,
        Feature.ON_COMPLETE,
        Feature.SCOPE,
        Feature.SELECTOR
})
@ExtendWith(InstancioExtension.class)
class OnCompleteWithScopeTest {

    private static final int COLLECTION_SIZE = 3;

    // Set fixed collection size, so we can verify the number of callback invocations
    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.COLLECTION_MIN_SIZE, COLLECTION_SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, COLLECTION_SIZE);

    // Phone has 2 String fields; therefore number of callbacks = 2 x num of phone instances in List<Phone>
    private static final int EXPECTED_NUM_CALLBACKS = COLLECTION_SIZE * 2;
    private static final String FOO = "foo";

    private final AtomicInteger callbacksCount = new AtomicInteger();

    @Test
    @DisplayName("Two callbacks with equal scope")
    void twoCallbacksWithEqualScopes() {
        Instancio.of(StringHolder.class)
                .onComplete(allStrings(), (String s) -> callbacksCount.incrementAndGet())
                .onComplete(allStrings(), (String s) -> callbacksCount.incrementAndGet())
                .create();

        assertThat(callbacksCount.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("Two callbacks with scopes that equivalent but not equal")
    void twoCallbacksWithDifferentScopes() {
        // The scopes are equivalent but not equal
        Instancio.of(StringHolder.class)
                .onComplete(allStrings(), (String s) -> callbacksCount.incrementAndGet())
                .onComplete(allStrings().within(scope(StringHolder.class)), (String s) -> callbacksCount.incrementAndGet())
                .create();

        assertThat(callbacksCount.get()).isEqualTo(2);
    }

    @Test
    void setThenOnComplete() {
        final Address result = Instancio.of(Address.class)
                .supply(allStrings().within(scope(Address.class), scope(Phone.class)), random -> FOO)
                .onComplete(allStrings().within(scope(Address.class), scope(Phone.class)), (String s) -> {
                    callbacksCount.incrementAndGet();
                    assertThat(s).isEqualTo(FOO);
                })
                .create();

        assertThat(result.getPhoneNumbers()).extracting(Phone::getCountryCode).containsOnly(FOO);
        assertThat(result.getPhoneNumbers()).extracting(Phone::getNumber).containsOnly(FOO);
        assertThatObject(result).doesNotHaveAllFieldsOfTypeEqualTo(String.class, FOO);
        assertThat(callbacksCount.get()).isEqualTo(EXPECTED_NUM_CALLBACKS);
    }

    @Test
    void onCompleteThenSet() {
        final Address result = Instancio.of(Address.class)
                .onComplete(allStrings().within(scope(Address.class), scope(Phone.class)), (String s) -> {
                    callbacksCount.incrementAndGet();
                    assertThat(s).isEqualTo(FOO);
                })
                .supply(allStrings().within(scope(Address.class), scope(Phone.class)), random -> FOO)
                .create();

        assertThat(result.getPhoneNumbers()).extracting(Phone::getCountryCode).containsOnly(FOO);
        assertThat(result.getPhoneNumbers()).extracting(Phone::getNumber).containsOnly(FOO);
        assertThatObject(result).doesNotHaveAllFieldsOfTypeEqualTo(String.class, FOO);
        assertThat(callbacksCount.get()).isEqualTo(EXPECTED_NUM_CALLBACKS);
    }
}

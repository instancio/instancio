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
package org.instancio.test.features.mode;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.internal.util.Sonar;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.PhoneWithType;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;

/**
 * Verify that when a class node is set to null,
 * the node's children don't trigger an unused selector error.
 */
@FeatureTag({
        Feature.MODE,
        Feature.GENERATE,
        Feature.IGNORE,
        Feature.ON_COMPLETE,
        Feature.SET,
        Feature.WITH_NULLABLE
})
// assertion not need; no "unused selector" error means test passed
@SuppressWarnings(Sonar.ADD_ASSERTION)
class StrictModeWithNullValuesTest {

    private static InstancioApi<Person> personWithNullAddress() {
        return Instancio.of(Person.class).set(all(Address.class), null);
    }

    private static Stream<Arguments> phoneNumberSelectors() {
        return Stream.of(
                Arguments.of(field(Phone.class, "number")),
                Arguments.of(fields().ofType(String.class).named("number"))
        );
    }

    @ParameterizedTest
    @MethodSource("phoneNumberSelectors")
    void withNullable(final TargetSelector selector) {
        personWithNullAddress().withNullable(selector).create();
    }

    @ParameterizedTest
    @MethodSource("phoneNumberSelectors")
    void ignore(final TargetSelector selector) {
        personWithNullAddress().ignore(selector).create();
    }

    @ParameterizedTest
    @MethodSource("phoneNumberSelectors")
    void set(final TargetSelector selector) {
        personWithNullAddress().set(selector, "foo").create();
    }

    @ParameterizedTest
    @MethodSource("phoneNumberSelectors")
    void generate(final TargetSelector selector) {
        personWithNullAddress().generate(selector, gen -> gen.string().digits()).create();
    }

    @ParameterizedTest
    @MethodSource("phoneNumberSelectors")
    void onComplete(final TargetSelector selector) {
        final AtomicInteger callbackCount = new AtomicInteger();
        personWithNullAddress()
                .onComplete(selector, (String number) -> callbackCount.incrementAndGet())
                .create();

        assertThat(callbackCount.get()).isZero();
    }

    @Test
    void subtype() {
        personWithNullAddress().subtype(all(Phone.class), PhoneWithType.class).create();
        personWithNullAddress().subtype(types().of(Phone.class), PhoneWithType.class).create();
    }
}

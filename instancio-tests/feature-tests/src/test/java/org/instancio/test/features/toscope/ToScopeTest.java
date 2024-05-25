/*
 *  Copyright 2022-2024 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.test.features.toscope;

import org.instancio.ConvertibleToScope;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.collections.lists.TwoListsOfInteger;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag(Feature.TO_SCOPE)
@ExtendWith(InstancioExtension.class)
class ToScopeTest {

    private static final String FOO = "foo";

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UsingPrimitiveAndWrapperSelectorImplTest {
        private Stream<Arguments> selectors() {
            return Stream.of(
                    Arguments.of(
                            field("list1"),
                            field("list2")),

                    Arguments.of(
                            field(TwoListsOfInteger.class, "list1"),
                            field(TwoListsOfInteger.class, "list2")));
        }

        @ParameterizedTest
        @MethodSource("selectors")
        void fromField(final ConvertibleToScope list1Selector, final ConvertibleToScope list2Selector) {
            final TwoListsOfInteger result = Instancio.of(TwoListsOfInteger.class)
                    .set(allInts().within(list1Selector.toScope()), 1)
                    .set(allInts().within(list2Selector.toScope()), 2)
                    .create();

            assertThat(result.getList1()).containsOnly(1);
            assertThat(result.getList2()).containsOnly(2);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ScopeCreatedFromSelectorImplTest {

        private Stream<Arguments> selectors() {
            return Stream.of(
                    Arguments.of(all(Phone.class)),
                    Arguments.of(field(Address.class, "phoneNumbers")),
                    Arguments.of(field("phoneNumbers")));
        }

        @ParameterizedTest
        @MethodSource("selectors")
        void verifyToScope(final ConvertibleToScope selector) {
            final Address result = Instancio.of(Address.class)
                    .set(allStrings().within(selector.toScope()), FOO)
                    .create();

            assertExpectedStrings(result);
        }
    }

    private static void assertExpectedStrings(final Address result) {
        assertThat(result.getStreet()).isNotEqualTo(FOO);
        assertThat(result.getCity()).isNotEqualTo(FOO);
        assertThat(result.getCountry()).isNotEqualTo(FOO);
        assertThat(result.getPhoneNumbers()).allSatisfy(phone ->
                assertThatObject(phone).hasAllFieldsOfTypeEqualTo(String.class, FOO));
    }
}

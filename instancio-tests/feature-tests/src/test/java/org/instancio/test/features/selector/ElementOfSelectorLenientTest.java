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
package org.instancio.test.features.selector;

import org.instancio.GroupableSelector;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;

@FeatureTag(Feature.ELEMENT_OF_SELECTOR)
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorLenientTest {

    private static final String VALUE = "foo";

    @Nested
    class ElementOf {

        @Test
        void lenient() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .set(elementOf(field(Address::getPhoneNumbers)).lenient(), VALUE)
                    .create();

            assertThat(result).isNotNull();
        }

        @Test
        void strict() {
            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .set(elementOf(field(Address::getPhoneNumbers)), VALUE);

            assertThatThrownBy(api::create).isExactlyInstanceOf(UnusedSelectorException.class);
        }
    }

    @Nested
    class ElementOfAt {

        @Test
        void lenient() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .set(elementOf(field(Address::getPhoneNumbers)).at(1).lenient(), VALUE)
                    .create();

            assertThat(result).isNotNull();
        }

        @Test
        void strict() {
            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .set(elementOf(field(Address::getPhoneNumbers)).at(1), VALUE);

            assertThatThrownBy(api::create).isExactlyInstanceOf(UnusedSelectorException.class);
        }
    }

    @Nested
    class ElementOfArg {

        private static final GroupableSelector ELEMENT_OF_ARG = field(Address::getPhoneNumbers);

        @Test
        void lenient() {
            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .set(elementOf(ELEMENT_OF_ARG.lenient()), VALUE);

            assertThatThrownBy(api::create).isExactlyInstanceOf(UnusedSelectorException.class);
        }

        @Test
        void strict() {
            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .set(elementOf(ELEMENT_OF_ARG), VALUE);

            assertThatThrownBy(api::create).isExactlyInstanceOf(UnusedSelectorException.class);
        }
    }

    @Nested
    class Target {

        private static final GroupableSelector TARGET = field(Person::getName);

        @Test
        void lenient() {
            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .set(elementOf(AbcListHolder::getAbcElements1).target(TARGET.lenient()), VALUE);

            assertThatThrownBy(api::create).isExactlyInstanceOf(UnusedSelectorException.class);
        }

        @Test
        void strict() {
            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .set(elementOf(AbcListHolder::getAbcElements1).target(TARGET), VALUE);

            assertThatThrownBy(api::create).isExactlyInstanceOf(UnusedSelectorException.class);
        }
    }

}

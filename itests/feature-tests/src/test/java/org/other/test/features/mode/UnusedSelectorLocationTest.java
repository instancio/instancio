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
package org.other.test.features.mode;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Selector;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.test.support.asserts.UnusedSelectorsAssert.ApiMethod;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.PersonHolder;
import org.instancio.test.support.pojo.person.RichPerson;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.Year;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.instancio.Select.all;
import static org.instancio.Select.allBytes;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;
import static org.instancio.test.support.asserts.UnusedSelectorsAssert.assertUnusedSelectorMessage;

@FeatureTag({Feature.MODE, Feature.SELECTOR})
class UnusedSelectorLocationTest {

    @Test
    void unused() {
        final Selector yearSelector = all(Year.class);
        final InstancioApi<Person> api = Instancio.of(Person.class)
                .set(all(Timestamp.class), null)
                .ignore(all(
                        all(Instant.class),
                        yearSelector
                ))
                .supply(field("name").within(scope(Address.class)), () -> fail("not called"))
                .ignore(field(StringHolder.class, "value"))
                .ignore(allStrings().within(scope(PersonHolder.class), scope(RichPerson.class)))
                .supply(allBytes(), () -> fail("not called"));

        assertThatThrownBy(api::create)
                .isInstanceOf(UnusedSelectorException.class)
                .satisfies(ex -> assertUnusedSelectorMessage(ex.getMessage())
                        .hasUnusedSelectorCount(7)
                        .containsOnly(ApiMethod.GENERATE_SET_SUPPLY, ApiMethod.IGNORE)
                        .containsUnusedSelectorAt(Timestamp.class, line(52))
                        .containsUnusedSelectorAt(Instant.class, line(54))
                        .containsUnusedSelectorAt(Year.class, line(50))
                        .containsUnusedSelectorAt(Person.class, "name", line(57))
                        .containsUnusedSelectorAt(StringHolder.class, "value", line(58))
                        .containsUnusedSelectorAt(String.class, line(59))
                        .containsUnusedSelectorAt("allBytes()", line(60)));
    }

    private static String line(final int line) {
        return String.format("at org.other.test.features.mode.UnusedSelectorLocationTest.unused(UnusedSelectorLocationTest.java:%s)", line);
    }
}

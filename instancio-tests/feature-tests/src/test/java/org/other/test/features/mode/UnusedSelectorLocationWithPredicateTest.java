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
package org.other.test.features.mode;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.PersonName;
import org.instancio.test.support.pojo.person.Pojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.fail;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;
import static org.instancio.test.support.UnusedSelectorsAssert.assertThrowsUnusedSelectorException;

@FeatureTag({Feature.MODE, Feature.PREDICATE_SELECTOR})
class UnusedSelectorLocationWithPredicateTest {

    @Test
    void unused() {
        final TargetSelector timestampSelector = types().of(Timestamp.class);
        final InstancioApi<Person> api = Instancio.of(Person.class)
                .set(timestampSelector, null)
                .ignore(types().annotated(Pojo.class).annotated(PersonName.class))
                .supply(fields().named("foo"), () -> fail("not called"))
                .ignore(types(klass -> false))
                .supply(fields(field -> false), () -> fail("not called"));

        assertThrowsUnusedSelectorException(api)
                .hasUnusedSelectorCount(5)
                .unusedGeneratorSelectorAt(timestampSelector, line(42))
                .unusedIgnoreSelectorAt(types().annotated(Pojo.class).annotated(PersonName.class), line(43))
                .unusedGeneratorSelectorAt(fields().named("foo"), line(44))
                .unusedIgnoreSelectorAt(types(klass -> false), line(45))
                .unusedGeneratorSelectorAt(fields(field -> false), line(46));
    }

    private static String line(final int line) {
        return String.format("at org.other.test.features.mode.UnusedSelectorLocationWithPredicateTest" +
                ".unused(UnusedSelectorLocationWithPredicateTest.java:%s)", line);
    }
}

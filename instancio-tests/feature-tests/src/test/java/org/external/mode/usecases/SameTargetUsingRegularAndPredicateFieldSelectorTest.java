/*
 * Copyright 2022-2025 the original author or authors.
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
package org.external.mode.usecases;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.test.support.UnusedSelectorsAssert.assertThrowsUnusedSelectorException;
import static org.instancio.test.support.UnusedSelectorsAssert.line;

@FeatureTag(Feature.MODE)
@ExtendWith(InstancioExtension.class)
class SameTargetUsingRegularAndPredicateFieldSelectorTest {

    @Test
    void regularAndPredicateSelectors() {
        // Since regular selector takes precedence predicate selector, the latter is unused
        final InstancioApi<Person> api = Instancio.of(Person.class)
                .set(field(Address.class, "city"), "foo")
                .set(fields().named("city"), "bar");

        assertThrowsUnusedSelectorException(api)
                .hasUnusedSelectorCount(1)
                .setSelector(fields().named("city"), line(getClass(), 42));
    }
}

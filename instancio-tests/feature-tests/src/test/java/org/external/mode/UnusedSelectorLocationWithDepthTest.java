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
package org.external.mode;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.PersonName;
import org.instancio.test.support.pojo.person.Pojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.fail;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;
import static org.instancio.test.support.UnusedSelectorsAssert.assertThrowsUnusedSelectorException;
import static org.instancio.test.support.UnusedSelectorsAssert.line;

@FeatureTag({Feature.MODE, Feature.PREDICATE_SELECTOR})
@ExtendWith(InstancioExtension.class)
class UnusedSelectorLocationWithDepthTest {

    @Test
    void unused() {
        final TargetSelector timestampSelector = types().of(Timestamp.class).atDepth(1);
        final InstancioApi<Person> api = Instancio.of(Person.class)
                .withMaxDepth(0) // all selectors with greater depth are unused
                .set(timestampSelector, null)
                .ignore(types().annotated(Pojo.class).annotated(PersonName.class).atDepth(2))
                .supply(fields().named("foo").atDepth(3), () -> fail("not called"))
                .ignore(types(klass -> false).atDepth(o -> true))
                .supply(fields(field -> false).atDepth(o -> true), () -> fail("not called"));

        int l = 48;
        assertThrowsUnusedSelectorException(api)
                .hasUnusedSelectorCount(5)
                .setSelector(timestampSelector, line(getClass(), 44)) // Special case: selector assigned to variable
                .ignoreSelector(types().annotated(Pojo.class).annotated(PersonName.class).atDepth(2), line(getClass(), l++))
                .supplySelector(fields().named("foo").atDepth(3), line(getClass(), l++))
                .ignoreSelector(types(klass -> false).atDepth(o -> true), line(getClass(), l++))
                .supplySelector(fields(field -> false).atDepth(o -> true), line(getClass(), l++));
    }
}

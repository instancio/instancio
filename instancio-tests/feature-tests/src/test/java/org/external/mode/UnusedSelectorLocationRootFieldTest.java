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
package org.external.mode;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.fail;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;
import static org.instancio.test.support.UnusedSelectorsAssert.assertThrowsUnusedSelectorException;
import static org.instancio.test.support.UnusedSelectorsAssert.line;

@FeatureTag({Feature.MODE, Feature.SELECTOR})
class UnusedSelectorLocationRootFieldTest {

    @Test
    void unused() {
        // Root field, specified without the class
        final TargetSelector rootClassFieldSelector = field("address").within(scope(byte.class));

        // The above selector gets processed to include the root class,
        // therefore when asserting for unused selectors, we must use the processed selector
        final TargetSelector expected = field(Person.class, "address").within(scope(byte.class));

        final InstancioApi<Person> api = Instancio.of(Person.class)
                .supply(rootClassFieldSelector, () -> fail("not called"));

        assertThrowsUnusedSelectorException(api)
                .hasUnusedSelectorCount(1)
                .generatorSelector(expected, line(getClass(), 38));
    }

}

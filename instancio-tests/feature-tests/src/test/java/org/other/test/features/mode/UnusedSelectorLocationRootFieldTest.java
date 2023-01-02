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
import org.instancio.exception.UnusedSelectorException;
import org.instancio.test.support.asserts.UnusedSelectorsAssert.ApiMethod;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;
import static org.instancio.test.support.asserts.UnusedSelectorsAssert.assertUnusedSelectorMessage;

@FeatureTag({Feature.MODE, Feature.SELECTOR})
class UnusedSelectorLocationRootFieldTest {

    @Test
    void unused() {
        final InstancioApi<Person> api = Instancio.of(Person.class)
                // invalid scope so selector is never used
                .supply(field("address").within(scope(byte.class)), () -> fail("not called"));

        assertThatThrownBy(api::create)
                .isInstanceOf(UnusedSelectorException.class)
                .satisfies(ex -> assertUnusedSelectorMessage(ex.getMessage())
                        .hasUnusedSelectorCount(1)
                        .containsOnly(ApiMethod.GENERATE_SET_SUPPLY)
                        .containsUnusedSelectorAt(Person.class, "address", line(40)));
    }

    private static String line(final int line) {
        return String.format("at org.other.test.features.mode.UnusedSelectorLocationRootFieldTest" +
                ".unused(UnusedSelectorLocationRootFieldTest.java:%s)", line);
    }
}

/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.test.features.assign;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.UnresolvedAssignmentException;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

@FeatureTag({Feature.ASSIGN, Feature.IGNORE})
@ExtendWith(InstancioExtension.class)
class AssignWithIgnoreTest {

    @Test
    void ignoreOrigin() {
        final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                .ignore(field(StringsAbc::getB))
                .assign(Assign.given(StringsAbc::getB).satisfies(o -> true).set(field(StringsAbc::getC), "C"));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnresolvedAssignmentException.class)
                .hasMessageContaining("unresolved assignment");
    }

    @Test
    void ignoreDestination() {
        final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                .ignore(field(StringsAbc::getA))
                .ignore(field(StringsAbc::getC))
                .assign(Assign.given(StringsAbc::getB).satisfies(o -> true)
                        .set(field(StringsAbc::getA), "A")
                        .set(field(StringsAbc::getC), "C"));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnusedSelectorException.class)
                .hasMessageContainingAll("field(StringsAbc, \"a\")", "field(StringsAbc, \"c\")");
    }
}

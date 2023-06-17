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
package org.instancio.test.features.assign;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.TypeToken;
import org.instancio.exception.UnresolvedAssignmentException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.MultipleClassesWithId;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;

@FeatureTag({Feature.ASSIGN, Feature.SCOPE})
@ExtendWith(InstancioExtension.class)
class AssignMultipleOriginMatchesTest {

    @Test
    void originMatchesMultipleFieldSelectorTargets() {
        final InstancioApi<MultipleClassesWithId<UUID>> api = Instancio.of(new TypeToken<MultipleClassesWithId<UUID>>() {})
                .assign(Assign.given(field(MultipleClassesWithId.ID.class, "value"))
                        .satisfies(any -> true)
                        .set(field(MultipleClassesWithId.class, "a"), null));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnresolvedAssignmentException.class)
                .hasMessageContaining("ambiguous assignment");
    }

    @Test
    void originMatchesMultipleClassSelectorTargets() {
        final InstancioApi<StringFields> api = Instancio.of(StringFields.class)
                .assign(Assign.given(allStrings())
                        .satisfies(any -> true)
                        .set(field(StringFields::getOne), null));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnresolvedAssignmentException.class)
                .hasMessageContaining("ambiguous assignment");
    }
}

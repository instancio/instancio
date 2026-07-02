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
package org.instancio.test.features.assign;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.TypeToken;
import org.instancio.exception.UnresolvedAssignmentException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.MultipleClassesWithId;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@FeatureTag({Feature.ASSIGN, Feature.ELEMENT_OF_SELECTOR, Feature.SELECTOR, Feature.SCOPE})
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
    void originMatchingSameFieldAcrossMultipleCollectionsIsNotAmbiguous() {
        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .assign(Assign.given(field(StringsAbc::getB))
                        .satisfies(any -> true)
                        .set(field(StringsAbc::getA), "foo"))
                .create();

        assertThatGraph(result)
                .hasValuesEqualToExactlyIn("foo", new String[]{
                        "abc.a",
                        "abcElements1[*].a",
                        "abcElements2[*].a",
                        "nested.abc.a",
                        "nested.abcElements1[*].a",
                        "nested.abcElements2[*].a"
                });
    }

    @Test
    void originMatchingSameFieldUnderSameParentTypeWithinCollectionIsNotAmbiguous() {
        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .assign(Assign.given(field(StringsAbc::getA))
                        .satisfies(any -> true)
                        .set(field(StringsAbc::getB), "foo"))
                .create();

        assertThatGraph(result)
                .hasValuesEqualToExactlyIn("foo", new String[]{
                        "abc.b",
                        "abcElements1[*].b",
                        "abcElements2[*].b",
                        "nested.abc.b",
                        "nested.abcElements1[*].b",
                        "nested.abcElements2[*].b"
                });
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

    @Test
    void originMatchingRootAndNestedNodeIsAmbiguous() {
        final InstancioApi<List<List<String>>> api = Instancio.of(new TypeToken<List<List<String>>>() {})
                .assign(Assign.given(all(List.class))
                        .satisfies(any -> true)
                        .set(allStrings(), "foo"));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnresolvedAssignmentException.class)
                .hasMessageContaining("ambiguous assignment");
    }

    @Test
    void originMatchingFieldsUnderDifferentParentTypesIsAmbiguous() {
        record StringAndPhoneList(String name, List<Phone> phones) {}

        final InstancioApi<StringAndPhoneList> api = Instancio.of(StringAndPhoneList.class)
                .assign(Assign.given(allStrings())
                        .satisfies(any -> true)
                        .set(field(StringAndPhoneList::name), null));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnresolvedAssignmentException.class)
                .hasMessageContaining("ambiguous assignment");
    }
}

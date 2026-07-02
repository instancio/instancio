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

import org.assertj.core.api.Assertions;
import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.collections.lists.ListString;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;

@FeatureTag(Feature.ELEMENT_OF_SELECTOR)
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorUnusedSelectorTest {

    @Test
    void usingSet_unusedElementOf() {
        final InstancioApi<Person> api = Instancio.of(Person.class)
                .set(elementOf(AbcListHolder::getAbcElements1) // invalid for Person class
                        .field(StringsAbc::getA), "foo");

        Assertions.assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnusedSelectorException.class)
                .hasMessageContaining("elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getA)");
    }

    @Test
    void usingSet_unusedIndexedElementSelector() {
        final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                .set(elementOf(AbcListHolder::getAbcElements1)
                                .field(Person::getName), // invalid for AbcListHolder::getAbcElements1 class
                        "foo");

        Assertions.assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnusedSelectorException.class)
                .hasMessageContaining("elementOf(AbcListHolder::getAbcElements1).field(Person::getName)");
    }

    @Test
    void usingAssign() {
        final InstancioApi<String> api = Instancio.of(String.class)
                .assign(Assign.valueOf(elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getA)).set("foo"));

        Assertions.assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnusedSelectorException.class)
                .hasMessageContaining("elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getA)");
    }


    @Test
    void unusedSelectorWhenFieldIsNotACollection() {
        final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                .set(elementOf(field(AbcListHolder::getAbc)).first(), new StringsAbc());

        assertThatThrownBy(api::create)
                .isInstanceOf(UnusedSelectorException.class)
                .hasMessageContaining("Unused selector");
    }

    @Test
    void usingFilter_unusedElementOf() {
        final InstancioApi<Person> api = Instancio.of(Person.class)
                .filter(elementOf(AbcListHolder::getAbcElements1) // invalid for Person class
                                .field(StringsAbc::getA),
                        (String s) -> true);

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnusedSelectorException.class)
                .hasMessageContaining("elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getA)");
    }

    @Test
    void usingWithNullable_unusedElementOf() {
        final InstancioApi<Person> api = Instancio.of(Person.class)
                .withNullable(elementOf(AbcListHolder::getAbcElements1) // invalid for Person class
                        .field(StringsAbc::getA));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnusedSelectorException.class)
                .hasMessageContaining("elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getA)");
    }

    @Test
    void usingAssign_scopedContainerNotMatchingAnyList_reportedAsUnused() {
        // The container selector's predicate matches any List, but its scope
        // (Person) never matches within AbcListHolder's node tree, so the
        // assignment can never fire and its selectors must be reported as
        // unused - even though Lists matching the bare predicate do exist.
        final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                .assign(Assign.valueOf(elementOf(all(List.class).within(scope(Person.class))).at(0))
                        .to(elementOf(all(List.class).within(scope(Person.class))).except(0)));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnusedSelectorException.class)
                .hasMessageContaining("elementOf(");
    }

    @Test
    void usingAssign_onlyOriginUnused_reportedAsUnused() {
        final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                .set(field(AbcListHolder::getAbcElements1), null)
                .size(field(AbcListHolder::getAbcElements2), 0)
                .assign(Assign.valueOf(elementOf(field(AbcListHolder::getAbcElements1)).at(0).field(StringsAbc::getA))
                        .to(elementOf(field(AbcListHolder::getAbcElements2)).except(0).field(StringsAbc::getA)));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnusedSelectorException.class)
                .hasMessageContaining("Unused selector in: assign() origin")
                .hasMessageContaining("elementOf(field(AbcListHolder::getAbcElements1)).at(0).field(StringsAbc::getA)")
                .hasMessageContaining("did not match any field within the element subtree")
                .hasMessageNotContaining("assign() destination");
    }

    @Test
    void usingAssign_lenientDestination_propagatesLeniencyToOrigin() {
        // Same never-firing assignment as above, but lenient() on the destination
        // must propagate to the origin, so neither selector is reported as unused.
        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .assign(Assign.valueOf(elementOf(all(List.class).within(scope(Person.class))).at(0))
                        .to(elementOf(all(List.class).within(scope(Person.class))).except(0).lenient()))
                .create();

        assertThat(result).isNotNull();
    }

    @Test
    void broadSelectorLosingToElementOfPriority_shouldNotBeReportedAsUnused() {
        final ListString result = Instancio.of(ListString.class)
                .size(field(ListString::getList), 3)
                .set(elementOf(ListString::getList), "elementof-value")
                .generate(all(String.class), gen -> gen.string().prefix("fallback_"))
                .create();

        assertThat(result.getList())
                .hasSize(3)
                .containsOnly("elementof-value");
    }
}

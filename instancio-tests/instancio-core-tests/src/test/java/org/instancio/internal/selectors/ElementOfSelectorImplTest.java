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
package org.instancio.internal.selectors;

import org.instancio.ElementOfSelector;
import org.instancio.Select;
import org.instancio.TargetSelector;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.Flattener;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.elementOf;

class ElementOfSelectorImplTest {

    @Test
    void verifyToString() {
        assertThat(Select.elementOf(Select.root()))
                .hasToString("elementOf(root())");

        assertThat(Select.elementOf(Select.field(Person::getAddress)))
                .hasToString("elementOf(field(Person::getAddress))");

        assertThat(Select.elementOf(Person::getAddress))
                .hasToString("elementOf(Person::getAddress)");

        assertThat(Select.elementOf(Person::getAddress).at(1))
                .hasToString("elementOf(Person::getAddress).at(1)");

        assertThat(Select.elementOf(Person::getAddress).field(Address::getCity))
                .hasToString("elementOf(Person::getAddress).field(Address::getCity)");

        assertThat(Select.elementOf(Person::getAddress).first().field(Address::getCity))
                .hasToString("elementOf(Person::getAddress).first().field(Address::getCity)");

        assertThat(Select.elementOf(Person::getAddress).last().field(Address::getCity))
                .hasToString("elementOf(Person::getAddress).last().field(Address::getCity)");

        assertThat(Select.elementOf(Person::getAddress).at(123).field(Address::getCity))
                .hasToString("elementOf(Person::getAddress).at(123).field(Address::getCity)");

        assertThat(Select.elementOf(Person::getAddress).at(1, 2, 3).field(Address::getCity))
                .hasToString("elementOf(Person::getAddress).at(1, 2, 3).field(Address::getCity)");

        assertThat(Select.elementOf(Person::getAddress).range(1, 8).field(Address::getCity))
                .hasToString("elementOf(Person::getAddress).range(1, 8).field(Address::getCity)");

        assertThat(Select.elementOf(Person::getAddress).except(1, 2, 3).field(Address::getCity))
                .hasToString("elementOf(Person::getAddress).except(1, 2, 3).field(Address::getCity)");

        assertThat(Select.elementOf(Select.types().of(List.class)).range(1, 8).field(Address::getCity))
                .hasToString("elementOf(types().of(List)).range(1, 8).field(Address::getCity)");

        assertThat(Select.elementOf(Select.field(Person::getAddress).lenient()))
                .hasToString("elementOf(field(Person::getAddress).lenient())");

        // lenient() applied to the elementOf() selector itself (not the inner selector)
        assertThat(Select.elementOf(Person::getAddress).lenient())
                .hasToString("elementOf(Person::getAddress).lenient()");

        assertThat(Select.elementOf(Person::getAddress).at(2).field(Address::getCity).lenient())
                .hasToString("elementOf(Person::getAddress).at(2).field(Address::getCity).lenient()");
    }

    @Test
    void targetRejectsNestedElementOf() {
        final ElementOfSelector collectionSelector = elementOf(AbcListHolder::getAbcElements1);
        final ElementOfSelector anotherCollectionSelector = elementOf(AbcListHolder::getAbcElements2);

        assertThatThrownBy(() -> collectionSelector.target(anotherCollectionSelector))
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("'.target(...)' does not support a nested elementOf() selector");
    }

    @Test
    void rangeRejectsStartGreaterThanEnd() {
        final ElementOfSelector selector = elementOf(AbcListHolder::getAbcElements1);

        assertThatThrownBy(() -> selector.range(5, 2))
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("start must not exceed end: 5, 2");
    }

    @Test
    void atRejectsEmptyIndices() {
        final ElementOfSelector selector = elementOf(AbcListHolder::getAbcElements1);

        assertThatThrownBy(() -> selector.at())
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("'.at(indices)' must not be empty");
    }

    @Test
    void exceptRejectsEmptyIndices() {
        final ElementOfSelector selector = elementOf(AbcListHolder::getAbcElements1);

        assertThatThrownBy(() -> selector.except())
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("'.except(indices)' must not be empty");
    }

    @Test
    void atRejectsNegativeIndex() {
        final ElementOfSelector selector = elementOf(AbcListHolder::getAbcElements1);

        assertThatThrownBy(() -> selector.at(-1))
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("index must not be negative: -1");
    }

    @Test
    void atRejectsNegativeIndexAmongMany() {
        final ElementOfSelector selector = elementOf(AbcListHolder::getAbcElements1);

        assertThatThrownBy(() -> selector.at(0, -2, 3))
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("index must not be negative: -2");
    }

    @Test
    void exceptRejectsNegativeIndex() {
        final ElementOfSelector selector = elementOf(AbcListHolder::getAbcElements1);

        assertThatThrownBy(() -> selector.except(-1, 0))
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("index must not be negative: -1");
    }

    @Test
    void rangeRejectsNegativeStart() {
        final ElementOfSelector selector = elementOf(AbcListHolder::getAbcElements1);

        assertThatThrownBy(() -> selector.range(-1, 3))
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("index must not be negative: -1");
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void fieldRejectsNullSelector() {
        final ElementOfSelector selector = elementOf(AbcListHolder::getAbcElements1);

        assertThatThrownBy(() -> selector.field(null))
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("'.field(selector)' selector must not be null");
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void targetRejectsNullSelector() {
        final ElementOfSelector selector = elementOf(AbcListHolder::getAbcElements1);

        assertThatThrownBy(() -> selector.target(null))
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("'.target(selector)' selector must not be null");
    }

    @Test
    void flatten() {
        final ElementOfSelector selector = elementOf(AbcListHolder::getAbcElements1);

        final List<TargetSelector> results = ((Flattener<TargetSelector>) selector).flatten();

        assertThat(results).containsExactly(selector);
    }

    @Test
    void lenientReturnsANewSelectorInstance() {
        final ElementOfSelectorImpl selector = (ElementOfSelectorImpl) elementOf(AbcListHolder::getAbcElements1);

        final ElementOfSelectorImpl lenientSelector = (ElementOfSelectorImpl) selector.lenient();

        assertThat(selector)
                .as("lenient() should return a new selector")
                .isNotSameAs(lenientSelector);

        assertThat(selector.isLenient())
                .as("the original selector should not be modified")
                .isFalse();

        assertThat(lenientSelector.isLenient()).isTrue();
    }

}

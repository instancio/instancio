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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Pet;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.scope;
import static org.instancio.Select.types;

/**
 * Precedence of selectors from lowest to highest,
 * with predicate selectors having the lowest priority:
 *
 * <ul>
 *   <li>{@code types()}</li>
 *   <li>{@code fields()}</li>
 *   <li>{@code all()}</li>
 *   <li>{@code field()}</li>
 * </ul>
 */
@FeatureTag({
        Feature.PREDICATE_SELECTOR,
        Feature.SELECTOR,
        Feature.SELECTOR_PRECEDENCE,
        Feature.SET,
        Feature.SUPPLY,
        Feature.GENERATE
})
@ExtendWith(InstancioExtension.class)
class SelectorPrecedenceTest {
    private static final String REGULAR_FIELD = "from regular field selector";
    private static final String REGULAR_TYPE = "from regular type selector";
    private static final String PREDICATE_FIELD = "from predicate field selector";
    private static final String PREDICATE_TYPE = "from predicate type selector";

    @Test
    @DisplayName("Verify each selector's precedence using set()")
    void verifySelectorPrecedenceUsingSet() {
        // Note: the order of set() methods in this particular test does not matter.
        // It would have mattered, if there was more than 1 selector per target,
        // in which case the first wins and the other(s) result in "unused selectors" error.
        final StringFields result = Instancio.of(StringFields.class)
                .set(field("four"), REGULAR_FIELD)
                .set(types().of(String.class), PREDICATE_TYPE)
                .set(fields().ofType(String.class).annotated(StringFields.Two.class), PREDICATE_FIELD)
                .set(allStrings().within(scope(StringFields.class, "three")), REGULAR_TYPE)
                .create();

        assertThat(result.getOne()).isEqualTo(PREDICATE_TYPE);
        assertThat(result.getTwo()).isEqualTo(PREDICATE_FIELD);
        assertThat(result.getThree()).isEqualTo(REGULAR_TYPE);
        assertThat(result.getFour()).isEqualTo(REGULAR_FIELD);
    }

    @Test
    @DisplayName("Verify each selector's precedence using generate()")
    void verifySelectorPrecedenceUsingGenerate() {
        final StringFields result = Instancio.of(StringFields.class)
                .generate(field("four"), gen -> gen.text().pattern(REGULAR_FIELD))
                .generate(types().of(String.class), gen -> gen.text().pattern(PREDICATE_TYPE))
                .generate(fields().ofType(String.class).annotated(StringFields.Two.class), gen -> gen.text().pattern(PREDICATE_FIELD))
                .generate(allStrings().within(scope(StringFields.class, "three")), gen -> gen.text().pattern(REGULAR_TYPE))
                .create();

        assertThat(result.getOne()).isEqualTo(PREDICATE_TYPE);
        assertThat(result.getTwo()).isEqualTo(PREDICATE_FIELD);
        assertThat(result.getThree()).isEqualTo(REGULAR_TYPE);
        assertThat(result.getFour()).isEqualTo(REGULAR_FIELD);
    }

    @Test
    @DisplayName("supply() using a field selector should take precedence over class selector, regardless of method order")
    void supplyUsingFieldSelectorShouldTakePrecedenceOverClassSelector() {
        final Person result = Instancio.of(Person.class)
                // field() first
                .supply(field("address"), () -> null)
                .supply(all(Address.class), Address::new)
                // all() first
                .set(allStrings(), "foo")
                .set(field("name"), "bar")
                .lenient()
                .create();

        assertThat(result.getAddress()).isNull();
        assertThat(result.getName()).isEqualTo("bar");
        assertThat(result.getPets()).extracting(Pet::getName).containsOnly("foo");
    }

    @Test
    @DisplayName("generate() (even with regular selector) has higher precedence than set() with predicate selector")
    void setWithPredicateHasHigherPrecedenceThanGenerate() {
        final StringFields result = Instancio.of(StringFields.class)
                .set(types().of(String.class), PREDICATE_TYPE)
                .generate(field("two"), gen -> gen.text().pattern(REGULAR_FIELD))
                .create();

        assertThat(result.getTwo()).isEqualTo(REGULAR_FIELD);

        assertThat(result.getOne()).isEqualTo(PREDICATE_TYPE);
        assertThat(result.getThree()).isEqualTo(PREDICATE_TYPE);
        assertThat(result.getFour()).isEqualTo(PREDICATE_TYPE);
    }

}

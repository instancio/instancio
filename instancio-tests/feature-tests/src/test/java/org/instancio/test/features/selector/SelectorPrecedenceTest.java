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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Pet;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.scope;
import static org.instancio.Select.setter;
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
    private static final String VIA_FIELD = "via field selector";
    private static final String VIA_SETTER = "via setter selector";
    private static final String VIA_TYPE = "via type selector";
    private static final String VIA_PREDICATE_FIELD = "via predicate field selector";
    private static final String VIA_PREDICATE_TYPE = "via predicate type selector";

    @Test
    @DisplayName("Verify each selector's precedence using set()")
    void verifySelectorPrecedenceUsingSet() {
        // Note: the order of set() methods in this particular test does not matter.
        // It would have mattered, if there was more than 1 selector per target,
        // in which case the last wins and the other(s) result in "unused selectors" error.
        final StringFields result = Instancio.of(StringFields.class)
                .set(field("four"), VIA_FIELD)
                .set(types().of(String.class), VIA_PREDICATE_TYPE)
                .set(fields().ofType(String.class).annotated(StringFields.Two.class), VIA_PREDICATE_FIELD)
                .set(allStrings().within(scope(StringFields.class, "three")), VIA_TYPE)
                .create();

        assertThat(result.getOne()).isEqualTo(VIA_PREDICATE_TYPE);
        assertThat(result.getTwo()).isEqualTo(VIA_PREDICATE_FIELD);
        assertThat(result.getThree()).isEqualTo(VIA_TYPE);
        assertThat(result.getFour()).isEqualTo(VIA_FIELD);
    }

    @Test
    @DisplayName("Verify each selector's precedence using generate()")
    void verifySelectorPrecedenceUsingGenerate() {
        final StringFields result = Instancio.of(StringFields.class)
                .generate(field("four"), gen -> gen.text().pattern(VIA_FIELD))
                .generate(types().of(String.class), gen -> gen.text().pattern(VIA_PREDICATE_TYPE))
                .generate(fields().ofType(String.class).annotated(StringFields.Two.class), gen -> gen.text().pattern(VIA_PREDICATE_FIELD))
                .generate(allStrings().within(scope(StringFields.class, "three")), gen -> gen.text().pattern(VIA_TYPE))
                .create();

        assertThat(result.getOne()).isEqualTo(VIA_PREDICATE_TYPE);
        assertThat(result.getTwo()).isEqualTo(VIA_PREDICATE_FIELD);
        assertThat(result.getThree()).isEqualTo(VIA_TYPE);
        assertThat(result.getFour()).isEqualTo(VIA_FIELD);
    }

    @Test
    @DisplayName("supply() using a field selector should take precedence over class selector, regardless of method order")
    void supplyUsingFieldSelectorShouldTakePrecedenceOverClassSelector() {
        final Person result = Instancio.of(Person.class)
                // Target address using field first, then class
                .supply(field("address"), () -> null)
                .supply(all(Address.class), Address::new)
                // Target strings using class first, then field
                .set(allStrings(), "foo")
                .set(field("name"), "bar")
                .lenient()
                .create();

        assertThat(result.getAddress()).isNull();
        assertThat(result.getName()).isEqualTo("bar");
        assertThat(result.getPets())
                .as("Field selector should win")
                .extracting(Pet::getName)
                .containsOnly("foo");
    }

    @Test
    @DisplayName("generate() (even with regular selector) has higher precedence than set() with predicate selector")
    void setWithPredicateHasHigherPrecedenceThanGenerate() {
        final StringFields result = Instancio.of(StringFields.class)
                .set(types().of(String.class), VIA_PREDICATE_TYPE)
                .generate(field("two"), gen -> gen.text().pattern(VIA_FIELD))
                .create();

        assertThat(result.getTwo()).isEqualTo(VIA_FIELD);
        assertThat(result.getOne()).isEqualTo(VIA_PREDICATE_TYPE);
        assertThat(result.getThree()).isEqualTo(VIA_PREDICATE_TYPE);
        assertThat(result.getFour()).isEqualTo(VIA_PREDICATE_TYPE);
    }

    @Test
    void whenSameMethodWithSameSelectorSpecifiedTwiceLastOneShouldWin() {
        final StringHolder result = Instancio.of(StringHolder.class)
                .generate(allStrings(), gen -> gen.string().prefix("foo"))
                .generate(allStrings(), gen -> gen.string().prefix("bar"))
                .create();

        assertThat(result.getValue()).startsWith("bar");
    }

    /**
     * The order in which selectors are specified should not matter.
     */
    @Nested
    @RunWith.MethodAssignmentOnly
    class FieldSelectorShouldTakePrecedenceOverSetterSelectorTest {

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.FAIL);

        @Test
        void fieldFirst() {
            final StringFields result = Instancio.of(StringFields.class)
                    .set(field(StringFields::getOne), VIA_FIELD)
                    .set(setter(StringFields::setOne), VIA_SETTER)
                    .lenient()
                    .create();

            assertThat(result.getOne()).isEqualTo(VIA_FIELD);
        }

        @Test
        void methodFirst() {
            final StringFields result = Instancio.of(StringFields.class)
                    .set(setter(StringFields::setOne), VIA_SETTER)
                    .set(field(StringFields::getOne), VIA_FIELD)
                    .lenient()
                    .create();

            assertThat(result.getOne()).isEqualTo(VIA_FIELD);
        }

        @Test
        void unusedSelectorIsThrownInStrictMode() {
            final InstancioApi<StringFields> api = Instancio.of(StringFields.class)
                    .set(setter(StringFields::setOne), VIA_SETTER)
                    .set(field(StringFields::getOne), VIA_FIELD);

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(UnusedSelectorException.class)
                    .hasMessageContaining("setter(StringFields, \"setOne(String)\")");
        }
    }

    @Nested
    @RunWith.MethodAssignmentOnly
    class SetterSelectorShouldTakePrecedenceOverTypeSelectorTest {

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.FAIL);

        @Test
        void setterSelectorFirst() {
            final StringFields result = Instancio.of(StringFields.class)
                    .set(setter(StringFields::setOne), VIA_SETTER)
                    .set(all(String.class), VIA_TYPE)
                    .create();

            assertThat(result.getOne()).isEqualTo(VIA_SETTER);
        }

        @Test
        void typeSelectorFirst() {
            final StringFields result = Instancio.of(StringFields.class)
                    .set(all(String.class), VIA_TYPE)
                    .set(setter(StringFields::setOne), VIA_SETTER)
                    .create();

            assertThat(result.getOne()).isEqualTo(VIA_SETTER);
        }
    }

    @Nested
    @RunWith.MethodAssignmentOnly
    class SetterSelectorShouldTakePrecedenceOverPredicateSelectorTest {

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.FAIL);

        @Test
        void setterSelectorShouldTakePrecedenceOverPredicateFieldSelector() {
            final StringFields result = Instancio.of(StringFields.class)
                    .set(fields().ofType(String.class), VIA_PREDICATE_FIELD)
                    .set(setter(StringFields::setOne), VIA_SETTER)
                    .create();

            assertThat(result.getOne()).isEqualTo(VIA_SETTER);
        }

        @Test
        void setterSelectorShouldTakePrecedenceOverPredicateTypeSelector() {
            final StringFields result = Instancio.of(StringFields.class)
                    .set(types().of(String.class), VIA_PREDICATE_TYPE)
                    .set(setter(StringFields::setOne), VIA_SETTER)
                    .create();

            assertThat(result.getOne()).isEqualTo(VIA_SETTER);
        }
    }
}

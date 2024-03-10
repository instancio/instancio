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
package org.instancio.test.features.setmodel;

import lombok.Data;
import org.instancio.Assignment;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Scope;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.given;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;

@FeatureTag({Feature.MODEL, Feature.SET_MODEL, Feature.ASSIGN})
@ExtendWith(InstancioExtension.class)
class SetModelAssignTest {

    private static @Data class Inner {
        String a;
        String b;
    }

    private static @Data class Outer {
        Inner inner1;
        Inner inner2;
    }
    //@formatter:on

    @Nested
    class AssignWithinModelTest {
        @Test
        void assign() {
            final String expectedValue = "-value-";
            final Model<Inner> innerModel = Instancio.of(Inner.class)
                    .set(field(Inner::getA), expectedValue)
                    .assign(valueOf(Inner::getA).to(Inner::getB))
                    .toModel();

            final Outer result = Instancio.of(Outer.class)
                    .setModel(field(Outer::getInner2), innerModel)
                    .create();

            assertThat(result.getInner1().getA()).isNotEqualTo(result.getInner1().getB());
            assertThat(result.getInner2().getA()).isEqualTo(result.getInner2().getB());
        }

        @RepeatedTest(10)
        void assignmentWithinCollectionElement() {
            // Phone instances will be used as collection elements
            final Model<Phone> phoneModel = Instancio.of(Phone.class)
                    .generate(field("countryCode"), gen -> gen.oneOf("+1", "+2"))
                    // set all numbers to 111 by default
                    // and overwrite those with countryCode=+2 to 222
                    .set(field("number"), "111")
                    .assign(given(field("countryCode"))
                            .is("+2")
                            .set(field("number"), "222"))
                    .toModel();

            final Person result = Instancio.of(Person.class)
                    .withSetting(Keys.COLLECTION_MIN_SIZE, 50)
                    .setModel(all(Phone.class).within(scope(Address::getPhoneNumbers)), phoneModel)
                    .create();

            final List<Phone> phones = result.getAddress().getPhoneNumbers();

            assertThat(phones)
                    .extracting(Phone::getCountryCode)
                    .containsOnly("+1", "+2");

            assertThat(phones)
                    .filteredOn(p -> p.getCountryCode().equals("+1"))
                    .extracting(Phone::getNumber)
                    .containsOnly("111");

            assertThat(phones)
                    .filteredOn(p -> p.getCountryCode().equals("+2"))
                    .extracting(Phone::getNumber)
                    .containsOnly("222");
        }
    }

    @Nested
    class AssignAcrossModelTest {

        /**
         * Direction of assignment: values from Model to object being created.
         */
        @Test
        void assignFromModelToObject() {
            final Scope inner1Scope = scope(Outer::getInner1);
            final Scope inner2Scope = scope(Outer::getInner2);

            final Model<Inner> innerModel = Instancio.of(Inner.class)
                    .set(field(Inner::getA), "a")
                    // assignment within inner1
                    .assign(given(field(Inner::getA)).is("a").set(field(Inner::getB), "b"))
                    .toModel();

            // assign inner1 field values to inner2
            final Assignment[] assignments = {
                    valueOf(field(Inner::getA).within(inner1Scope)).to(field(Inner::getA).within(inner2Scope)),
                    valueOf(field(Inner::getB).within(inner1Scope)).to(field(Inner::getB).within(inner2Scope))
            };

            final Outer result = Instancio.of(Outer.class)
                    .setModel(field(Outer::getInner1), innerModel)
                    .assign(assignments)
                    .create();

            assertThat(result.inner1.a).isEqualTo(result.inner2.a).isEqualTo("a");
            assertThat(result.inner1.b).isEqualTo(result.inner2.b).isEqualTo("b");
        }

        /**
         * Direction of assignment: values object being created to Model.
         */
        @Test
        void assignFromObjectToModel() {
            final Model<Inner> innerModel = Instancio.of(Inner.class).toModel();

            final Scope inner1Scope = scope(Outer::getInner1);
            final Scope inner2Scope = scope(Outer::getInner2);

            // assignment targets are within the inner1 model
            final Outer result = Instancio.of(Outer.class)
                    .setModel(field(Outer::getInner1), innerModel)
                    .assign(valueOf(field(Inner::getA).within(inner2Scope)).to(field(Inner::getA).within(inner1Scope)))
                    .assign(valueOf(field(Inner::getB).within(inner2Scope)).to(field(Inner::getB).within(inner1Scope)))
                    .create();

            assertThat(result.inner1.a).isEqualTo(result.inner2.a);
            assertThat(result.inner1.b).isEqualTo(result.inner2.b);
        }
    }
}

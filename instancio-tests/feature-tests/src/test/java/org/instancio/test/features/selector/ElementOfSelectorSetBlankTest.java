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

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.feed.Feed;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.collections.lists.ListPerson;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.record.AddressRecord;
import org.instancio.test.support.pojo.record.PersonRecord;
import org.instancio.test.support.pojo.record.StringsAbcRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.root;
import static org.instancio.Select.scope;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.ELEMENT_OF_SELECTOR, Feature.BLANK})
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorSetBlankTest {

    private static final int SIZE = 5;

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.COLLECTION_MIN_SIZE, SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, SIZE);

    private static void assertStringsAbcBlank(final StringsAbc element) {
        assertThat(element).isNotNull();
        assertThat(element.a).isNull();
        assertThat(element.b).isNull();
        assertThat(element.c).isNull();
        assertThat(element.def).isNotNull();
        assertThatObject(element.def).hasAllFieldsOfTypeSetToNull(String.class);
    }

    private static void assertStringsAbcFullyPopulated(final StringsAbc element) {
        assertThat(element).isNotNull();
        assertThat(element.a).isNotBlank();
        assertThatObject(element).isFullyPopulated();
    }

    @Nested
    class WholeElement {

        @Test
        void first() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(AbcListHolder::getAbcElements1).first())
                    .create();

            assertStringsAbcBlank(result.getAbcElements1().get(0));
            assertStringsAbcFullyPopulated(result.getAbcElements1().get(1));
        }

        @Test
        void last() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(AbcListHolder::getAbcElements1).last())
                    .create();

            assertStringsAbcBlank(result.getAbcElements1().get(SIZE - 1));
            assertStringsAbcFullyPopulated(result.getAbcElements1().get(0));
        }

        @Test
        void atIndex() {
            final int index = 2;
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(AbcListHolder::getAbcElements1).at(index))
                    .create();

            assertStringsAbcBlank(result.getAbcElements1().get(index));
            assertStringsAbcFullyPopulated(result.getAbcElements1().get(index - 1));
            assertStringsAbcFullyPopulated(result.getAbcElements1().get(index + 1));
        }

        @Test
        void atIndices() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(AbcListHolder::getAbcElements1).at(0, 2, 4))
                    .create();

            assertStringsAbcBlank(result.getAbcElements1().get(0));
            assertStringsAbcBlank(result.getAbcElements1().get(2));
            assertStringsAbcBlank(result.getAbcElements1().get(4));
            assertStringsAbcFullyPopulated(result.getAbcElements1().get(1));
            assertStringsAbcFullyPopulated(result.getAbcElements1().get(3));
        }

        @Test
        void range() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(AbcListHolder::getAbcElements1).range(1, 3))
                    .create();

            assertStringsAbcBlank(result.getAbcElements1().get(1));
            assertStringsAbcBlank(result.getAbcElements1().get(2));
            assertStringsAbcBlank(result.getAbcElements1().get(3));
            assertStringsAbcFullyPopulated(result.getAbcElements1().get(0));
            assertStringsAbcFullyPopulated(result.getAbcElements1().get(4));
        }

        @Test
        void except() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(AbcListHolder::getAbcElements1).except(0, 4))
                    .create();

            assertStringsAbcBlank(result.getAbcElements1().get(1));
            assertStringsAbcBlank(result.getAbcElements1().get(2));
            assertStringsAbcBlank(result.getAbcElements1().get(3));
            assertStringsAbcFullyPopulated(result.getAbcElements1().get(0));
            assertStringsAbcFullyPopulated(result.getAbcElements1().get(4));
        }

        @Test
        void allElements() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(AbcListHolder::getAbcElements1))
                    .create();

            assertThat(result.getAbcElements1())
                    .hasSize(SIZE)
                    .allSatisfy(ElementOfSelectorSetBlankTest::assertStringsAbcBlank);
        }
    }

    @Nested
    class ElementField {

        @Test
        void fieldOfAllElements() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getA))
                    .create();

            assertThatGraph(result)
                    .hasValuesOfTypeEqualToExactlyIn(String.class, null, "abcElements1[*].a");
        }

        @Test
        void fieldOfFirstElement() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getA))
                    .create();

            assertThatGraph(result)
                    .hasValuesOfTypeEqualToExactlyIn(String.class, null, "abcElements1[0].a");
        }

        @Test
        void fieldOfElementsInRange() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(AbcListHolder::getAbcElements1).range(1, 3).field(StringsAbc::getA))
                    .create();

            assertThatGraph(result)
                    .hasValuesOfTypeEqualToExactlyIn(String.class, null, "abcElements1[1-3].a");
        }
    }

    @Nested
    class ElementTarget {

        @Test
        void targetWithinFirstElement() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(AbcListHolder::getAbcElements1).first().target(all(StringsDef.class)))
                    .create();

            final StringsAbc element0 = result.getAbcElements1().get(0);
            assertThat(element0.a).isNotBlank();
            assertThat(element0.def).isNotNull();
            assertThatObject(element0.def).hasAllFieldsOfTypeSetToNull(String.class);
        }

        @Test
        void deepTargetWithinAllElements() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(AbcListHolder::getAbcElements1).target(field(StringsDef::getD)))
                    .create();

            assertThatGraph(result)
                    .hasValuesOfTypeEqualToExactlyIn(String.class, null, "abcElements1[*].def.d");
        }
    }

    @Nested
    class RootList {

        @Test
        void wholeElementFirst() {
            final List<StringsAbc> result = Instancio.ofList(StringsAbc.class)
                    .setBlank(elementOf(root()).first())
                    .create();

            assertThat(result).hasSize(SIZE);
            assertStringsAbcBlank(result.get(0));
            assertStringsAbcFullyPopulated(result.get(1));
        }

        @Test
        void wholeElementAll() {
            final List<StringsAbc> result = Instancio.ofList(StringsAbc.class)
                    .setBlank(elementOf(root()))
                    .create();

            assertThat(result).hasSize(SIZE)
                    .allSatisfy(ElementOfSelectorSetBlankTest::assertStringsAbcBlank);
        }

        @Test
        void fieldOfFirstElement() {
            final List<StringsAbc> result = Instancio.ofList(StringsAbc.class)
                    .setBlank(elementOf(root()).first().field(StringsAbc::getA))
                    .create();

            assertThat(result.get(0).a).isNull();
        }
    }

    @Nested
    class ContainerSelector {

        @Test
        void allListFirstElement() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(all(List.class)).first())
                    .create();

            assertStringsAbcBlank(result.getAbcElements1().get(0));
            assertStringsAbcBlank(result.getAbcElements2().get(0));
            assertStringsAbcFullyPopulated(result.getAbcElements1().get(1));
            assertStringsAbcFullyPopulated(result.getAbcElements2().get(1));
        }
    }

    @Nested
    class ContainerVariations {

        @Nested
        class RecordElements {

            @Test
            void wholeElementFirst() {
                final List<StringsAbcRecord> result = Instancio.ofList(StringsAbcRecord.class)
                        .setBlank(elementOf(root()).first())
                        .create();

                assertThat(result.get(0)).isNotNull().satisfies(blank -> {
                    assertThat(blank.a()).isNull();
                    assertThat(blank.def()).isNotNull();
                    assertThatGraph(blank).hasAllValuesOfTypeEqualTo(String.class, null);
                });
                assertThatObject(result.get(1)).isFullyPopulated();
            }

            @Test
            void allElements() {
                final List<PersonRecord> result = Instancio.ofList(PersonRecord.class)
                        .setBlank(elementOf(root()))
                        .create();

                assertThat(result).hasSize(SIZE)
                        .allSatisfy(person -> {
                            assertThat(person.name()).isNull();
                            assertThat(person.age()).isZero();
                            assertThat(person.address()).isNotNull().satisfies(address ->
                                    assertThat(address.phoneNumbers()).isEmpty());
                        });
            }

            @Test
            void componentField() {
                final List<PersonRecord> result = Instancio.ofList(PersonRecord.class)
                        .setBlank(elementOf(root()).first().field(PersonRecord::address))
                        .create();

                final PersonRecord person = result.get(0);
                assertThat(person.address()).isNotNull().satisfies(address -> {
                    assertThat(address.street()).isNull();
                    assertThat(address.phoneNumbers()).isEmpty();
                });
                assertThat(person.name()).isNotBlank();
                assertThatObject(result.get(1)).isFullyPopulated();
            }

            @Test
            void nestedRecordWithinField() {
                final AddressRecord result = Instancio.of(AddressRecord.class)
                        .setBlank(elementOf(AddressRecord::phoneNumbers).first())
                        .create();

                assertThat(result.phoneNumbers()).isNotEmpty();
                assertThat(result.phoneNumbers().get(0)).satisfies(phone -> {
                    assertThat(phone.countryCode()).isNull();
                    assertThat(phone.number()).isNull();
                });
            }
        }

        @Nested
        class PojoElements {

            @Test
            void wholeElementFirst() {
                final ListPerson result = Instancio.of(ListPerson.class)
                        .setBlank(elementOf(ListPerson::getList).first())
                        .create();

                final Person blank = result.getList().get(0);
                assertThat(blank.getName()).isNull();
                assertThat(blank.getAge()).isZero();
                assertThat(blank.getPets()).isEmpty();
                assertThat(blank.getAddress()).isNotNull().satisfies(address ->
                        assertThat(address.getPhoneNumbers()).isEmpty());
                assertThatObject(result.getList().get(1)).isFullyPopulated();
            }

            @Test
            void subField() {
                final ListPerson result = Instancio.of(ListPerson.class)
                        .setBlank(elementOf(ListPerson::getList).first().field(Person::getAddress))
                        .create();

                final Person person = result.getList().get(0);
                assertThat(person.getAddress()).isNotNull().satisfies(address -> {
                    assertThat(address.getStreet()).isNull();
                    assertThat(address.getPhoneNumbers()).isEmpty();
                });
                assertThat(person.getName()).isNotBlank();
                assertThatObject(result.getList().get(1)).isFullyPopulated();
            }
        }

        @Nested
        class MultiContainer {

            @Test
            void listOfMapBlankWholeElement() {
                record Holder(List<Map<String, String>> listOfMaps) {}

                final Holder result = Instancio.of(Holder.class)
                        .setBlank(elementOf(Holder::listOfMaps).at(0))
                        .create();

                assertThat(result.listOfMaps()).hasSize(SIZE);
                assertThat(result.listOfMaps().get(0)).isEmpty();
                assertThat(result.listOfMaps().get(1)).isNotEmpty();
            }

            @Test
            void listOfListBlankInnerList() {
                record Holder(List<List<StringsAbc>> matrix) {}

                final Holder result = Instancio.of(Holder.class)
                        .setBlank(elementOf(Holder::matrix).at(0))
                        .create();

                assertThat(result.matrix()).hasSize(SIZE);
                assertThat(result.matrix().get(0)).isEmpty();
                assertThat(result.matrix().get(1)).isNotEmpty();
            }
        }

        @Nested
        class SetElements {

            @WithSettings
            private static final Settings setSettings = Settings.create()
                    .set(Keys.COLLECTION_MIN_SIZE, 2)
                    .set(Keys.COLLECTION_MAX_SIZE, 2);

            @Test
            void wholeElementFirst() {
                final Set<StringsAbc> result = Instancio.ofSet(StringsAbc.class)
                        .setBlank(elementOf(root()).first())
                        .create();

                assertThat(result)
                        .hasSize(2)
                        .anySatisfy(ElementOfSelectorSetBlankTest::assertStringsAbcBlank);
            }

            @Test
            void subFieldAllElements() {
                final Set<StringsAbc> result = Instancio.ofSet(StringsAbc.class)
                        .setBlank(elementOf(root()).field(StringsAbc::getA))
                        .create();

                assertThat(result).isNotEmpty()
                        .allSatisfy(e -> assertThat(e.a).isNull());
            }
        }
    }

    @Nested
    class FeedInteraction {

        private static final int FEED_SIZE = 3;

        @WithSettings
        private static final Settings feedSettings = Settings.create()
                .set(Keys.COLLECTION_MIN_SIZE, FEED_SIZE)
                .set(Keys.COLLECTION_MAX_SIZE, FEED_SIZE);

        @Feed.Source(string = """
                a,b,c
                a1,b1,c1
                a2,b2,c2
                a3,b3,c3""")
        private interface AbcFeed extends Feed {}

        private static Feed abcFeed() {
            return Instancio.createFeed(AbcFeed.class);
        }

        @Test
        void feedPopulatesElements_blankIsNoOp() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1), abcFeed())
                    .setBlank(elementOf(AbcListHolder::getAbcElements1).first())
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertThat(elements.get(0).a).isEqualTo("a1");
            assertThat(elements.get(1).a).isEqualTo("a2");
            assertThat(elements.get(2).a).isEqualTo("a3");
        }

        @Test
        void blankAtDifferentIndexWhileFeed() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).at(1), abcFeed())
                    .setBlank(elementOf(AbcListHolder::getAbcElements1).at(0))
                    .create();

            assertThat(result.getAbcElements1().get(0).a).isNull();
            assertThat(result.getAbcElements1().get(1).a).isEqualTo("a1");
        }

        @Test
        void blankInnerFieldOfFedElements() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1), abcFeed())
                    .setBlank(elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getDef))
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            for (int i = 0; i < FEED_SIZE; i++) {
                assertThat(elements.get(i).a).isEqualTo("a" + (i + 1));
                assertThat(elements.get(i).def.d).isNull();
            }
        }
    }

    @Nested
    class ModelInteraction {

        @Test
        void setBlankInModel() {
            final Model<AbcListHolder> model = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(AbcListHolder::getAbcElements1).first())
                    .toModel();

            final AbcListHolder result = Instancio.create(model);

            assertStringsAbcBlank(result.getAbcElements1().get(0));
            assertStringsAbcFullyPopulated(result.getAbcElements1().get(1));
        }

        @Test
        void modelReusedViaSetModel() {
            final Model<AbcListHolder> model = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(AbcListHolder::getAbcElements1).first())
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(root(), model)
                    .create();

            assertStringsAbcBlank(result.getAbcElements1().get(0));
        }
    }

    @Nested
    class ScopeInteraction {

        @Test
        void allListWithinNestedScope() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(all(List.class)
                            .within(scope(AbcListHolder.Nested.class)))
                            .first())
                    .create();

            assertStringsAbcBlank(result.getNested().getAbcElements1().get(0));
            assertStringsAbcBlank(result.getNested().getAbcElements2().get(0));
            assertStringsAbcFullyPopulated(result.getAbcElements1().get(0));
        }

        @Test
        void withinScopeFieldsChain() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(all(List.class)
                            .within(fields().named("abcElements1")
                                    .declaredIn(AbcListHolder.Nested.class)
                                    .toScope()))
                            .first())
                    .create();

            assertStringsAbcBlank(result.getNested().getAbcElements1().get(0));
        }

        @Test
        void containerScopedViaFieldToScope() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(all(List.class)
                            .within(field(AbcListHolder::getAbcElements1).toScope()))
                            .first())
                    .create();

            assertStringsAbcBlank(result.getAbcElements1().get(0));
            assertStringsAbcFullyPopulated(result.getAbcElements2().get(0));
        }
    }

    @Nested
    class EdgeCases {

        @Test
        void setBlankElementOfNeverReportedAsUnused() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .withSetting(Keys.FAIL_ON_ERROR, true)
                    .setBlank(elementOf(AbcListHolder::getAbcElements1).first())
                    .create();

            assertThat(result.getAbcElements1()).hasSize(SIZE);
        }

        @Test
        void indexBeyondSizeIsSilentNoOp() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .withSetting(Keys.FAIL_ON_ERROR, true)
                    .setBlank(elementOf(AbcListHolder::getAbcElements1)
                            .at(SIZE + 10)
                            .field(StringsAbc::getA))
                    .create();

            assertThat(result.getAbcElements1()).hasSize(SIZE)
                    .allSatisfy(e -> assertThat(e.a).isNotNull());
        }

        @Test
        void multipleElementOfBlanksAtDifferentDepths() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(AbcListHolder::getAbcElements1).at(0))
                    .setBlank(elementOf(AbcListHolder::getAbcElements2).at(2).field(StringsAbc::getDef))
                    .create();

            assertStringsAbcBlank(result.getAbcElements1().get(0));
            assertThatObject(result.getAbcElements2().get(2).def)
                    .hasAllFieldsOfTypeSetToNull(String.class);
            assertStringsAbcFullyPopulated(result.getAbcElements1().get(1));
            assertStringsAbcFullyPopulated(result.getAbcElements2().get(1));
        }

        @Test
        void elementOfBlankAndFieldBlankCoexist() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setBlank(elementOf(AbcListHolder::getAbcElements2).at(0))
                    .setBlank(field(AbcListHolder::getAbcElements1))
                    .create();

            assertThat(result.getAbcElements1()).isEmpty();
            assertStringsAbcBlank(result.getAbcElements2().get(0));
            assertStringsAbcFullyPopulated(result.getAbcElements2().get(1));
        }

        @Test
        void setOverridesBlank() {
            final List<StringsAbc> result = Instancio.ofList(StringsAbc.class)
                    .setBlank(elementOf(root()).first())
                    .set(elementOf(root()).first().field(StringsAbc::getA), "OVERRIDE")
                    .create();

            assertThat(result.get(0).a).isEqualTo("OVERRIDE");
            assertThat(result.get(0).b).isNull();
        }
    }
}

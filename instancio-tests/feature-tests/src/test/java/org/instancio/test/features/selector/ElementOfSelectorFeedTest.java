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

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Model;
import org.instancio.TypeToken;
import org.instancio.exception.InstancioApiException;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.feed.Feed;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.Api;
import org.instancio.test.support.conditions.Conditions;
import org.instancio.test.support.pojo.misc.AbcArrayHolder;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.record.StringsAbcRecord;
import org.instancio.test.support.pojo.record.StringsDefRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.Select.root;
import static org.instancio.Select.scope;

@FeatureTag({Feature.ELEMENT_OF_SELECTOR, Feature.FEED, Feature.APPLY_FEED, Feature.SELECTOR})
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorFeedTest {

    private static final int SIZE = 3;

    @WithSettings
    private static final Settings SETTINGS = Settings.create()
            .set(Keys.ARRAY_MIN_SIZE, SIZE)
            .set(Keys.ARRAY_MAX_SIZE, SIZE)
            .set(Keys.COLLECTION_MIN_SIZE, SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, SIZE);

    @Feed.Source(string = """
            a,b,c
            a1,b1,c1
            a2,b2,c2
            a3,b3,c3
            a4,b4,c4
            a5,b5,c5
            a6,b6,c6
            a7,b7,c7
            a8,b8,c8
            a9,b9,c9""")
    private interface AbcFeed extends Feed {}

    @Feed.Source(string = """
            d,e,f
            d1,e1,f1
            d2,e2,f2
            d3,e3,f3
            d4,e4,f4
            d5,e5,f5
            d6,e6,f6
            d7,e7,f7
            d8,e8,f8
            d9,e9,f9""")
    private interface DefFeed extends Feed {}

    @Feed.Source(string = """
            g,h,i
            g1,h1,i1
            g2,h2,i2
            g3,h3,i3
            g4,h4,i4
            g5,h5,i5
            g6,h6,i6
            g7,h7,i7
            g8,h8,i8
            g9,h9,i9""")
    private interface GhiFeed extends Feed {}

    private static Feed abcFeed() {
        return Instancio.createFeed(AbcFeed.class);
    }

    private static Feed defFeed() {
        return Instancio.createFeed(DefFeed.class);
    }

    private static Feed ghiFeed() {
        return Instancio.createFeed(GhiFeed.class);
    }

    private static void assertFedFromRow(final StringsAbc element, final int row) {
        assertThat(element.getA()).isEqualTo("a" + row);
        assertThat(element.getB()).isEqualTo("b" + row);
        assertThat(element.getC()).isEqualTo("c" + row);
    }

    private static void assertFedFromRow(final StringsDef def, final int row) {
        assertThat(def.getD()).isEqualTo("d" + row);
        assertThat(def.getE()).isEqualTo("e" + row);
        assertThat(def.getF()).isEqualTo("f" + row);
    }

    private static void assertNotFed(final StringsAbc element) {
        assertThat(element.getA()).is(Conditions.RANDOM_STRING);
        assertThat(element.getB()).is(Conditions.RANDOM_STRING);
        assertThat(element.getC()).is(Conditions.RANDOM_STRING);
    }

    private static void assertNotFed(final StringsDef def) {
        assertThat(def.getD()).is(Conditions.RANDOM_STRING);
        assertThat(def.getE()).is(Conditions.RANDOM_STRING);
        assertThat(def.getF()).is(Conditions.RANDOM_STRING);
    }

    private static void assertGhiFedFromRow(final StringsGhi ghi, final int row) {
        assertThat(ghi.getG()).isEqualTo("g" + row);
        assertThat(ghi.getH()).isEqualTo("h" + row);
        assertThat(ghi.getI()).isEqualTo("i" + row);
    }

    private static void assertNotFed(final StringsGhi ghi) {
        assertThat(ghi.getG()).is(Conditions.RANDOM_STRING);
        assertThat(ghi.getH()).is(Conditions.RANDOM_STRING);
        assertThat(ghi.getI()).is(Conditions.RANDOM_STRING);
    }

    private static void assertFedFromRow(final StringsDefRecord def, final int row) {
        assertThat(def.d()).isEqualTo("d" + row);
        assertThat(def.e()).isEqualTo("e" + row);
        assertThat(def.f()).isEqualTo("f" + row);
    }

    @Nested
    class WholeElements {

        @Test
        void eachElementOfTargetedListIsFedSequentialRecords() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1), abcFeed())
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertThat(elements).hasSize(SIZE);
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(elements.get(i), i + 1);
            }
        }

        @Test
        void containersOfTheSameElementTypeAreNotAffected() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1), abcFeed())
                    .create();

            // abcElements2 has the same element type as the
            // targeted abcElements1, but must remain unaffected
            assertThat(result.getAbcElements2())
                    .hasSize(SIZE)
                    .allSatisfy(ElementOfSelectorFeedTest::assertNotFed);

            // ... as must the standalone field of the element type
            assertNotFed(result.getAbc());
        }

        @Test
        void eachElementOfTargetedArrayIsFedSequentialRecords() {
            final AbcArrayHolder result = Instancio.of(AbcArrayHolder.class)
                    .applyFeed(elementOf(AbcArrayHolder::getAbcElements1), abcFeed())
                    .create();

            final StringsAbc[] elements = result.getAbcElements1();
            assertThat(elements).hasSize(SIZE);
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(elements[i], i + 1);
            }
            assertThat(result.getAbcElements2())
                    .hasSize(SIZE)
                    .allSatisfy(ElementOfSelectorFeedTest::assertNotFed);
        }

        @Test
        void rootContainerViaElementOfRootSelector() {
            final List<StringsAbc> results = Instancio.of(new TypeToken<List<StringsAbc>>() {})
                    .applyFeed(elementOf(root()), abcFeed())
                    .create();

            assertThat(results).hasSize(SIZE);
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(results.get(i), i + 1);
            }
        }

        @Test
        void feedOnEmptyContainerIsANoOp() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .generate(field(AbcListHolder.class, "abcElements1"), gen -> gen.collection().size(0))
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1), abcFeed())
                    .create();

            // the element node exists in the node tree, so the feed selector is
            // considered used (no unused-selector error) even with no elements
            assertThat(result.getAbcElements1()).isEmpty();
        }

        @Test
        void setElementsAreFedDistinctRecords() {
            final Set<StringsAbc> results = Instancio.of(new TypeToken<Set<StringsAbc>>() {})
                    .applyFeed(elementOf(root()), abcFeed())
                    .create();

            // Sets have no element order; verify each element holds
            // a complete feed record, and that all records are distinct
            assertThat(results).hasSize(SIZE).allSatisfy(element -> {
                assertThat(element.getA()).matches("a[1-3]");
                final int row = Integer.parseInt(element.getA().substring(1));
                assertFedFromRow(element, row);
            });
            assertThat(results).extracting(StringsAbc::getA).doesNotHaveDuplicates();
        }
    }

    @Nested
    class IndexedElements {

        @Test
        void atSingleIndex() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).at(1), abcFeed())
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertNotFed(elements.get(0));
            assertFedFromRow(elements.get(1), 1);
            assertNotFed(elements.get(2));
        }

        @Test
        void atMultipleIndices_skippedElementsDoNotConsumeRecords() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).at(0, 2), abcFeed())
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertFedFromRow(elements.get(0), 1);
            // element 1 is not targeted, so it must not consume a feed record
            assertNotFed(elements.get(1));
            assertFedFromRow(elements.get(2), 2);
        }

        @Test
        void range() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).range(1, 2), abcFeed())
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertNotFed(elements.get(0));
            assertFedFromRow(elements.get(1), 1);
            assertFedFromRow(elements.get(2), 2);
        }

        @Test
        void except() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).except(1), abcFeed())
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertFedFromRow(elements.get(0), 1);
            assertNotFed(elements.get(1));
            assertFedFromRow(elements.get(2), 2);
        }

        @Test
        void first() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).first(), abcFeed())
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertFedFromRow(elements.get(0), 1);
            assertNotFed(elements.get(1));
            assertNotFed(elements.get(2));
        }

        @Test
        void last() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).last(), abcFeed())
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertNotFed(elements.get(0));
            assertNotFed(elements.get(1));
            assertFedFromRow(elements.get(2), 1);
        }

        @Test
        void indexedArrayElement() {
            final AbcArrayHolder result = Instancio.of(AbcArrayHolder.class)
                    .applyFeed(elementOf(AbcArrayHolder::getAbcElements1).at(2), abcFeed())
                    .create();

            final StringsAbc[] elements = result.getAbcElements1();
            assertNotFed(elements[0]);
            assertNotFed(elements[1]);
            assertFedFromRow(elements[2], 1);
        }
    }

    @Nested
    class InnerTargets {

        @Test
        void fieldWithinEachElement() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getDef), defFeed())
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertThat(elements).hasSize(SIZE);
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(elements.get(i).getDef(), i + 1);
                // the elements themselves are not feed targets
                assertNotFed(elements.get(i));
            }
            // the same field outside the targeted container is unaffected
            assertNotFed(result.getAbc().getDef());
            result.getAbcElements2().forEach(element -> assertNotFed(element.getDef()));
        }

        @Test
        void fieldWithinElementAtIndex() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).at(1).field(StringsAbc::getDef), defFeed())
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertNotFed(elements.get(0).getDef());
            assertFedFromRow(elements.get(1).getDef(), 1);
            assertNotFed(elements.get(2).getDef());
        }

        @Test
        void targetSelectorWithinEachElement() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).target(all(StringsDef.class)), defFeed())
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(elements.get(i).getDef(), i + 1);
            }
            assertNotFed(result.getAbc().getDef());
        }

        @Test
        void innerTargetAppliesOnlyWithinDirectElementSubtree() {
            final List<AbcListHolder> results = Instancio.of(new TypeToken<List<AbcListHolder>>() {})
                    .applyFeed(elementOf(root()).target(field(StringsAbc.class, "def")), defFeed())
                    .create();

            assertThat(results).hasSize(SIZE);
            for (int i = 0; i < SIZE; i++) {
                final AbcListHolder holder = results.get(i);
                assertFedFromRow(holder.getAbc().getDef(), 2 * i + 1);
                assertFedFromRow(holder.getNested().getAbc().getDef(), 2 * i + 2);

                holder.getAbcElements1().forEach(element -> assertNotFed(element.getDef()));
                holder.getAbcElements2().forEach(element -> assertNotFed(element.getDef()));
            }
        }

        @Test
        void feedAppliesThroughMapWithinElement() {
            //@formatter:off
            @Data class MapElement { Map<String, StringsDef> defs; }
            @Data class MapElementHolder { List<MapElement> elements; }
            //@formatter:on

            final MapElementHolder result = Instancio.of(MapElementHolder.class)
                    .applyFeed(elementOf(field(MapElementHolder.class, "elements")).target(all(StringsDef.class)), defFeed())
                    .withSetting(Keys.MAP_MIN_SIZE, 1)
                    .withSetting(Keys.MAP_MAX_SIZE, 1)
                    .create();

            assertThat(result.getElements()).hasSize(SIZE).allSatisfy(element ->
                    assertThat(element.getDefs().values()).hasSize(1).allSatisfy(def ->
                            assertThat(def.getD()).matches("d[1-9]")));

            assertThat(result.getElements())
                    .flatExtracting(element -> List.copyOf(element.getDefs().values()))
                    .extracting(StringsDef::getD)
                    .doesNotHaveDuplicates();
        }

        @Test
        void threeLevelsDeepWithinEachElement() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).target(all(StringsGhi.class)), ghiFeed())
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertThat(elements).hasSize(SIZE);
            for (int i = 0; i < SIZE; i++) {
                assertGhiFedFromRow(elements.get(i).getDef().getGhi(), i + 1);
                assertNotFed(elements.get(i));
                assertNotFed(elements.get(i).getDef());
            }
            assertNotFed(result.getAbc().getDef().getGhi());
        }

        @Test
        void threeFeedsAtDifferentDepthsOfTheSameElement() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1), abcFeed())
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).target(all(StringsDef.class)), defFeed())
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).target(all(StringsGhi.class)), ghiFeed())
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertThat(elements).hasSize(SIZE);
            for (int i = 0; i < SIZE; i++) {
                final StringsAbc element = elements.get(i);
                assertFedFromRow(element, i + 1);
                assertFedFromRow(element.getDef(), i + 1);
                assertGhiFedFromRow(element.getDef().getGhi(), i + 1);
            }
            assertNotFed(result.getAbc());
            assertNotFed(result.getAbc().getDef());
            assertNotFed(result.getAbc().getDef().getGhi());
        }
    }

    @Nested
    class Precedence {

        @Test
        void generateOverridesFeedPropertyOfTargetedElements() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1), abcFeed())
                    .generate(field(StringsAbc.class, "a"), gen -> gen.string().prefix("gen_"))
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            for (int i = 0; i < SIZE; i++) {
                final StringsAbc element = elements.get(i);
                assertThat(element.getA()).startsWith("gen_");
                assertThat(element.getB()).isEqualTo("b" + (i + 1));
                assertThat(element.getC()).isEqualTo("c" + (i + 1));
            }
        }

        @Test
        void supplyOverridesFedFieldOfEachElement() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1), abcFeed())
                    .supply(field(StringsAbc.class, "b"), () -> "_supplied_")
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            for (int i = 0; i < SIZE; i++) {
                assertThat(elements.get(i).getA()).isEqualTo("a" + (i + 1));
                assertThat(elements.get(i).getB()).isEqualTo("_supplied_");
                assertThat(elements.get(i).getC()).isEqualTo("c" + (i + 1));
            }
        }

        @RepeatedTest(5)
        void elementOfFeedTakesPrecedenceOverRegularFeedSelector() {
            final Feed feed = Instancio.createFeed(XAbcFeed.class);

            final AbcListHolder result = Api.shuffleApiOrder(AbcListHolder.class,
                            api -> api.applyFeed(elementOf(AbcListHolder::getAbcElements1), abcFeed()),
                            api -> api.applyFeed(all(StringsAbc.class), feed))
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(elements.get(i), i + 1);
            }
            assertThat(result.getAbc().getA()).startsWith("x");
            assertThat(result.getAbcElements2())
                    .extracting(StringsAbc::getA)
                    .allSatisfy(a -> assertThat(a).startsWith("x"));
        }

        @Test
        void setOnIndexedElementFieldDoesNotConsumeThatColumn() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1), abcFeed())
                    .set(elementOf(AbcListHolder::getAbcElements1).at(0).field(StringsAbc::getA), "_set_")
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertThat(elements.get(0).getA()).isEqualTo("_set_");
            assertThat(elements.get(0).getB()).isEqualTo("b1");
            assertThat(elements.get(0).getC()).isEqualTo("c1");
            assertThat(elements.get(1).getA()).isEqualTo("a1");
            assertThat(elements.get(1).getB()).isEqualTo("b2");
            assertThat(elements.get(1).getC()).isEqualTo("c2");
            assertThat(elements.get(2).getA()).isEqualTo("a2");
            assertThat(elements.get(2).getB()).isEqualTo("b3");
            assertThat(elements.get(2).getC()).isEqualTo("c3");
        }

        // The all(StringsAbc.class) selector matches 11 records' worth of targets:
        // abc, the elements of abcElements2, and the nested holder's abc and elements
        @Feed.Source(string = """
                a,b,c
                xa1,xb1,xc1
                xa2,xb2,xc2
                xa3,xb3,xc3
                xa4,xb4,xc4
                xa5,xb5,xc5
                xa6,xb6,xc6
                xa7,xb7,xc7
                xa8,xb8,xc8
                xa9,xb9,xc9
                xa10,xb10,xc10
                xa11,xb11,xc11
                xa12,xb12,xc12""")
        private interface XAbcFeed extends Feed {}

    }

    @Nested
    class MultipleFeeds {

        private static @Data class Holder {
            StringsAbc abc;
            List<StringsAbc> elements;
        }

        @Feed.Source(string = """
                a,b,c
                f1a1,f1b1,f1c1
                f1a2,f1b2,f1c2
                f1a3,f1b3,f1c3""")
        private interface FirstFeed extends Feed {}

        @Feed.Source(string = """
                a,b,c
                f2a1,f2b1,f2c1
                f2a2,f2b2,f2c2
                f2a3,f2b3,f2c3""")
        private interface SecondFeed extends Feed {}

        @Test
        void disjointIndexSpecsOnTheSameContainer() {
            final Holder result = Instancio.of(Holder.class)
                    .applyFeed(elementOf(field(Holder.class, "elements")).at(0), Instancio.createFeed(FirstFeed.class))
                    .applyFeed(elementOf(field(Holder.class, "elements")).at(1), Instancio.createFeed(SecondFeed.class))
                    .create();

            final List<StringsAbc> elements = result.getElements();
            assertThat(elements.get(0).getA()).isEqualTo("f1a1");
            assertThat(elements.get(0).getB()).isEqualTo("f1b1");
            assertThat(elements.get(1).getA()).isEqualTo("f2a1");
            assertThat(elements.get(1).getB()).isEqualTo("f2b1");
            assertNotFed(elements.get(2));
        }

        @Test
        void lastAppliedFeedWinsWhenIndexSpecsOverlap() {
            final Holder result = Instancio.of(Holder.class)
                    .applyFeed(elementOf(field(Holder.class, "elements")), Instancio.createFeed(FirstFeed.class))
                    .applyFeed(elementOf(field(Holder.class, "elements")), Instancio.createFeed(SecondFeed.class))
                    .create();

            final List<StringsAbc> elements = result.getElements();
            for (int i = 0; i < SIZE; i++) {
                assertThat(elements.get(i).getA()).isEqualTo("f2a" + (i + 1));
            }
        }

        @Test
        void regularFeedCoversElementsNotTargetedByElementOfFeed() {
            final Holder result = Instancio.of(Holder.class)
                    .applyFeed(all(StringsAbc.class), Instancio.createFeed(FirstFeed.class))
                    .applyFeed(elementOf(field(Holder.class, "elements")).at(1), Instancio.createFeed(SecondFeed.class))
                    .create();

            // generation order: abc, then elements 0..2; the elementOf feed
            // claims element 1, the regular feed covers the rest
            assertThat(result.getAbc().getA()).isEqualTo("f1a1");

            final List<StringsAbc> elements = result.getElements();
            assertThat(elements.get(0).getA()).isEqualTo("f1a2");
            assertThat(elements.get(1).getA()).isEqualTo("f2a1");
            assertThat(elements.get(2).getA()).isEqualTo("f1a3");
        }

        @Feed.Source(string = """
                d,e,f
                xd1,xe1,xf1
                xd2,xe2,xf2
                xd3,xe3,xf3
                xd4,xe4,xf4""")
        private interface XDefFeed extends Feed {}

        @Test
        void innerTargetFeedCombinesWithRegularFeed() {
            final Holder result = Instancio.of(Holder.class)
                    .applyFeed(all(StringsDef.class), Instancio.createFeed(XDefFeed.class))
                    .applyFeed(elementOf(field(Holder.class, "elements")).field(StringsAbc::getDef), defFeed())
                    .create();

            // defs within the targeted container's elements use the elementOf feed
            final List<StringsAbc> elements = result.getElements();
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(elements.get(i).getDef(), i + 1);
            }

            // the def outside the targeted container uses the regular feed
            assertThat(result.getAbc().getDef().getD()).isEqualTo("xd1");
        }
    }

    @Nested
    class ContainerScopes {

        @Test
        void containerSelectorTargetingNestedClassField() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(field(AbcListHolder.Nested.class, "abcElements1")), abcFeed())
                    .create();

            final List<StringsAbc> nestedElements = result.getNested().getAbcElements1();
            assertThat(nestedElements).hasSize(SIZE);
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(nestedElements.get(i), i + 1);
            }

            // the same-named field of the outer class is unaffected
            assertThat(result.getAbcElements1())
                    .hasSize(SIZE)
                    .allSatisfy(ElementOfSelectorFeedTest::assertNotFed);
        }

        @Test
        void containerSelectorWithScope() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(
                            elementOf(all(List.class).within(scope(AbcListHolder.Nested.class))),
                            abcFeed())
                    .create();

            assertThat(result.getNested().getAbcElements1())
                    .hasSize(SIZE)
                    .allSatisfy(element -> assertThat(element.getA()).matches("a\\d"));
            assertThat(result.getNested().getAbcElements2())
                    .hasSize(SIZE)
                    .allSatisfy(element -> assertThat(element.getA()).matches("a\\d"));
            assertThat(result.getAbcElements1()).allSatisfy(ElementOfSelectorFeedTest::assertNotFed);
            assertThat(result.getAbcElements2()).allSatisfy(ElementOfSelectorFeedTest::assertNotFed);
        }

        @Test
        void chainedScopesNarrowToNestedClassField() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(
                            elementOf(all(List.class).within(
                                    scope(AbcListHolder.Nested.class),
                                    scope(AbcListHolder.Nested.class, "abcElements1"))),
                            abcFeed())
                    .create();

            final List<StringsAbc> nested = result.getNested().getAbcElements1();
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(nested.get(i), i + 1);
            }
            assertThat(result.getNested().getAbcElements2()).allSatisfy(ElementOfSelectorFeedTest::assertNotFed);
            assertThat(result.getAbcElements1()).allSatisfy(ElementOfSelectorFeedTest::assertNotFed);
        }
    }

    @Nested
    class ContainerWidening {

        @Test
        void indexSpecWidensContainerBeyondConfiguredSize() {
            final int index = SIZE + 1;

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).at(index), abcFeed())
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertThat(elements).hasSize(index + 1);
            assertFedFromRow(elements.get(index), 1);
            for (int i = 0; i < index; i++) {
                assertNotFed(elements.get(i));
            }
        }

        @Test
        void indexSpecConflictingWithExplicitSizeThrowsError() {
            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .generate(field(AbcListHolder.class, "abcElements1"), gen -> gen.collection().size(2))
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).at(4), abcFeed());

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("requires at least 5 elements");
        }
    }

    @Nested
    class Models {

        @Test
        void feedFromModelIsAppliedOnCreate() {
            final Model<AbcListHolder> model = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).at(1), abcFeed())
                    .toModel();

            final AbcListHolder result = Instancio.create(model);

            final List<StringsAbc> elements = result.getAbcElements1();
            assertNotFed(elements.get(0));
            assertFedFromRow(elements.get(1), 1);
            assertNotFed(elements.get(2));
        }

        @Test
        void modelWithElementOfFeedConsumedViaSetModel() {
            final Model<AbcListHolder> model = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1), abcFeed())
                    .toModel();

            final List<AbcListHolder> results = Instancio.of(new TypeToken<List<AbcListHolder>>() {})
                    .setModel(all(AbcListHolder.class), model)
                    .create();

            assertThat(results).hasSize(SIZE).allSatisfy(holder -> {
                assertThat(holder.getAbcElements1())
                        .hasSize(SIZE)
                        .allSatisfy(element -> assertThat(element.getA()).matches("a\\d"));
                assertThat(holder.getAbcElements2())
                        .allSatisfy(ElementOfSelectorFeedTest::assertNotFed);
            });
            assertThat(results)
                    .flatExtracting(AbcListHolder::getAbcElements1)
                    .extracting(StringsAbc::getA)
                    .doesNotHaveDuplicates();
        }

        @Test
        void modelReusedAcrossCreatesAdvancesFeedState() {
            final Model<AbcListHolder> model = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1), abcFeed())
                    .toModel();

            final AbcListHolder first = Instancio.create(model);
            final AbcListHolder second = Instancio.create(model);

            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(first.getAbcElements1().get(i), i + 1);
                assertFedFromRow(second.getAbcElements1().get(i), SIZE + i + 1);
            }
        }

        @Test
        void setModelWithElementOfTargetAndFeedInsideModelIsNotSupported() {
            final Model<StringsAbc> model = Instancio.of(StringsAbc.class)
                    .applyFeed(root(), abcFeed())
                    .toModel();

            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1), model);

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(UnusedSelectorException.class)
                    .hasMessageContaining("scope(elementOf(AbcListHolder::getAbcElements1))");
        }
    }

    @Nested
    class SetModelWithInnerElementOfFeed {

        @Test
        void wholeElementsOfListModel() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .applyFeed(elementOf(root()), abcFeed())
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertThat(elements).hasSize(SIZE);
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(elements.get(i), i + 1);
            }

            // the same-typed sibling list is untouched
            assertThat(result.getAbcElements2()).allSatisfy(ElementOfSelectorFeedTest::assertNotFed);
        }

        @Test
        void singleIndexOfListModel() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .applyFeed(elementOf(root()).at(1), abcFeed())
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertNotFed(elements.get(0));
            assertFedFromRow(elements.get(1), 1);
            assertNotFed(elements.get(2));
        }

        @Test
        void innerFieldOfElementsOfListModel() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .applyFeed(elementOf(root()).field(StringsAbc::getDef), defFeed())
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(elements.get(i).getDef(), i + 1);
                assertNotFed(elements.get(i));
            }
        }

        @Test
        void deepTargetWithinElementsOfListModel() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .applyFeed(elementOf(root()).target(all(StringsDef.class)), defFeed())
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(elements.get(i).getDef(), i + 1);
                assertNotFed(elements.get(i));
            }
            assertNotFed(result.getAbc().getDef());
        }

        @Test
        void appliedToAllListsViaBroadContainerSelector() {
            // A single feed instance is shared across all four lists (12 elements),
            // so records are consumed sequentially across containers; use RECYCLE so
            // the 9-row feed covers all 12 elements.
            final Feed recyclingFeed = Instancio.ofFeed(AbcFeed.class)
                    .withSetting(Keys.FEED_DATA_END_ACTION, FeedDataEndAction.RECYCLE)
                    .create();

            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .applyFeed(elementOf(root()), recyclingFeed)
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(all(List.class), innerModel)
                    .create();

            // every element across every list is fed a row
            assertThat(result.getAbcElements1()).allSatisfy(e -> assertThat(e.getA()).matches("a[1-9]"));
            assertThat(result.getAbcElements2()).allSatisfy(e -> assertThat(e.getA()).matches("a[1-9]"));
            assertThat(result.getNested().getAbcElements1()).allSatisfy(e -> assertThat(e.getA()).matches("a[1-9]"));
            assertThat(result.getNested().getAbcElements2()).allSatisfy(e -> assertThat(e.getA()).matches("a[1-9]"));

            // the first list consumes rows 1..3
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(result.getAbcElements1().get(i), i + 1);
            }
        }

        @Test
        void scopedContainerOnlyFedElements() {
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .applyFeed(elementOf(root()), abcFeed())
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(all(List.class).within(scope(AbcListHolder.Nested.class)), innerModel)
                    .create();

            // only the scoped (nested) lists are fed; both share one feed instance,
            // so list1 consumes rows 1..3 and list2 continues with rows 4..6
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(result.getNested().getAbcElements1().get(i), i + 1);
                assertFedFromRow(result.getNested().getAbcElements2().get(i), SIZE + i + 1);
            }
            assertThat(result.getAbcElements1()).allSatisfy(ElementOfSelectorFeedTest::assertNotFed);
            assertThat(result.getAbcElements2()).allSatisfy(ElementOfSelectorFeedTest::assertNotFed);
        }

        @Test
        void dataEndRecycleSettingCarriedThroughModel() {
            final Feed recyclingFeed = Instancio.ofFeed(AbcFeed.class)
                    .withSetting(Keys.FEED_DATA_END_ACTION, FeedDataEndAction.RECYCLE)
                    .create();

            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .applyFeed(elementOf(root()), recyclingFeed)
                    .toModel();

            // bigger list than the inner model's own list size; the feed setting
            // (recycle) must be carried through setModel
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .size(field(AbcListHolder::getAbcElements1), 12)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertThat(elements).hasSize(12);
            for (int i = 0; i < 9; i++) {
                assertFedFromRow(elements.get(i), i + 1);
            }
            // recycled back to the first 3 rows
            assertFedFromRow(elements.get(9), 1);
            assertFedFromRow(elements.get(10), 2);
            assertFedFromRow(elements.get(11), 3);
        }

        @Test
        void outerFeedOnElementsCombinesWithInnerElementModel() {
            // inner model fixes def.d on the whole element; outer feeds the element fields
            final Model<StringsAbc> elementModel = Instancio.of(StringsAbc.class)
                    .set(field(StringsDef::getD), "_fixed_")
                    .toModel();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .setModel(elementOf(AbcListHolder::getAbcElements1).at(0), elementModel)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1), abcFeed())
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(elements.get(i), i + 1);
            }
            // inner-model set() wins over the feed for def.d on the targeted element
            assertThat(elements.get(0).getDef().getD()).isEqualTo("_fixed_");
        }

        @Test
        void innerModelFeedCombinesWithOuterRegularFeed() {
            // inner list-model feeds def via elementOf; outer feeds the abc field directly
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .applyFeed(elementOf(root()).field(StringsAbc::getDef), defFeed())
                    .toModel();

            final Feed outerDefFeed = Instancio.ofFeed(DefFeed.class)
                    .withSetting(Keys.FEED_DATA_END_ACTION, FeedDataEndAction.RECYCLE)
                    .create();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(all(StringsDef.class), outerDefFeed)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel)
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            // defs of the targeted container use the elementOf feed from the inner model
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(elements.get(i).getDef(), i + 1);
            }
            // the standalone def uses the outer regular feed
            assertThat(result.getAbc().getDef().getD()).isEqualTo("d1");
        }

        @Test
        void unusedInnerFeedSelectorReported() {
            // inner model feeds elementOf(field(StringsAbc::getA)) which is not a collection,
            // so the feed selector is never used
            final Model<List<StringsAbc>> innerModel = Instancio.ofList(StringsAbc.class)
                    .applyFeed(elementOf(field(StringsAbc::getA)), abcFeed())
                    .toModel();

            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .setModel(field(AbcListHolder::getAbcElements1), innerModel);

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("unused");
        }
    }

    @Nested
    class FeedOptions {

        @Feed.Source(string = """
                a,b,c
                a1,b1,c1
                a2,b2,c2""")
        private interface TwoRowFeed extends Feed {}

        @Test
        void dataEndActionRecycleRestartsFromTheFirstRecord() {
            final Feed twoRowFeed = Instancio.ofFeed(TwoRowFeed.class)
                    .withSetting(Keys.FEED_DATA_END_ACTION, FeedDataEndAction.RECYCLE)
                    .create();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1), twoRowFeed)
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertFedFromRow(elements.get(0), 1);
            assertFedFromRow(elements.get(1), 2);
            assertFedFromRow(elements.get(2), 1);
        }

        @Feed.Source(string = """
                a,b,c,unmatchedProperty
                a1,b1,c1,x1""")
        private interface FeedWithUnmatchedProperty extends Feed {}

        @Test
        void onFeedPropertyUnmatchedFailIsHonouredForElementTargets() {
            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .applyFeed(
                            elementOf(AbcListHolder::getAbcElements1),
                            Instancio.createFeed(FeedWithUnmatchedProperty.class));

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("unmatchedProperty");
        }
    }

    @Nested
    class UnusedSelectors {

        @Test
        void containerNotPresentInNodeTree() {
            final InstancioApi<Person> api = Instancio.of(Person.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1), abcFeed());

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(UnusedSelectorException.class)
                    .hasMessageContaining("elementOf(AbcListHolder::getAbcElements1)");
        }

        @Test
        void innerTargetOnNonPojoFieldIsNotAFeedTarget() {
            // feeds can only be applied to POJOs/records; 'a' is a String
            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getA), abcFeed());

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(UnusedSelectorException.class)
                    .hasMessageContaining("elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getA)");
        }

        @Test
        void nonPojoElementsAreNotFeedTargets() {
            final InstancioApi<List<String>> api = Instancio.of(new TypeToken<List<String>>() {})
                    .applyFeed(elementOf(root()), abcFeed());

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(UnusedSelectorException.class)
                    .hasMessageContaining("elementOf(root())");
        }

        @Test
        void mapContainersAreNotSupported() {
            final InstancioApi<Map<String, StringsAbc>> api = Instancio.of(new TypeToken<Map<String, StringsAbc>>() {})
                    .applyFeed(elementOf(root()), abcFeed());

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(UnusedSelectorException.class)
                    .hasMessageContaining("elementOf(root())");
        }

        @Test
        void lenientSuppressesUnusedSelectorError() {
            final Person result = Instancio.of(Person.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).lenient(), abcFeed())
                    .create();

            assertThat(result).isNotNull();
        }
    }

    @Nested
    class Streaming {

        @Test
        void recordsAdvanceAcrossStreamedListElements() {
            final List<List<StringsAbc>> streamed = Instancio.of(new TypeToken<List<StringsAbc>>() {})
                    .applyFeed(elementOf(root()), abcFeed())
                    .stream()
                    .limit(2)
                    .toList();

            assertThat(streamed).hasSize(2);
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(streamed.get(0).get(i), i + 1);
                assertFedFromRow(streamed.get(1).get(i), SIZE + i + 1);
            }
        }

        @Test
        void recycleAcrossLimitedStream() {
            final Feed recyclingFeed = Instancio.ofFeed(AbcFeed.class)
                    .withSetting(Keys.FEED_DATA_END_ACTION, FeedDataEndAction.RECYCLE)
                    .create();

            final List<List<StringsAbc>> streamed = Instancio.of(new TypeToken<List<StringsAbc>>() {})
                    .applyFeed(elementOf(root()), recyclingFeed)
                    .stream()
                    .limit(4)
                    .toList();

            final List<StringsAbc> flat = streamed.stream()
                    .flatMap(List::stream)
                    .toList();

            assertThat(flat).hasSize(12);
            for (int i = 0; i < 9; i++) {
                assertFedFromRow(flat.get(i), i + 1);
            }
            assertFedFromRow(flat.get(9), 1);
            assertFedFromRow(flat.get(10), 2);
            assertFedFromRow(flat.get(11), 3);
        }
    }

    @Nested
    class Records {

        private record RecordHolder(List<StringsAbcRecord> records) {}

        private record NestedRecord(StringsAbcRecord inner) {}

        private record NestedRecordHolder(List<NestedRecord> records) {}

        @Test
        void wholeRecordElementsAreFed() {
            final RecordHolder result = Instancio.of(RecordHolder.class)
                    .applyFeed(elementOf(RecordHolder::records), abcFeed())
                    .create();

            final List<StringsAbcRecord> records = result.records();
            assertThat(records).hasSize(SIZE);
            for (int i = 0; i < SIZE; i++) {
                final StringsAbcRecord r = records.get(i);
                assertThat(r.a()).isEqualTo("a" + (i + 1));
                assertThat(r.b()).isEqualTo("b" + (i + 1));
                assertThat(r.c()).isEqualTo("c" + (i + 1));
            }
        }

        @Test
        void recordComponentFedViaField() {
            final RecordHolder result = Instancio.of(RecordHolder.class)
                    .applyFeed(elementOf(RecordHolder::records).field(StringsAbcRecord::def), defFeed())
                    .create();

            final List<StringsAbcRecord> records = result.records();
            assertThat(records).hasSize(SIZE);
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(records.get(i).def(), i + 1);
            }
        }

        @Test
        void recordNestedInRecordInnerComponentFed() {
            final NestedRecordHolder result = Instancio.of(NestedRecordHolder.class)
                    .applyFeed(elementOf(NestedRecordHolder::records).target(all(StringsDefRecord.class)), defFeed())
                    .create();

            final List<NestedRecord> records = result.records();
            assertThat(records).hasSize(SIZE);
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(records.get(i).inner().def(), i + 1);
            }
        }
    }

    @Nested
    class SelectorGroups {

        @Test
        void groupOfTwoElementOfSelectors() {
            final Feed recyclingFeed = Instancio.ofFeed(AbcFeed.class)
                    .withSetting(Keys.FEED_DATA_END_ACTION, FeedDataEndAction.RECYCLE)
                    .create();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(
                            all(elementOf(AbcListHolder::getAbcElements1),
                                    elementOf(AbcListHolder::getAbcElements2)),
                            recyclingFeed)
                    .create();

            assertThat(result.getAbcElements1()).allSatisfy(e -> assertThat(e.getA()).matches("a[1-9]"));
            assertThat(result.getAbcElements2()).allSatisfy(e -> assertThat(e.getA()).matches("a[1-9]"));
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(result.getAbcElements1().get(i), i + 1);
                assertFedFromRow(result.getAbcElements2().get(i), SIZE + i + 1);
            }
        }

        @Test
        void groupOfIndexedElementSelectors() {
            final Feed recyclingFeed = Instancio.ofFeed(AbcFeed.class)
                    .withSetting(Keys.FEED_DATA_END_ACTION, FeedDataEndAction.RECYCLE)
                    .create();

            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(
                            all(elementOf(AbcListHolder::getAbcElements1).at(0),
                                    elementOf(AbcListHolder::getAbcElements1).last()),
                            recyclingFeed)
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            assertFedFromRow(elements.get(0), 1);
            assertNotFed(elements.get(1));
            assertFedFromRow(elements.get(2), 2);
        }
    }

    @Nested
    class BoundedRecursion {

        private static @Data class RecursiveHolder {
            String id;
            List<StringsAbc> elements;
            RecursiveHolder child;
        }

        @Test
        void elementOfFeedOnCollectionFieldOfBoundedRecursiveHolder() {
            final RecursiveHolder result = Instancio.of(RecursiveHolder.class)
                    .withMaxDepth(3)
                    .applyFeed(elementOf(field(RecursiveHolder.class, "elements")), abcFeed())
                    .create();

            assertThat(result.getElements()).allSatisfy(e -> assertThat(e.getA()).matches("a[1-9]"));
            assertThat(result.getElements())
                    .extracting(StringsAbc::getA)
                    .doesNotHaveDuplicates();
        }
    }

    @Nested
    class IgnoreInteraction {

        @Test
        void ignoreElementFieldWinsOverFeed() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1), abcFeed())
                    .ignore(elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getA))
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            for (int i = 0; i < SIZE; i++) {
                assertThat(elements.get(i).getA()).isNull();
                assertThat(elements.get(i).getB()).isEqualTo("b" + (i + 1));
                assertThat(elements.get(i).getC()).isEqualTo("c" + (i + 1));
            }
        }
    }

    @Nested
    class MaxDepthInteraction {

        @Test
        void feedBeyondMaxDepthIsUnused() {
            final InstancioApi<AbcListHolder> api = Instancio.of(AbcListHolder.class)
                    .withMaxDepth(2)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getDef), defFeed());

            assertThatThrownBy(api::create)
                    .isInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("unused");
        }

        @Test
        void feedAppliesToElementWithinMaxDepth() {
            final AbcListHolder result = Instancio.of(AbcListHolder.class)
                    .withMaxDepth(3)
                    .applyFeed(elementOf(AbcListHolder::getAbcElements1), abcFeed())
                    .create();

            final List<StringsAbc> elements = result.getAbcElements1();
            for (int i = 0; i < SIZE; i++) {
                assertFedFromRow(elements.get(i), i + 1);
            }
        }
    }
}

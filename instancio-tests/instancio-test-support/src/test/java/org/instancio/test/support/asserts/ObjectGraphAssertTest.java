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
package org.instancio.test.support.asserts;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@SuppressWarnings({"java:S5778", "rawtypes"})
class ObjectGraphAssertTest {

    private static final String EXPECTED = "expected";
    private static final String WRONG = "wrong";

    private static class Leaf {
        String name;
        int count;

        Leaf(String name) {
            this.name = name;
            this.count = 1;
        }
    }

    private static class WithCollections {
        String top;
        List<Leaf> leaves;
        Leaf[] leafArray;
        String[] tags;
        Map<String, Leaf> leafMap;
        Map<Leaf, String> leafKeyMap;
        int[] primitives;
    }

    private static class Cyclic {
        String name;
        Cyclic self;
    }

    private static class WithNullElements {
        List<Integer> intList;
        Integer[] intArray;
        Map<String, Integer> intMap;
        List rawList;
        List<? extends Number> wildcardList;
    }

    private WithCollections buildAllExpected() {
        final WithCollections w = new WithCollections();
        w.top = EXPECTED;
        w.leaves = List.of(new Leaf(EXPECTED), new Leaf(EXPECTED));
        w.leafArray = new Leaf[]{new Leaf(EXPECTED), new Leaf(EXPECTED)};
        w.tags = new String[]{EXPECTED, EXPECTED};
        w.leafMap = new HashMap<>();
        w.leafMap.put("k1", new Leaf(EXPECTED));
        w.leafMap.put("k2", new Leaf(EXPECTED));
        w.leafKeyMap = new HashMap<>();
        w.leafKeyMap.put(new Leaf(EXPECTED), EXPECTED);
        w.primitives = new int[]{1, 2, 3};
        return w;
    }

    private WithCollections buildPartitionGraph() {
        // EXPECTED appears only in `leaves` and `top`; everything else is WRONG.
        final WithCollections w = new WithCollections();
        w.top = WRONG;
        w.leaves = List.of(new Leaf(EXPECTED), new Leaf(EXPECTED));
        w.leafArray = new Leaf[]{new Leaf(WRONG), new Leaf(WRONG)};
        w.tags = new String[]{WRONG, WRONG};
        w.leafMap = new HashMap<>();
        w.leafMap.put("k1", new Leaf(WRONG));
        w.leafKeyMap = new HashMap<>();
        w.leafKeyMap.put(new Leaf(WRONG), WRONG);
        w.primitives = new int[]{1};
        return w;
    }

    private WithNullElements buildWithNullElements() {
        final WithNullElements w = new WithNullElements();
        w.intList = new ArrayList<>();
        w.intList.add(1);
        w.intList.add(null); // index 1 is null
        w.intList.add(3);
        w.intArray = new Integer[]{1, null, 3}; // index 1 is null
        w.intMap = new HashMap<>();
        w.intMap.put("a", 1);
        w.intMap.put("b", null); // value is null
        w.rawList = new ArrayList<>();
        w.rawList.add("hello");
        w.rawList.add(null);
        final List<Integer> wcList = new ArrayList<>();
        wcList.add(1);
        wcList.add(null);
        wcList.add(3);
        w.wildcardList = wcList;
        return w;
    }

    @Nested
    class HasAllValues {

        @Test
        void passesWhenEveryStringMatches() {
            final WithCollections w = buildAllExpected();

            assertThatGraph(w).hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }

        @Test
        void failsOnTopLevelString() {
            final WithCollections w = buildAllExpected();
            w.top = WRONG;

            assertThatThrownBy(() ->
                    assertThatGraph(w).hasAllValuesOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("top");
        }

        @Test
        void failsOnStringInListElement() {
            final WithCollections w = buildAllExpected();
            w.leaves.get(1).name = WRONG;

            assertThatThrownBy(() ->
                    assertThatGraph(w).hasAllValuesOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("name");
        }

        @Test
        void failsOnStringInArrayElement() {
            final WithCollections w = buildAllExpected();
            w.leafArray[0].name = WRONG;

            assertThatThrownBy(() ->
                    assertThatGraph(w).hasAllValuesOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("name");
        }

        @Test
        void failsOnStringInMapValue() {
            final WithCollections w = buildAllExpected();
            w.leafMap.get("k1").name = WRONG;

            assertThatThrownBy(() ->
                    assertThatGraph(w).hasAllValuesOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("name");
        }

        @Test
        void failsOnStringInMapKey() {
            final WithCollections w = buildAllExpected();
            w.leafKeyMap.keySet().iterator().next().name = WRONG;

            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .hasAllValuesOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("name");
        }

        @Test
        void failsOnArrayStringElement() {
            final WithCollections w = buildAllExpected();
            w.tags = new String[]{EXPECTED, WRONG};

            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .hasAllValuesOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("tags[1]");
        }

        @Test
        void failsOnMapStringValue() {
            final WithCollections w = buildAllExpected();
            final Leaf key = w.leafKeyMap.keySet().iterator().next();
            w.leafKeyMap.put(key, WRONG);

            assertThatThrownBy(() ->
                    assertThatGraph(w).hasAllValuesOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("leafKeyMap");
        }

        @Test
        void matchesRoot() {
            assertThatGraph(EXPECTED).hasAllValuesOfTypeEqualTo(String.class, EXPECTED);

            assertThatThrownBy(() ->
                    assertThatGraph(WRONG)
                            .hasAllValuesOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("<root>");
        }

        @Test
        void worksForPrimitiveType() {
            final WithCollections w = buildAllExpected();
            assertThatGraph(w).hasAllValuesOfTypeEqualTo(int.class, 1);

            w.leaves.get(0).count = 99;
            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .hasAllValuesOfTypeEqualTo(int.class, 1))
                    .isInstanceOf(AssertionFailedError.class)
                    .hasMessageContaining("count");
        }

        @Test
        void handlesCyclicReferences() {
            final Cyclic c = new Cyclic();
            c.name = EXPECTED;
            c.self = c;

            assertThatGraph(c).hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }

        @Test
        void failsWhenTypeHasNoCandidates() {
            final Leaf leaf = new Leaf(EXPECTED);

            assertThatThrownBy(() ->
                    assertThatGraph(leaf)
                            .hasAllValuesOfTypeEqualTo(Date.class, null))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("Expected at least one value of type");
        }

        @Test
        void failsWhenTypeAbsentFromGraph() {
            class Foo {}

            final WithCollections w = buildAllExpected();

            assertThatThrownBy(() -> assertThatGraph(w).hasAllValuesOfTypeEqualTo(Foo.class, BigInteger.ONE))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("Expected at least one value of type");
        }
    }

    @Nested
    class HasNoValue {

        @Test
        void passesWhenValueAbsent() {
            final WithCollections w = buildAllExpected();

            assertThatGraph(w).hasNoValueOfTypeEqualTo(String.class, "never-present");
        }

        @Test
        void failsWhenNestedFieldHoldsValue() {
            final WithCollections w = buildAllExpected();

            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .hasNoValueOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("not to be equal to");
        }

        @Test
        void failsOnTopLevel() {
            Leaf leaf = new Leaf(EXPECTED);

            assertThatThrownBy(() -> assertThatGraph(leaf).hasNoValueOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("name");
        }

        @Test
        void detectsSentinelInListElement() {
            final WithCollections w = buildAllExpected();
            final Leaf sentinel = new Leaf("DELETED");
            w.leaves = List.of(new Leaf(EXPECTED), sentinel);

            assertThatThrownBy(() -> assertThatGraph(w).hasNoValueOfTypeEqualTo(Leaf.class, sentinel))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("leaves[1]");
        }

        @Test
        void mapKeyIsTraversedButNotEmittedAsValue() {
            final WithCollections w = buildAllExpected();
            final Leaf key = w.leafKeyMap.keySet().iterator().next();

            // the key's `name` field is reachable, so a String assertion sees it
            assertThatThrownBy(() -> assertThatGraph(w).hasNoValueOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionError.class);

            // the key object itself is never asserted as a value
            assertThatGraph(w).hasNoValueOfTypeEqualTo(Leaf.class, key);
        }
    }

    @Nested
    class InferredType {

        @Test
        void hasAllValuesEqualTo() {
            final WithCollections w = buildAllExpected();

            assertThatGraph(w).hasAllValuesEqualTo(EXPECTED);
        }

        @Test
        void hasNoValueEqualTo() {
            final WithCollections w = buildAllExpected();

            assertThatThrownBy(() -> assertThatGraph(w).hasNoValueEqualTo(EXPECTED))
                    .isInstanceOf(AssertionError.class);
        }

        @Test
        void hasValuesEqualToExactlyIn() {
            final WithCollections w = buildPartitionGraph();

            assertThatGraph(w).hasValuesEqualToExactlyIn(EXPECTED, "leaves");
        }

        @Test
        void rejectsNullValue() {
            final WithCollections w = buildAllExpected();

            assertThatThrownBy(() -> assertThatGraph(w).hasAllValuesEqualTo(null))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("cannot infer type");
        }
    }

    @Nested
    class CustomAssertions {

        @Test
        void allValuesOfTypeSatisfy() {
            final WithCollections w = buildAllExpected();

            assertThatGraph(w)
                    .allValuesOfTypeSatisfy(String.class, v -> assertThat((String) v).startsWith("ex"));
        }

        @Test
        void assertEachValueOfTypeVisitsAllPaths() {
            final WithCollections w = buildAllExpected();
            final List<String> paths = new ArrayList<>();

            assertThatGraph(w)
                    .assertEachValueOfType(String.class, (path, value) -> paths.add(path));

            assertThat(paths)
                    .hasSizeGreaterThanOrEqualTo(8)
                    .contains("<root>.top")
                    .anyMatch(p -> p.endsWith(".name"));
        }
    }

    @Nested
    class ExcludingPaths {

        @Test
        void skipsMatchingPath() {
            final WithCollections w = buildAllExpected();
            w.top = WRONG;

            assertThatGraph(w)
                    .excludingPaths("top")
                    .hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }

        @Test
        void stillAssertsSiblingPaths() {
            final WithCollections w = buildAllExpected();
            w.top = WRONG;
            w.leaves.get(0).name = WRONG;

            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .excludingPaths("top")
                            .hasAllValuesOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("name");
        }

        @Test
        void globMatchesEveryNestedName() {
            final WithCollections w = buildAllExpected();
            for (Leaf l : w.leaves) l.name = WRONG;
            for (Leaf l : w.leafArray) l.name = WRONG;
            w.leafMap.values().forEach(l -> l.name = WRONG);
            w.leafKeyMap.keySet().forEach(l -> l.name = WRONG);

            assertThatGraph(w)
                    .excludingPaths("**.name")
                    .hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }

        @Test
        void plainNameIsRootAnchored() {
            final WithCollections w = buildAllExpected();

            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .excludingPaths("name")
                            .hasAllValuesOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("did not match");
        }

        @Test
        void doubleStarMatchesNestedName() {
            final WithCollections w = buildAllExpected();
            for (Leaf l : w.leaves) l.name = WRONG;

            assertThatGraph(w)
                    .excludingPaths("**.name")
                    .hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }

        @Test
        void byExactIndex() {
            final WithCollections w = buildAllExpected();
            w.leaves.get(0).name = WRONG;

            assertThatGraph(w)
                    .excludingPaths("leaves[0].name")
                    .hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }

        @Test
        void byIndexUnion() {
            final WithCollections w = buildAllExpected();
            w.leaves.get(0).name = WRONG;
            w.leaves.get(1).name = WRONG;

            assertThatGraph(w)
                    .excludingPaths("leaves[0,1].name")
                    .hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }

        @Test
        void byIndexRange() {
            final WithCollections w = buildAllExpected();
            w.leaves.get(0).name = WRONG;
            w.leaves.get(1).name = WRONG;

            assertThatGraph(w)
                    .excludingPaths("leaves[0-1].name")
                    .hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }

        @Test
        void byIndexWildcard() {
            final WithCollections w = buildAllExpected();
            for (Leaf l : w.leafArray) l.name = WRONG;

            assertThatGraph(w)
                    .excludingPaths("leafArray[*].name")
                    .hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }

        @Test
        void byMapKey() {
            final WithCollections w = buildAllExpected();
            w.leafMap.get("k1").name = WRONG;

            assertThatGraph(w)
                    .excludingPaths("leafMap[k1].name")
                    .hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }

        @Test
        void unmatchedPatternFails() {
            final WithCollections w = buildAllExpected();

            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .excludingPaths("nonexistent.path")
                            .hasAllValuesOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("did not match")
                    .hasMessageContaining("nonexistent.path");
        }

        @Test
        void everyPatternMustMatch() {
            final WithCollections w = buildAllExpected();
            w.top = WRONG;

            for (Leaf l : w.leaves) {
                l.name = WRONG;
            }

            assertThatGraph(w)
                    .excludingPaths("top", "**.name")
                    .hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }
    }

    @Nested
    class ExcludingSubtrees {

        @Test
        void skipsEntireSubtree() {
            final WithCollections w = buildAllExpected();
            for (Leaf l : w.leaves) {
                l.name = WRONG;
            }

            assertThatGraph(w)
                    .excludingSubtrees("leaves")
                    .excludingPaths("**.name")
                    .hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }

        @Test
        void unmatchedPatternFails() {
            final WithCollections w = buildAllExpected();

            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .excludingSubtrees("does.not.exist")
                            .hasAllValuesOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("does.not.exist");
        }

        @Test
        void byElementIndexExcludesOnlyThatElement() {
            final WithCollections w = buildAllExpected();
            w.leaves.get(0).name = WRONG;

            assertThatGraph(w)
                    .excludingSubtrees("leaves[0]")
                    .hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }

        @Test
        void byElementIndexFailsWhenSiblingDiffers() {
            final WithCollections w = buildAllExpected();
            w.leaves.get(1).name = WRONG;

            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .excludingSubtrees("leaves[0]")
                            .hasAllValuesOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("leaves[1].name");
        }

        @Test
        void rootAnchoredPatternMatchesRootField() {
            final WithCollections w = buildAllExpected();

            assertThatGraph(w)
                    .excludingSubtrees("leaves")
                    .excludingPaths("**.name")
                    .hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }
    }

    @Nested
    class IncludingPaths {

        @Test
        void restrictsScopeToMatchingPath() {
            final WithCollections w = buildAllExpected();
            w.top = WRONG;
            w.leaves.get(0).name = WRONG;
            w.leaves.get(1).name = EXPECTED;

            assertThatGraph(w)
                    .includingPaths("leaves[1].name")
                    .hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }

        @Test
        void failsWhenIncludedPathDiffers() {
            final WithCollections w = buildAllExpected();
            w.leaves.get(1).name = WRONG;

            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .includingPaths("leaves[1].name")
                            .hasAllValuesOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("leaves[1].name");
        }

        @Test
        void unmatchedPatternFails() {
            final WithCollections w = buildAllExpected();

            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .includingPaths("nonexistent.path")
                            .hasAllValuesOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("did not match")
                    .hasMessageContaining("nonexistent.path");
        }

        @Test
        void byIndexRange() {
            final WithCollections w = buildAllExpected();
            w.top = WRONG;
            w.leaves.get(0).name = EXPECTED;
            w.leaves.get(1).name = EXPECTED;

            assertThatGraph(w)
                    .includingPaths("leaves[0-1].name")
                    .hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }
    }

    @Nested
    class IncludingSubtrees {

        @Test
        void restrictsScopeToSubtree() {
            final WithCollections w = buildAllExpected();
            w.top = WRONG;

            assertThatGraph(w)
                    .includingSubtrees("leaves")
                    .hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }

        @Test
        void failsWhenInsideSubtreeDiffers() {
            final WithCollections w = buildAllExpected();
            w.leaves.get(0).name = WRONG;

            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .includingSubtrees("leaves")
                            .hasAllValuesOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("leaves[0].name");
        }

        @Test
        void ignoresPathsOutsideSubtree() {
            final WithCollections w = buildAllExpected();
            w.top = WRONG;

            assertThatGraph(w)
                    .includingSubtrees("leafMap")
                    .hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }

        @Test
        void unmatchedPatternFails() {
            final WithCollections w = buildAllExpected();

            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .includingSubtrees("does.not.exist")
                            .hasAllValuesOfTypeEqualTo(String.class, EXPECTED))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("did not match")
                    .hasMessageContaining("does.not.exist");
        }

        @Test
        void byMapKey() {
            final WithCollections w = buildAllExpected();
            w.leafMap.get("k1").name = WRONG;

            assertThatGraph(w)
                    .includingSubtrees("leafMap[k2]")
                    .hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }
    }

    @Nested
    class IncludeAndExclude {

        @Test
        void scopesIntersect() {
            final WithCollections w = buildAllExpected();
            w.top = WRONG;
            w.leaves.get(0).name = WRONG;
            w.leaves.get(1).name = EXPECTED;

            assertThatGraph(w)
                    .includingSubtrees("leaves")
                    .excludingPaths("leaves[0].name")
                    .hasAllValuesOfTypeEqualTo(String.class, EXPECTED);
        }

        @Test
        void includeSubtreeExcludeField() {
            final WithCollections w = buildAllExpected();
            w.leaves.get(0).name = WRONG;

            assertThatGraph(w)
                    .includingSubtrees("leaves")
                    .excludingPaths("**.name")
                    .hasNoValueOfTypeEqualTo(String.class, "this-value-doesnt-exist");
        }
    }

    @Nested
    class ExactlyIn {

        @Test
        void passesWhenPartitionMatches() {
            final WithCollections w = buildPartitionGraph();

            assertThatGraph(w)
                    .hasValuesOfTypeEqualToExactlyIn(String.class, EXPECTED, "leaves");
        }

        @Test
        void failsWhenInsideSubtreeNotExpected() {
            final WithCollections w = buildPartitionGraph();
            w.leaves.get(1).name = WRONG;

            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .hasValuesOfTypeEqualToExactlyIn(String.class, EXPECTED, "leaves"))
                    .isInstanceOf(AssertionError.class);
        }

        @Test
        void failsWhenExpectedLeaksOutsideSubtree() {
            final WithCollections w = buildPartitionGraph();
            w.top = EXPECTED;

            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .hasValuesOfTypeEqualToExactlyIn(String.class, EXPECTED, "leaves"))
                    .isInstanceOf(AssertionError.class);
        }

        @Test
        void supportsMultipleSubtrees() {
            final WithCollections w = buildPartitionGraph();
            w.top = EXPECTED;

            assertThatGraph(w)
                    .hasValuesOfTypeEqualToExactlyIn(String.class, EXPECTED, "leaves", "top");
        }

        @Test
        void unmatchedSubtreeFails() {
            final WithCollections w = buildPartitionGraph();

            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .hasValuesOfTypeEqualToExactlyIn(String.class, EXPECTED, "leaves", "doesNotExist"))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("doesNotExist");
        }

        @Test
        void worksForDomainType() {
            final Leaf sentinel = new Leaf(EXPECTED);
            final WithCollections w = buildPartitionGraph();
            w.leaves = List.of(sentinel, sentinel);

            assertThatGraph(w)
                    .hasValuesOfTypeEqualToExactlyIn(Leaf.class, sentinel, "leaves");
        }
    }

    @Nested
    class NullElementMatching {

        final WithNullElements w = buildWithNullElements();

        @Test
        void nullInList_matchedViaIncludingPaths() {
            assertThatGraph(w)
                    .includingPaths("intList[1]")
                    .hasAllValuesOfTypeEqualTo(Integer.class, null);
        }

        @Test
        void nullInArray_matchedViaIncludingPaths() {
            assertThatGraph(w)
                    .includingPaths("intArray[1]")
                    .hasAllValuesOfTypeEqualTo(Integer.class, null);
        }

        @Test
        void nullInMapValue_matchedViaIncludingPaths() {
            assertThatGraph(w)
                    .includingPaths("intMap[b]")
                    .hasAllValuesOfTypeEqualTo(Integer.class, null);
        }

        @Test
        void nullInList_exactlyIn_isolatedGraph() {
            // Single null element in an otherwise empty graph
            class Holder {
                List<Integer> list;
            }
            final Holder h = new Holder();
            h.list = new ArrayList<>();
            h.list.add(null);

            assertThatGraph(h)
                    .hasValuesOfTypeEqualToExactlyIn(Integer.class, null, "list[0]");
        }

        @Test
        void nullInList_exactlyIn_failsWhenNullLeaksToSiblingIndex() {
            // list = [null, null, 3]: null appears at indices 0 AND 1, but the
            // assertion claims null appears exactly at index 0. The null at index 1
            // leaks outside the expected subtree, so the contract must fail.
            class Holder {
                List<Integer> list;
            }
            final Holder h = new Holder();
            h.list = new ArrayList<>();
            h.list.add(null); // index 0
            h.list.add(null); // index 1 - leaks outside the expected subtree
            h.list.add(3);    // index 2

            assertThatThrownBy(() ->
                    assertThatGraph(h)
                            .hasValuesOfTypeEqualToExactlyIn(Integer.class, null, "list[0]"))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("list[1]");
        }

        @Test
        void nullInList_exactlyIn_passesWhenEveryNullInSubtree() {
            // Same graph as above, but the subtree now covers every null element.
            class Holder {
                List<Integer> list;
            }
            final Holder h = new Holder();
            h.list = new ArrayList<>();
            h.list.add(null); // index 0
            h.list.add(null); // index 1
            h.list.add(3);    // index 2

            assertThatGraph(h)
                    .hasValuesOfTypeEqualToExactlyIn(Integer.class, null, "list[0,1]");
        }

        @Test
        void nullInList_failsWhenValueExpectedOnNullPath() {
            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .includingPaths("intList[1]")
                            .hasAllValuesOfTypeEqualTo(Integer.class, 42))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("expected: 42")
                    .hasMessageContaining("but was: null");
        }

        @Test
        void nullInRawList_notMatched() {
            // Raw List has no declared element type, so null elements
            // cannot be matched; backward-compatible behavior.
            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .includingPaths("rawList[1]")
                            .hasAllValuesOfTypeEqualTo(String.class, null))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("includingPaths:rawList[1]");
        }

        @Test
        void nullInWildcardList_notMatchedWithSubtypeValueType() {
            // List<? extends Number> has elementType=Number
            // Integer.isAssignableFrom(Number) is false → declaredMatch=false
            // null can't match via isInstance → not matched
            assertThatThrownBy(() ->
                    assertThatGraph(w)
                            .includingPaths("wildcardList[1]")
                            .hasAllValuesOfTypeEqualTo(Integer.class, null))
                    .isInstanceOf(AssertionError.class)
                    .hasMessageContaining("includingPaths:wildcardList[1]");
        }

        @Test
        void nullInWildcardList_matchedWithNumberType() {
            // Number.isAssignableFrom(Number) is true → declaredMatch=true via includingPaths
            assertThatGraph(w)
                    .includingPaths("wildcardList[1]")
                    .hasAllValuesOfTypeEqualTo(Number.class, null);
        }

        @Test
        void hasNoValue_succeedsWhenNullsCoexist() {
            assertThatGraph(w).hasNoValueOfTypeEqualTo(Integer.class, 42);
        }
    }
}

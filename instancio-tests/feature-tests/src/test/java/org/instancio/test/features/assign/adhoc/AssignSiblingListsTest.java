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
package org.instancio.test.features.assign.adhoc;

import lombok.Data;
import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.collections.lists.TwoListsOfInteger;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.given;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

/**
 * Given 2 sibling list fields, set properties of one list
 * based on the contents of the other list.
 */
@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignSiblingListsTest {

    //@formatter:off
    private static @Data class Root { List<A> listA; List<B> listB; }
    private static @Data class A { Integer val; }
    private static @Data class B { Integer val; }
    //@formatter:on

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void aToB() {
        final int listSize = 10;
        final int expectedValue = -3;

        final Root result = Instancio.of(Root.class)
                .generate(field(A::getVal), gen -> gen.ints().range(-5, -1))
                .assign(Assign.given(Root::getListA)
                        .satisfies((List<A> l) -> l.stream().anyMatch(e -> e.val == expectedValue))
                        .generate(field(Root::getListB), gen -> gen.collection().size(listSize))
                        .set(field(B::getVal), expectedValue))
                .create();

        if (result.listA.stream().anyMatch(e -> e.val == expectedValue)) {
            assertThat(result.listB)
                    .hasSize(listSize)
                    .extracting(B::getVal)
                    .containsOnly(expectedValue);
        } else {
            assertThat(result.listB)
                    .hasSizeLessThan(listSize)
                    .extracting(B::getVal)
                    .doesNotContain(expectedValue);
        }
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void bToA() {
        final int listSize = 10;
        final int expectedValue = -3;

        final Root result = Instancio.of(Root.class)
                .generate(field(B::getVal), gen -> gen.ints().range(-5, -1))
                .assign(Assign.given(Root::getListB)
                        .satisfies((List<B> l) -> l.stream().anyMatch(e -> e.val == expectedValue))
                        .generate(field(Root::getListA), gen -> gen.collection().size(listSize))
                        .set(field(A::getVal), expectedValue))
                .create();

        if (result.listB.stream().anyMatch(e -> e.val == expectedValue)) {
            assertThat(result.listA)
                    .hasSize(listSize)
                    .extracting(A::getVal)
                    .containsOnly(expectedValue);
        } else {
            assertThat(result.listA)
                    .hasSizeLessThan(listSize)
                    .extracting(A::getVal)
                    .doesNotContain(expectedValue);
        }
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void interdependentLists() {
        final TargetSelector list1Elements = all(Integer.class).within(field(TwoListsOfInteger::getList1).toScope());

        final TwoListsOfInteger result = Instancio.of(TwoListsOfInteger.class)
                .assign(Assign.valueOf(list1Elements).generate(gen -> gen.ints().range(1, 10)))
                .assign(given(TwoListsOfInteger::getList1)
                        .satisfies((List<Integer> l) -> l.stream().allMatch(i -> i >= 1 && i <= 10))
                        .generate(field(TwoListsOfInteger::getList2), gen -> gen.collection().size(2)))
                .create();

        assertThat(result.getList1()).isNotEmpty().allSatisfy(i -> assertThat(i).isBetween(1, 10));
        assertThat(result.getList2()).hasSize(2);
    }
}

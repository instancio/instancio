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
package org.instancio.test.features.assign.adhoc;

import lombok.Data;
import org.assertj.core.api.ListAssert;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.given;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;

/**
 * Test for a bug that was fixed in the {@code DelayedNodeQueue} class.
 * The bug was caused by checking a {@code Set<InternalNode>} of "seen" nodes
 * (i.e. don't add node to the queue if already seen).
 */
@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignDelayedNodeVisitedCheckBugTest {

    //@formatter:off
    private static @Data class Root { String rootValue; Foo foo; Bar bar; }
    private static @Data class Foo { List<FooItem> fooItems; }
    private static @Data class FooItem { String id; }
    private static @Data class Bar { BarItemA barItemA; BarItemB barItemB; }
    private static @Data class BarItemA { BarItemId id; }
    private static @Data class BarItemB { BarItemId id; }
    private static @Data class BarItemId { String id; }
    //@formatter:on

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void delayedNodeQueue_seenBug() {
        final String barItemId1 = "barItemId1";
        final String barItemId2 = "barItemId2";
        final String rootValue = "rootValue";
        final String fooItemId = "fooItemId";

        final Root result = Instancio.of(Root.class)
                .generate(field(BarItemId::getId), gen -> gen.oneOf(barItemId1, barItemId2))

                .assign(given(field(BarItemId::getId).within(scope(BarItemA.class))).is(barItemId2)
                        .set(field(Root::getRootValue), rootValue))

                .assign(given(field(Root::getRootValue)).is(rootValue)
                        .set(field(FooItem::getId), fooItemId))

                .create();

        final BarItemId barItemId = result.getBar().getBarItemA().getId();

        assertThat(barItemId.getId()).isIn(barItemId1, barItemId2);

        final ListAssert<FooItem> fooItemsAssert = assertThat(result.getFoo().getFooItems()).isNotEmpty();

        if (barItemId.getId().equals(barItemId2)) {
            assertThat(result.getRootValue()).isEqualTo(rootValue);
            fooItemsAssert.allSatisfy(item -> assertThat(item.getId()).isEqualTo(fooItemId));
        } else {
            assertThat(result.getRootValue()).isNotEqualTo(rootValue);
            fooItemsAssert.allSatisfy(item -> assertThat(item.getId()).isNotEqualTo(fooItemId));
        }
    }
}

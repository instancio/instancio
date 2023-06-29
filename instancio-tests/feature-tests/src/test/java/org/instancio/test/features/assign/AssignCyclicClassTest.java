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
package org.instancio.test.features.assign;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.Selector;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.cyclic.ListNode;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;

@FeatureTag({
        Feature.ASSIGN,
        Feature.CYCLIC,
        Feature.DEPTH_SELECTOR,
        Feature.SCOPE
})
@ExtendWith(InstancioExtension.class)
class AssignCyclicClassTest {

    @Test
    @DisplayName("Using depth to target origin and destination selectors")
    void assignmentWithDepth() {
        final String rootVal = "root-val";
        final String childVal = "child-val";
        final Selector valueField = field(ListNode.class, "value");

        final ListNode<String> result = Instancio.of(new TypeToken<ListNode<String>>() {})
                .set(valueField.atDepth(1), rootVal)
                .assign(Assign.given(valueField.atDepth(1))
                        .satisfies(rootVal::equals)
                        .set(valueField.atDepth(2), childVal))
                .create();

        assertThat(result.getValue()).isEqualTo(rootVal);
        assertThat(result.getPrev().getValue()).isEqualTo(childVal);
        assertThat(result.getNext().getValue()).isEqualTo(childVal);

        // prev
        assertThat(result.getPrev().getPrev()).isNull();
        assertThat(result.getPrev().getNext()).isNull();

        // next
        assertThat(result.getNext().getNext()).isNull();
        assertThat(result.getNext().getPrev()).isNull();
    }

    @Test
    @DisplayName("Using depth to target origin and scope to target destination")
    void assignmentWithDepthAndScope() {
        final String rootVal = "root-val";
        final String childVal = "child-val";

        final Selector valueField = field(ListNode.class, "value");

        final ListNode<String> result = Instancio.of(new TypeToken<ListNode<String>>() {})
                .set(valueField.atDepth(1), rootVal)
                // values within prev
                .assign(Assign.given(valueField.atDepth(1))
                        .satisfies(rootVal::equals)
                        .set(valueField.within(scope(ListNode.class, "prev")), childVal))
                // values within next
                .assign(Assign.given(valueField.atDepth(1))
                        .satisfies(rootVal::equals)
                        .set(valueField.within(scope(ListNode.class, "next")), childVal))
                .create();

        //
        // Should set all values except root to childVal
        //
        assertThat(result.getValue()).isEqualTo(rootVal);
        assertThat(result.getPrev().getValue()).isEqualTo(childVal);
        assertThat(result.getNext().getValue()).isEqualTo(childVal);

        // prev
        assertThat(result.getPrev().getPrev()).isNull();
        assertThat(result.getPrev().getNext()).isNull();

        // next
        assertThat(result.getNext().getNext()).isNull();
        assertThat(result.getNext().getPrev()).isNull();
    }
}

/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.test.features.scope;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.test.support.pojo.cyclic.ListNode;
import org.instancio.test.support.pojo.cyclic.onetomany.DetailRecord;
import org.instancio.test.support.pojo.cyclic.onetomany.MainRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allLongs;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.scope;

@FeatureTag({Feature.CYCLIC, Feature.SCOPE, Feature.SELECTOR})
class ScopeWithCyclicIObjectTest {

    @Test
    void mainRecordClass() {
        final long mainId = 1;
        final long detailId = 2;

        final MainRecord mainRecord = Instancio.of(MainRecord.class)
                .set(allLongs().within(scope(MainRecord.class)), mainId)
                .set(allLongs().within(scope(DetailRecord.class)), detailId)
                .create();

        assertThat(mainRecord.getId()).isEqualTo(mainId);
        assertThat(mainRecord.getDetailRecords()).allSatisfy(detail -> {
            assertThat(detail.getId()).isEqualTo(detailId);
        });
    }

    @Test
    void listNode() {
        final String prev = "prev-val";
        final String next = "next-val";

        final ListNode<String> result = Instancio.of(new TypeToken<ListNode<String>>() {})
                .set(allStrings().within(scope(ListNode.class, "prev")), prev)
                .set(allStrings().within(scope(ListNode.class, "next")), next)
                .create();

        assertThat(result.getValue()).isNotIn(prev, next);
        assertThat(result.getNext().getValue()).isEqualTo(next);
        assertThat(result.getPrev().getValue()).isEqualTo(prev);

        assertThat(result.getNext().getNext()).isNull();
        assertThat(result.getPrev().getPrev()).isNull();
    }
}

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
package org.instancio.test.features.scope;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.cyclic.ClassesWithCrossReferences;
import org.instancio.test.support.pojo.cyclic.ClassesWithCrossReferences.ObjectA;
import org.instancio.test.support.pojo.cyclic.ClassesWithCrossReferences.ObjectB;
import org.instancio.test.support.pojo.cyclic.ClassesWithCrossReferences.ObjectC;
import org.instancio.test.support.pojo.cyclic.ClassesWithCrossReferences.ObjectD;
import org.instancio.test.support.pojo.cyclic.ClassesWithCrossReferences.ObjectE;
import org.instancio.test.support.pojo.cyclic.ClassesWithCrossReferences.ObjectF;
import org.instancio.test.support.pojo.cyclic.ClassesWithCrossReferences.ObjectG;
import org.instancio.test.support.pojo.cyclic.ListNode;
import org.instancio.test.support.pojo.cyclic.onetomany.DetailPojo;
import org.instancio.test.support.pojo.cyclic.onetomany.MainPojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allLongs;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.scope;

@FeatureTag({Feature.CYCLIC, Feature.SCOPE, Feature.SELECTOR})
@ExtendWith(InstancioExtension.class)
class ScopeWithCyclicIObjectTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.MAX_DEPTH, Integer.MAX_VALUE);

    @Test
    void mainPojo() {
        final long mainId = 1;
        final long detailId = 2;

        final MainPojo mainPojo = Instancio.of(MainPojo.class)
                .set(allLongs().within(scope(MainPojo.class)), mainId)
                .set(allLongs().within(scope(DetailPojo.class)), detailId)
                .set(allLongs().within(scope(DetailPojo::getMainPojoId)), mainId)
                .create();

        assertThat(mainPojo.getId()).isEqualTo(mainId);
        assertThat(mainPojo.getDetailPojos()).allSatisfy(detail -> {
            assertThat(detail.getId()).isEqualTo(detailId);
            assertThat(detail.getMainPojoId()).isEqualTo(mainId);
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

    @Test
    void selectorScopeWithCyclicObject() {
        final ClassesWithCrossReferences r = Instancio.of(ClassesWithCrossReferences.class)
                .withMaxDepth(6)
                .set(allStrings().within(scope(ObjectA.class), scope(ObjectB.class)), "B-within-A")
                // B-within-B would form a cycle, therefore, the second B is null and the
                // following line is omitted as it would result in an unused selector error:
                // .set(allStrings().within(scope(ObjectB.class), scope(ObjectB.class)), "B-within-B")
                .set(allStrings().within(scope(ObjectC.class), scope(ObjectB.class)), "B-within-C")
                .set(allStrings().within(scope(ObjectD.class), scope(ObjectB.class)), "B-within-D")
                .set(allStrings().within(scope(ObjectE.class), scope(ObjectB.class)), "B-within-E")
                .set(allStrings().within(scope(ObjectF.class), scope(ObjectB.class)), "B-within-F")
                .set(allStrings().within(scope(ObjectG.class), scope(ObjectB.class)), "B-within-G")
                .create();

        assertThat(r.getObjectA().getObjectB().getB().getValue()).isEqualTo("B-within-A");
        assertThat(r.getObjectA().getObjectC().getObjectB().getB().getValue()).isEqualTo("B-within-C");
        assertThat(r.getObjectA().getObjectD().getObjectB().getB().getValue()).isEqualTo("B-within-D");
        assertThat(r.getObjectA().getObjectE().getObjectB().getB().getValue()).isEqualTo("B-within-E");
        assertThat(r.getObjectA().getObjectF().getObjectB().getB().getValue()).isEqualTo("B-within-F");
        assertThat(r.getObjectA().getObjectG().getObjectB().getB().getValue()).isEqualTo("B-within-G");

        assertThat(r.getObjectB().getObjectA().getObjectB()).isNull(); // cyclic

        assertThat(r.getObjectC().getObjectA().getObjectB().getB().getValue()).isEqualTo("B-within-C");
        assertThat(r.getObjectC().getObjectB().getB().getValue()).isEqualTo("B-within-C");
        assertThat(r.getObjectC().getObjectD().getObjectB().getB().getValue()).isEqualTo("B-within-D");
        assertThat(r.getObjectC().getObjectE().getObjectB().getB().getValue()).isEqualTo("B-within-E");
        assertThat(r.getObjectC().getObjectF().getObjectB().getB().getValue()).isEqualTo("B-within-F");
        assertThat(r.getObjectC().getObjectG().getObjectB().getB().getValue()).isEqualTo("B-within-G");

        assertThat(r.getObjectD().getObjectA().getObjectB().getB().getValue()).isEqualTo("B-within-D");
        assertThat(r.getObjectD().getObjectB().getB().getValue()).isEqualTo("B-within-D");
        assertThat(r.getObjectD().getObjectC().getObjectB().getB().getValue()).isEqualTo("B-within-D");
        assertThat(r.getObjectD().getObjectE().getObjectB().getB().getValue()).isEqualTo("B-within-E");
        assertThat(r.getObjectD().getObjectF().getObjectB().getB().getValue()).isEqualTo("B-within-F");
        assertThat(r.getObjectD().getObjectG().getObjectB().getB().getValue()).isEqualTo("B-within-G");

        assertThat(r.getObjectE().getObjectA().getObjectB().getB().getValue()).isEqualTo("B-within-E");
        assertThat(r.getObjectE().getObjectB().getB().getValue()).isEqualTo("B-within-E");
        assertThat(r.getObjectE().getObjectC().getObjectB().getB().getValue()).isEqualTo("B-within-E");
        assertThat(r.getObjectE().getObjectD().getObjectB().getB().getValue()).isEqualTo("B-within-E");
        assertThat(r.getObjectE().getObjectF().getObjectB().getB().getValue()).isEqualTo("B-within-F");
        assertThat(r.getObjectE().getObjectG().getObjectB().getB().getValue()).isEqualTo("B-within-G");

        assertThat(r.getObjectF().getObjectA().getObjectB().getB().getValue()).isEqualTo("B-within-F");
        assertThat(r.getObjectF().getObjectB().getB().getValue()).isEqualTo("B-within-F");
        assertThat(r.getObjectF().getObjectC().getObjectB().getB().getValue()).isEqualTo("B-within-F");
        assertThat(r.getObjectF().getObjectD().getObjectB().getB().getValue()).isEqualTo("B-within-F");
        assertThat(r.getObjectF().getObjectE().getObjectB().getB().getValue()).isEqualTo("B-within-F");
        assertThat(r.getObjectF().getObjectG().getObjectB().getB().getValue()).isEqualTo("B-within-G");

        assertThat(r.getObjectG().getObjectA().getObjectB().getB().getValue()).isEqualTo("B-within-G");
        assertThat(r.getObjectG().getObjectB().getB().getValue()).isEqualTo("B-within-G");
        assertThat(r.getObjectG().getObjectC().getObjectB().getB().getValue()).isEqualTo("B-within-G");
        assertThat(r.getObjectG().getObjectD().getObjectB().getB().getValue()).isEqualTo("B-within-G");
        assertThat(r.getObjectG().getObjectE().getObjectB().getB().getValue()).isEqualTo("B-within-G");
        assertThat(r.getObjectG().getObjectF().getObjectB().getB().getValue()).isEqualTo("B-within-G");
    }
}

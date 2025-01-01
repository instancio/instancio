/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.test.features.cyclic;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.test.support.pojo.cyclic.onetomany.OneToManyWithCrossReferences.ObjectA;
import org.instancio.test.support.pojo.cyclic.onetomany.OneToManyWithCrossReferences.ObjectB;
import org.instancio.test.support.pojo.cyclic.onetomany.OneToManyWithCrossReferences.ObjectC;
import org.instancio.test.support.pojo.cyclic.onetomany.OneToManyWithCrossReferences.ObjectD;
import org.instancio.test.support.pojo.cyclic.onetomany.OneToManyWithCrossReferences.ObjectE;
import org.instancio.test.support.pojo.cyclic.onetomany.OneToManyWithCrossReferences.ObjectF;
import org.instancio.test.support.pojo.cyclic.onetomany.OneToManyWithCrossReferences.ObjectG;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({Feature.CYCLIC, Feature.MAX_DEPTH})
@ExtendWith(InstancioExtension.class)
class OneToManyWithCrossReferencesTest {

    @Test
    @Timeout(1)
    void defaultMaxDepth() {
        final ObjectA objectA = Instancio.create(ObjectA.class);

        final int defaultMaxDepth = Keys.MAX_DEPTH.defaultValue();
        assertThat(defaultMaxDepth).isEqualTo(8);

        final Object maxDepthObject = objectA // 0
                .getObjectC()  // 1
                .get(0)        // 2
                .getObjectD()  // 3
                .getObjectE()  // 4
                .getObjectF()  // 5
                .getObjectG()  // 6
                .getObjectB(); // 7

        assertThat(maxDepthObject).hasAllNullFieldsOrProperties();

        // Cycles should be terminated with a null
        assertThat(objectA.getObjectB().get(0).getObjectA()).isNull();
        assertThat(objectA.getObjectB().get(0).getObjectC().getObjectB()).isNull();

        assertThat(objectA.getObjectC().get(0).getObjectA()).isNull();
        assertThat(objectA.getObjectC().get(0).getObjectD().getObjectG().getObjectD()).isNull();

        assertThat(objectA.getObjectD().get(0).getObjectA()).isNull();
        assertThat(objectA.getObjectD().get(0).getObjectB().getObjectC().getObjectB()).isNull();
    }

    @Test
    void maxDepthObject() {
        final ObjectA objectA = Instancio.of(ObjectA.class)
                .withMaxDepth(4)
                .create();

        final Object maxDepthObject = objectA // 0
                .getObjectC()  // 1
                .get(0)        // 2
                .getObjectD()  // 3
                .getObjectE(); // 4

        assertThat(maxDepthObject).hasAllNullFieldsOrProperties();
    }

    /**
     * With the original implementation of {@code ignore()}, this test would
     * have timed out because Instancio would try to create the entire
     * node hierarchy even though the nodes were marked as ignored.
     *
     * <p>With the current implementation, ignored nodes are handled
     * by the node factory (no children are created for ignored nodes).
     * As a result, ignoring nodes actually speeds up object generation
     * and this method should no longer time out.
     */
    @Test
    @Timeout(1)
    void withIgnore() {
        final ObjectA objectA = Instancio.of(ObjectA.class)
                .withMaxDepth(Integer.MAX_VALUE)
                .ignore(all(
                        all(ObjectC.class),
                        all(ObjectD.class),
                        all(ObjectE.class),
                        all(ObjectF.class),
                        all(ObjectG.class)))
                .create();

        assertThat(objectA.getObjectB()).isNotEmpty().allSatisfy(
                (ObjectB b) -> {
                    assertThat(b.getObjectA()).isNull();
                    assertThat(b.getObjectC()).isNull();
                    assertThat(b.getObjectD()).isNull();
                    assertThat(b.getObjectE()).isNull();
                    assertThat(b.getObjectF()).isNull();
                    assertThat(b.getObjectG()).isNull();
                });

        assertThat(objectA.getObjectC()).isEmpty();
        assertThat(objectA.getObjectD()).isEmpty();
        assertThat(objectA.getObjectE()).isEmpty();
        assertThat(objectA.getObjectF()).isEmpty();
        assertThat(objectA.getObjectG()).isEmpty();
    }
}

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
package org.instancio.test.features.cyclic;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.cyclic.onetomany.OneToManyWithCrossReferences.ObjectA;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.CYCLIC, Feature.MAX_DEPTH})
@ExtendWith(InstancioExtension.class)
class OneToManyWithCrossReferencesTest {

    @Test
    @Timeout(value = 3)
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
                .getObjectB()  // 7
                .getObjectC(); // 8

        assertThat(maxDepthObject).hasAllNullFieldsOrProperties();

        // Cycles should be terminated with a null
        assertThat(objectA.getObjectB().get(0).getObjectA()).isNull();
        assertThat(objectA.getObjectB().get(0).getObjectC().getObjectB().getObjectC()).isNull();

        assertThat(objectA.getObjectC().get(0).getObjectA()).isNull();
        assertThat(objectA.getObjectC().get(0).getObjectD().getObjectG().getObjectD().getObjectG()).isNull();

        assertThat(objectA.getObjectD().get(0).getObjectA()).isNull();
        assertThat(objectA.getObjectD().get(0).getObjectB().getObjectC().getObjectB().getObjectC()).isNull();
    }

    @Test
    void maxDepthObject() {
        final ObjectA objectA = Instancio.of(ObjectA.class)
                .withSettings(Settings.create().set(Keys.MAX_DEPTH, 4))
                .create();

        final Object maxDepthObject = objectA // 0
                .getObjectC()  // 1
                .get(0)        // 2
                .getObjectD()  // 3
                .getObjectE(); // 4

        assertThat(maxDepthObject).hasAllNullFieldsOrProperties();
    }
}

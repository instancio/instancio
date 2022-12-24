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
package org.instancio.test.java16;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.java16.record.cyclic.NodeRecord;
import org.instancio.test.support.java16.record.cyclic.ParentRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * NOTE: this test fails in IntelliJ, run it using Maven
 */
@FeatureTag(Feature.CYCLIC)
@ExtendWith(InstancioExtension.class)
class CyclicRecordTest {

    @Test
    void cyclicGenericRecord() {
        final NodeRecord<String> result = Instancio.create(new TypeToken<>() {});

        assertThat(result.next()).isNull();
        assertThat(result.prev()).isNull();
        assertThat(result.value()).isNotBlank();
    }

    @Test
    void cyclicParentChild() {
        final ParentRecord parent = Instancio.create(ParentRecord.class);

        assertThat(parent.name()).isNotBlank();
        assertThat(parent.children()).isNotEmpty()
                .allSatisfy(child -> Assertions.assertThat(child.parent()).isNull());
    }
}

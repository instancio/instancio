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
package org.instancio.test.java16;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allInts;

@FeatureTag(Feature.DEPTH_SELECTOR)
@ExtendWith(InstancioExtension.class)
class SelectorDepthRecordTest {

    @Test
    void recordDepth() {
        final Pojo0 result = Instancio.of(Pojo0.class)
                .set(allInts().atDepth(1), 10)
                .set(allInts().atDepth(2), 20)
                .set(allInts().atDepth(3), 30)
                .create();

        assertThat(result.val).isEqualTo(10);
        assertThat(result.pojo1.val).isEqualTo(20);
        assertThat(result.pojo1.pojo2.val).isEqualTo(30);
    }

    private record Pojo0(Integer val, Pojo1 pojo1) {}

    private record Pojo1(Integer val, Pojo2 pojo2) {}

    private record Pojo2(Integer val) {}
}

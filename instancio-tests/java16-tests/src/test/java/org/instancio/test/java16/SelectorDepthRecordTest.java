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
package org.instancio.test.java16;

import org.instancio.Instancio;
import org.instancio.Selector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;

@FeatureTag(Feature.DEPTH_SELECTOR)
@ExtendWith(InstancioExtension.class)
class SelectorDepthRecordTest {

    @Test
    void recordDepth() {
        record Rec2(Integer val) {}
        record Rec1(Integer val, Rec2 rec2) {}
        record Rec0(Integer val, Rec1 rec1) {}

        final Rec0 result = Instancio.of(Rec0.class)
                .set(allInts().atDepth(1), 10)
                .set(allInts().atDepth(2), 20)
                .set(allInts().atDepth(3), 30)
                .create();

        assertThat(result.val).isEqualTo(10);
        assertThat(result.rec1.val).isEqualTo(20);
        assertThat(result.rec1.rec2.val).isEqualTo(30);
    }

    @Test
    void atDepthWithScope() {
        record Id(int value) {}
        record Inner(Id id) {}
        record Mid(Id id, Inner inner) {}
        record Outer(Id id, Mid mid) {}

        final Selector id = all(Id.class);

        final Outer result = Instancio.of(Outer.class)
                .set(allInts().within(id.atDepth(1).toScope()), 100)
                .set(allInts().within(id.atDepth(2).toScope()), 200)
                .create();

        assertThat(result.id.value).isEqualTo(100);
        assertThat(result.mid.id.value).isEqualTo(200);
        assertThat(result.mid.inner.id.value).isEqualTo(200);
    }
}

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
package org.instancio.test.features.maxdepth;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.MAX_DEPTH)
@ExtendWith(InstancioExtension.class)
class MaxDepthCollectionTest {

    private static List<Phone> createWithDepth(final int depth) {
        return Instancio.ofList(Phone.class).size(5)
                .withMaxDepth(depth)
                .create();
    }

    @Test
    void withDepth0() {
        final List<Phone> results = createWithDepth(0);

        assertThat(results).isEmpty();
    }

    @Test
    void withDepth1() {
        final List<Phone> results = createWithDepth(1);

        assertThat(results).isNotEmpty().allSatisfy(phone ->
                assertThat(phone).hasAllNullFieldsOrProperties()
        );
    }

    @Test
    void withDepth2() {
        final List<Phone> results = createWithDepth(2);

        assertThat(results).isNotEmpty().allSatisfy(phone ->
                assertThat(phone).hasNoNullFieldsOrProperties()
        );
    }
}

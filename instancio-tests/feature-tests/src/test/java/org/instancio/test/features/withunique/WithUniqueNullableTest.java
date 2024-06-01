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
package org.instancio.test.features.withunique;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allBooleans;
import static org.instancio.Select.root;

@FeatureTag(Feature.WITH_UNIQUE)
@ExtendWith(InstancioExtension.class)
class WithUniqueNullableTest {

    @RepeatedTest(5)
    void nullableElement() {
        final List<Boolean> result = Instancio.ofList(Boolean.class)
                .generate(root(), gen -> gen.collection().nullableElements().size(3))
                .withUnique(allBooleans())
                .create();

        assertThat(result)
                .hasSize(3)
                .containsExactlyInAnyOrder(true, false, null);
    }
}

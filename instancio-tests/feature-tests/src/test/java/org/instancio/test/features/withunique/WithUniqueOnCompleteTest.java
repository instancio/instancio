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
package org.instancio.test.features.withunique;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allInts;

@FeatureTag({Feature.WITH_UNIQUE, Feature.ON_COMPLETE})
@ExtendWith(InstancioExtension.class)
class WithUniqueOnCompleteTest {

    /**
     * Verify that any duplicates that may have been generated (and rejected)
     * do not trigger the {@code onComplete()} callback.
     */
    @RepeatedTest(5)
    void onCompleteShouldNotBeCalledWithRejectedValues() {
        final List<Integer> callbackValues = new ArrayList<>();

        final List<Integer> results = Instancio.ofList(Integer.class)
                .size(10)
                .generate(allInts(), gen -> gen.ints().range(1, 10))
                .onComplete(allInts(), (Integer val) -> callbackValues.add(val))
                .withUnique(allInts())
                .create();

        assertThat(results)
                .hasSize(10)
                .doesNotHaveDuplicates()
                .containsExactlyElementsOf(callbackValues);
    }
}

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
package org.instancio.test.features.generator.oneof;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;

@FeatureTag({Feature.GENERATE, Feature.ONE_OF_COLLECTION_GENERATOR})
@ExtendWith(InstancioExtension.class)
class OneOfCollectionGeneratorTest {

    @Test
    void oneOfSingleChoice() {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.oneOf(Collections.singleton("one")))
                .create();

        assertThat(result).isEqualTo("one");
    }

    @Test
    void oneOfConsidersAllChoices() {
        final Set<String> results = new HashSet<>();
        final List<String> choices = Arrays.asList("one", "two", "three");
        for (int i = 0; i < 30; i++) {
            results.add(Instancio.of(String.class)
                    .generate(allStrings(), gen -> gen.oneOf(choices))
                    .create());
        }
        assertThat(results).containsAll(choices);
    }
}

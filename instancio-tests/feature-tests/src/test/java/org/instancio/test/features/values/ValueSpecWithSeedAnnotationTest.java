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
package org.instancio.test.features.values;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.instancio.settings.Keys;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.VALUE_SPEC, Feature.WITH_SEED_ANNOTATION})
@ExtendWith(InstancioExtension.class)
class ValueSpecWithSeedAnnotationTest {

    private static final long SEED = -1;
    private static final Set<String> results = new HashSet<>();

    @Seed(SEED)
    @RepeatedTest(10)
    void value1() {
        results.add(Instancio.gen().string().get());
        assertThat(results).hasSize(1);
    }

    @Seed(SEED)
    @RepeatedTest(10)
    void value2() {
        results.add(Instancio.gen().string().get());
        assertThat(results).hasSize(1);
    }

    @Seed(SEED)
    @Test
    void produceDifferentValues() {
        final String s1 = Instancio.create(String.class);
        final String s2 = Instancio.gen().string().get();
        assertThat(s1).isNotEqualTo(s2);
    }

    @Seed(SEED)
    @Test
    void withSetting_shouldTakePrecedenceOverSeedAnnotation() {
        final String s = Instancio.gen()
                .withSetting(Keys.SEED, -999L)
                .string()
                .get();

        assertThat(s).isNotIn(results);
    }
}

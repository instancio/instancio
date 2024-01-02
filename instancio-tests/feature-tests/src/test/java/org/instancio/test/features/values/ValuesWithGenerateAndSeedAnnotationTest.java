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

import org.instancio.Gen;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;

@FeatureTag({Feature.GENERATE, Feature.VALUE_SPEC, Feature.WITH_SEED_ANNOTATION})
@ExtendWith(InstancioExtension.class)
class ValuesWithGenerateAndSeedAnnotationTest {

    private static final long SEED = -1;
    private static final Set<String> results = new HashSet<>();

    @Seed(SEED)
    @RepeatedTest(10)
    void valueSpecWithGenerate() {
        final StringFields stringFields = Instancio.of(StringFields.class)
                .generate(allStrings(), Gen.string().alphaNumeric())
                .create();

        results.add(stringFields.getOne());
        results.add(stringFields.getTwo());
        results.add(stringFields.getThree());
        results.add(stringFields.getFour());

        assertThat(results).hasSize(4);
    }
}

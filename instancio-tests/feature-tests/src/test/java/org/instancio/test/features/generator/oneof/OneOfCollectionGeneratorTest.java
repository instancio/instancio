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
package org.instancio.test.features.generator.oneof;

import org.instancio.Instancio;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.OptionalString;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;

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
    void oneOfWithNullChoice() {
        final Stream<String> results = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.oneOf(Arrays.asList("one", null)))
                .stream()
                .limit(Constants.SAMPLE_SIZE_DD);

        assertThat(results).containsOnly("one", null);
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

    @Test
    void oneOfNullable() {
        final Set<String> choices = CollectionUtils.asSet("one");

        final Stream<String> results = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.oneOf(choices).nullable())
                .stream()
                .limit(Constants.SAMPLE_SIZE_DD);

        assertThat(results).containsOnly("one", null);
    }

    @Test
    void oneOfAs() {
        final Set<String> choices = Collections.singleton("one");

        final OptionalString result = Instancio.of(OptionalString.class)
                .generate(field(OptionalString::getOptional), gen -> gen.oneOf(choices).as(Optional::of))
                .create();

        assertThat(result.getOptional()).contains("one");
    }

    @Test
    void oneOfAllowRandom() {
        final Set<String> results = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen
                        .oneOf(Collections.singleton("one"))
                        .orRandom())
                .stream()
                .limit(Constants.SAMPLE_SIZE_DD)
                .collect(Collectors.toSet());

        assertThat(results)
                .doesNotContainNull()
                .hasSizeGreaterThan(1);
    }

    @Test
    void oneOfAllowNullableRandom() {
        final Set<String> results = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen
                        .oneOf(Collections.singleton("one"))
                        .orRandom().nullable())
                .stream()
                .limit(Constants.SAMPLE_SIZE_DD)
                .collect(Collectors.toSet());

        assertThat(results)
                .contains(null, "one")
                .hasSizeGreaterThan(2);
    }
}

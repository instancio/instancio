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
package org.instancio.test.features.generator.enumof;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.Select.root;

@NonDeterministicTag
@FeatureTag({Feature.GENERATE, Feature.ENUM_GENERATOR})
@ExtendWith(InstancioExtension.class)
class EnumGeneratorTest {

    @Test
    @DisplayName("Should generate a nullable value, excluding all values except one")
    void enumOfWithExcludesAndNullable() {
        final Set<Gender> results = IntStream.range(0, 100)
                .mapToObj(i -> Instancio.of(Person.class)
                        .generate(field("gender"), gen -> gen.enumOf(Gender.class)
                                .excluding(Gender.MALE, Gender.FEMALE)
                                .nullable())
                        .create().getGender())
                .collect(Collectors.toSet());

        assertThat(results).containsOnly(Gender.OTHER, null);
    }

    @Test
    void nullable() {
        final Stream<Gender> results = Instancio.of(Gender.class)
                .generate(root(), gen -> gen.enumOf(Gender.class).nullable())
                .stream()
                .limit(Constants.SAMPLE_SIZE_DD);

        assertThat(results)
                .containsNull()
                .anyMatch(Objects::nonNull);
    }
}

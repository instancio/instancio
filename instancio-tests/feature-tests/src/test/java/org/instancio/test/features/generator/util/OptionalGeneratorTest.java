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
package org.instancio.test.features.generator.util;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.test.support.pojo.empty.EmptyEnum;
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;

@FeatureTag(Feature.GENERATOR)
class OptionalGeneratorTest {

    @Test
    void optional() {
        final Optional<String> result = Instancio.create(new TypeToken<Optional<String>>() {});
        assertThat(result).isNotEmpty();
        assertThat(result.get()).isNotBlank();
    }

    @Test
    void emptyOptional() {
        final Optional<EmptyEnum> result = Instancio.create(new TypeToken<Optional<EmptyEnum>>() {});
        assertThat(result).isEmpty();
    }

    @Test
    void optionalWithGenericType() {
        final Optional<Pair<Long, String>> result = Instancio.create(new TypeToken<Optional<Pair<Long, String>>>() {});
        assertThat(result)
                .isNotEmpty()
                .get()
                .satisfies(pair -> assertThatObject(pair).hasNoNullFieldsOrProperties());
    }
}

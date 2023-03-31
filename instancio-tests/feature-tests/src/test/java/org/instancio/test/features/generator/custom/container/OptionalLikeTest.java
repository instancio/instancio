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
package org.instancio.test.features.generator.custom.container;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.TypeToken;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.internal.generator.InternalContainerHint;
import org.instancio.test.support.pojo.containers.OptionalLike;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag({Feature.GENERATOR, Feature.CONTAINER_GENERATOR})
class OptionalLikeTest {

    private static <T> Generator<OptionalLike<T>> generator() {
        return new Generator<OptionalLike<T>>() {
            @Override
            public OptionalLike<T> generate(final Random random) {
                return null; // delegates to the engine via createFunction hint
            }

            @Override
            public Hints hints() {
                return Hints.builder()
                        .with(InternalContainerHint.builder()
                                .generateEntries(1)
                                .createFunction(args -> OptionalLike.of(args[0]))
                                .build())
                        .build();
            }
        };
    }

    @Test
    void optionalLike() {
        final OptionalLike<Phone> result = Instancio.of(new TypeToken<OptionalLike<Phone>>() {})
                .supply(all(OptionalLike.class), generator())
                .create();

        final Phone phone = result.get();

        assertThat(phone).isNotNull();
        assertThat(phone.getCountryCode()).isNotBlank();
        assertThat(phone.getNumber()).isNotBlank();
    }
}

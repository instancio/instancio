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
package org.instancio.test.features.generator.custom.container;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.TypeToken;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.internal.generator.InternalContainerHint;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.containers.BuildableList;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;

/**
 * Tests generating a "container" that is built using static factory method.
 * See {@link BuildableList} javadoc.
 */
@FeatureTag({Feature.GENERATOR, Feature.CONTAINER_GENERATOR})
@ExtendWith(InstancioExtension.class)
class BuildableListTest {

    private static <T> Generator<BuildableList.Builder<T>> generator(final int numEntries) {
        return new Generator<BuildableList.Builder<T>>() {

            @Override
            public BuildableList.Builder<T> generate(final Random random) {
                return BuildableList.builder();
            }

            @Override
            @SuppressWarnings("unchecked")
            public Hints hints() {
                return Hints.builder()
                        .with(InternalContainerHint.builder()
                                .generateEntries(numEntries)
                                .addFunction((BuildableList.Builder<T> builder, Object... args) ->
                                        builder.add((T) args[0]))
                                .buildFunction((BuildableList.Builder<T> builder) ->
                                        builder.build())
                                .build())
                        .build();
            }
        };
    }

    @ValueSource(ints = {0, 1, 2, 3})
    @ParameterizedTest
    void generateContainer(final int numEntries) {
        final BuildableList<String> result = Instancio.of(new TypeToken<BuildableList<String>>() {})
                .supply(all(BuildableList.class), generator(numEntries))
                .create();

        assertThat(result.getElements())
                .hasSize(numEntries)
                .doesNotContainNull();

        result.assertOriginalListNotOverwritten();
    }

    @Test
    void withNullable() {
        final int numEntries = 500;
        final BuildableList<String> result = Instancio.of(new TypeToken<BuildableList<String>>() {})
                .withNullable(allStrings())
                .supply(all(BuildableList.class), generator(numEntries))
                .create();

        assertThat(result.getElements())
                .hasSize(numEntries)
                .containsNull();
    }

    @Test
    void ignore() {
        final int numEntries = 3;
        final BuildableList<String> result = Instancio.of(new TypeToken<BuildableList<String>>() {})
                .ignore(allStrings())
                .supply(all(BuildableList.class), generator(numEntries))
                .create();

        assertThat(result.getElements())
                .hasSize(numEntries)
                .containsOnlyNulls();
    }
}

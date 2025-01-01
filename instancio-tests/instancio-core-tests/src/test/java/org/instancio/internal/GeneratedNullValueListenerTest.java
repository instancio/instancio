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
package org.instancio.internal;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generation.GenerationListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;

class GeneratedNullValueListenerTest {

    @Test
    void createShouldReturnNoopClass_whenAllSelectorMapsAreEmpty() {
        final ModelContext ctx = ModelContext.builder(String.class).build();
        final GenerationListener result = GeneratedNullValueListener.create(ctx);

        assertThat(result).isSameAs(GenerationListener.NOOP_LISTENER);
    }

    @Test
    void createShouldReturnNoopClass_whenLenientModeIsEnabled() {
        final ModelContext ctx = ModelContext.builder(String.class)
                .withGenerator(allStrings(), random -> null)
                .lenient()
                .build();

        final GenerationListener result = GeneratedNullValueListener.create(ctx);

        assertThat(result).isSameAs(GenerationListener.NOOP_LISTENER);
    }

    /**
     * Should <b>not</b> return no-op listener
     * if at least one non-empty selector map is present.
     */
    @MethodSource("args")
    @ParameterizedTest
    void createShouldReturnGeneratedNullValueListener_whenSelectorIsPresent(final ModelContext ctx) {
        final GenerationListener result = GeneratedNullValueListener.create(ctx);

        assertThat(result).isExactlyInstanceOf(GeneratedNullValueListener.class);
    }

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(ModelContext.builder(String.class)
                        .withAssignments(Assign.valueOf(String.class).to(all(String.class)))
                        .build()),

                Arguments.of(ModelContext.builder(String.class)
                        .withGenerator(allStrings(), random -> null)
                        .build()),

                Arguments.of(ModelContext.builder(String.class)
                        .withOnCompleteCallback(allStrings(), System.out::println)
                        .build()),

                Arguments.of(ModelContext.builder(String.class)
                        .setModel(allStrings(), Instancio.of(String.class).toModel())
                        .build()),

                Arguments.of(ModelContext.builder(String.class)
                        .withIgnored(allStrings())
                        .build()),

                Arguments.of(ModelContext.builder(String.class)
                        .withNullable(allStrings())
                        .build()),

                Arguments.of(ModelContext.builder(String.class)
                        .withSubtype(allStrings(), String.class)
                        .build())
        );
    }
}

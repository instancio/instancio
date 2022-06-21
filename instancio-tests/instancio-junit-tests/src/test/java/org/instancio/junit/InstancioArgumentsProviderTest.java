/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.junit;

import org.instancio.Random;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InstancioArgumentsProviderTest {

    private final InstancioArgumentsProvider provider = new InstancioArgumentsProvider();

    @InstancioSource({String.class, Integer.class})
    private void dummy() {
    }

    @Test
    void provideArguments() throws Exception {
        final Method method = InstancioArgumentsProviderTest.class.getDeclaredMethod("dummy");
        final InstancioSource instancioSource = method.getAnnotation(InstancioSource.class);
        final ExtensionContext mockContext = mock(ExtensionContext.class);
        when(mockContext.getTestMethod()).thenReturn(Optional.of(method));

        // Methods under test
        provider.accept(instancioSource);
        final Stream<? extends Arguments> stream = provider.provideArguments(mockContext);

        assertThat(stream.collect(toList()))
                .hasSize(1)
                .extracting(Arguments::get)
                .allSatisfy(params -> {
                    assertThat(params).hasSize(2);
                    assertThat(params[0]).isInstanceOf(String.class);
                    assertThat(params[1]).isInstanceOf(Integer.class);
                });
    }

    @MethodSource("types")
    @ParameterizedTest
    void createObjectsGroupingByType(final Class<?>[] types) {
        final Random random = new DefaultRandom();
        final Settings settings = Settings.create();
        final Object[] results = InstancioArgumentsProvider.createObjectsGroupingByType(types, random, settings);

        assertThat(results).hasExactlyElementsOfTypes(types);
    }

    private static Stream<Arguments> types() {
        return Stream.of(
                args(),
                args(String.class),
                args(String.class, String.class),
                args(String.class, Integer.class, String.class),
                args(String.class, Integer.class, Integer.class, String.class, Integer.class, Long.class)
        );
    }

    private static Arguments args(final Class<?>... types) {
        return Arguments.of((Object) types);
    }
}

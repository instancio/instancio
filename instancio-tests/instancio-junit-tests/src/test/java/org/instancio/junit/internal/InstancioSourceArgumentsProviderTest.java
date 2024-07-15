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
package org.instancio.junit.internal;

import org.instancio.Random;
import org.instancio.junit.InstancioSource;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InstancioSourceArgumentsProviderTest {

    private final InstancioSourceArgumentsProvider provider = new InstancioSourceArgumentsProvider();

    @InstancioSource(samples = 1)
    private void dummy() {
    }

    @Test
    void typesProvidedByTheAnnotationShouldBeIgnored() throws Exception {
        final Method method = InstancioSourceArgumentsProviderTest.class.getDeclaredMethod("dummy");
        final InstancioSource instancioSource = method.getAnnotation(InstancioSource.class);
        final ExtensionContext mockContext = mock(ExtensionContext.class);
        when(mockContext.getTestMethod()).thenReturn(Optional.of(method));
        when(mockContext.getRequiredTestMethod()).thenReturn(method);

        // Methods under test
        provider.accept(instancioSource);
        final Stream<? extends Arguments> stream = provider.provideArguments(mockContext);

        assertThat(stream).doesNotContainNull().hasSize(1).allSatisfy(arguments -> assertThat(arguments.get()).isEmpty());
    }

    @NonDeterministicTag("Small chance of duplicate values being generated")
    @MethodSource("types")
    @ParameterizedTest
    void createObjects(final Class<?>[] types) {
        final Random random = new DefaultRandom();
        final Settings settings = Settings.create()
                .set(Keys.STRING_MIN_LENGTH, 20)
                .set(Keys.INTEGER_MIN, Integer.MIN_VALUE)
                .set(Keys.INTEGER_MAX, Integer.MAX_VALUE)
                .set(Keys.LONG_MIN, Long.MIN_VALUE)
                .set(Keys.LONG_MAX, Long.MAX_VALUE);

        final Object[] results = InstancioSourceArgumentsProvider.createObjects(types, random, settings);

        assertThat(results).hasExactlyElementsOfTypes(types);
        assertThat(new HashSet<>(Arrays.asList(results)))
                .as("Distinct parameter values should be generated")
                .hasSize(types.length);
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

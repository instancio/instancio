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
package org.instancio.test.java21;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.internal.util.Constants;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SequencedCollection;
import java.util.SequencedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@ExtendWith(InstancioExtension.class)
class Java21SupportedCollectionsTest {

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(new TypeToken<SequencedCollection<UUID>>() {}, ArrayList.class),
                Arguments.of(new TypeToken<SequencedSet<UUID>>() {}, TreeSet.class)
        );
    }

    @ParameterizedTest
    @MethodSource("args")
    <C extends Collection<UUID>> void verify(final TypeToken<C> type, final Class<?> expectedSubtype) {
        verifyCreate(type, expectedSubtype);
        verifyCreateWithSize(type, expectedSubtype);
    }

    private static <C extends Collection<UUID>> void verifyCreate(final TypeToken<C> type, final Class<?> expectedSubtype) {
        final Collection<UUID> result = Instancio.create(type);

        assertThat(result)
                .as("Failed type: %s, expected subtype: %s", type.get(), expectedSubtype)
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .isInstanceOf(expectedSubtype);
    }

    private static <C extends Collection<UUID>> void verifyCreateWithSize(final TypeToken<C> type, final Class<?> expectedSubtype) {
        final int size = 5;
        final UUID expected = Instancio.create(UUID.class);
        final Collection<UUID> result = Instancio.of(type)
                .generate(root(), gen -> gen.collection().size(size).with(expected))
                .create();

        assertThat(result)
                .as("Failed type: %s, expected subtype: %s", type.get(), expectedSubtype)
                .hasSize(size + 1) // plus expected element
                .contains(expected)
                .isInstanceOf(expectedSubtype);
    }
}

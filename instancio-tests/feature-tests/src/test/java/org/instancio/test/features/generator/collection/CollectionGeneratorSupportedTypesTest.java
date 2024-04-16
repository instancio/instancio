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
package org.instancio.test.features.generator.collection;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TransferQueue;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag(Feature.COLLECTION_GENERATOR_SUBTYPE)
@ExtendWith(InstancioExtension.class)
class CollectionGeneratorSupportedTypesTest {

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(new TypeToken<ArrayDeque<UUID>>() {}, ArrayDeque.class),
                Arguments.of(new TypeToken<BlockingDeque<UUID>>() {}, LinkedBlockingDeque.class),
                Arguments.of(new TypeToken<BlockingQueue<UUID>>() {}, LinkedBlockingQueue.class),
                Arguments.of(new TypeToken<Collection<UUID>>() {}, ArrayList.class),
                Arguments.of(new TypeToken<ConcurrentLinkedQueue<UUID>>() {}, ConcurrentLinkedQueue.class),
                Arguments.of(new TypeToken<CopyOnWriteArrayList<UUID>>() {}, CopyOnWriteArrayList.class),
                Arguments.of(new TypeToken<CopyOnWriteArraySet<UUID>>() {}, CopyOnWriteArraySet.class),
                Arguments.of(new TypeToken<Deque<UUID>>() {}, ArrayDeque.class),
                Arguments.of(new TypeToken<List<UUID>>() {}, ArrayList.class),
                Arguments.of(new TypeToken<NavigableSet<UUID>>() {}, TreeSet.class),
                Arguments.of(new TypeToken<PriorityBlockingQueue<UUID>>() {}, PriorityBlockingQueue.class),
                Arguments.of(new TypeToken<Set<UUID>>() {}, HashSet.class),
                Arguments.of(new TypeToken<SortedSet<UUID>>() {}, TreeSet.class),
                Arguments.of(new TypeToken<Stack<UUID>>() {}, Stack.class),
                Arguments.of(new TypeToken<TransferQueue<UUID>>() {}, LinkedTransferQueue.class),
                Arguments.of(new TypeToken<Queue<UUID>>() {}, ArrayDeque.class)
        );
    }

    @ParameterizedTest
    @MethodSource("args")
    <C extends Collection<UUID>> void verify(final TypeToken<C> type, final Class<?> expectedSubtype) {
        final int size = 5;
        final UUID expected = Instancio.create(UUID.class);
        final Collection<UUID> result = Instancio.of(type)
                .generate(root(), gen -> gen.collection().size(size).with(expected))
                .create();

        assertThat(result)
                .as("Failed type: %s, expected subtype: %s", type.get(), expectedSubtype)
                .hasSize(size + 1)
                .contains(expected)
                .isExactlyInstanceOf(expectedSubtype);
    }
}

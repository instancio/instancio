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
package org.instancio.internal.nodes.resolvers;

import org.instancio.internal.nodes.NodeKind;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

class NodeKindResolverTest {

    @ValueSource(classes = {int[].class, String[].class, List[].class})
    @ParameterizedTest
    void array(Class<?> klass) {
        assertThat(new NodeKindArrayResolver().resolve(klass)).contains(NodeKind.ARRAY);
    }

    @Test
    void array() {
        assertThat(new NodeKindArrayResolver().resolve(String.class)).isEmpty();
    }

    @ValueSource(classes = {Collection.class, List.class, ArrayList.class, Set.class})
    @ParameterizedTest
    void collection(Class<?> klass) {
        assertThat(new NodeKindCollectionResolver().resolve(klass)).contains(NodeKind.COLLECTION);
    }


    @Test
    void collection() {
        assertThat(new NodeKindCollectionResolver().resolve(String.class)).isEmpty();
    }

    @ValueSource(classes = {Map.class, HashMap.class, TreeMap.class})
    @ParameterizedTest
    void map(Class<?> klass) {
        assertThat(new NodeKindMapResolver().resolve(klass)).contains(NodeKind.MAP);
    }


    @Test
    void map() {
        assertThat(new NodeKindMapResolver().resolve(String.class)).isEmpty();
    }

    @ValueSource(classes = {EnumSet.class, Optional.class})
    @ParameterizedTest
    void optional(Class<?> klass) {
        assertThat(new NodeKindContainerResolver(Collections.emptyList()).resolve(klass))
                .contains(NodeKind.CONTAINER);
    }
}

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
package org.instancio.internal.generator;

import org.instancio.internal.generator.util.CollectionGenerator;
import org.instancio.internal.generator.util.MapGenerator;
import org.instancio.internal.util.IgnoreJRERequirement;

import java.util.*;

/**
 * Support for generating Java 21 sequenced collections.
 */
@IgnoreJRERequirement
public final class Java21GeneratorResolvers {

    private Java21GeneratorResolvers() {
        // non-instantiable
    }

    public static void putSubtypes(final Map<Class<?>, Class<?>> map) {
        map.put(SequencedSet.class, TreeSet.class);
        map.put(SequencedMap.class, TreeMap.class);
    }

    public static void putGenerators(final Map<Class<?>, Class<?>> map) {
        map.put(SequencedCollection.class, CollectionGenerator.class);
        map.put(SequencedSet.class, CollectionGenerator.class);
        map.put(SequencedMap.class, MapGenerator.class);
    }
}

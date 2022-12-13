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
package org.instancio.guava;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Table;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.util.CollectionGenerator;
import org.instancio.internal.generator.util.MapGenerator;
import org.instancio.spi.GeneratorProvider;

import java.util.HashMap;
import java.util.Map;

public class GuavaGeneratorProvider implements GeneratorProvider {

    @Override
    public Map<Class<?>, Generator<?>> getGenerators(final GeneratorContext context) {
        final Map<Class<?>, Generator<?>> generators = new HashMap<>();
        generators.put(ImmutableList.class, new CollectionGenerator<>(context));
        generators.put(ImmutableMap.class, new MapGenerator<>(context));
        generators.put(Table.class, new GuavaTableGenerator<>());
        generators.put(ListMultimap.class, new GuavaListMultimapGenerator<>());
        generators.put(ImmutableListMultimap.class, new GuavaListMultimapGenerator<>());

        return generators;
    }
}

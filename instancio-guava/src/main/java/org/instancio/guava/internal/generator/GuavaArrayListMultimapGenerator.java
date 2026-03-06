/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.guava.internal.generator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.instancio.Random;
import org.instancio.documentation.Initializer;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.guava.generator.specs.MultimapGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.InternalContainerHint;
import org.instancio.internal.util.Constants;
import org.instancio.internal.util.NumberUtils;

public class GuavaArrayListMultimapGenerator<K, V>
        implements Generator<Multimap<K, V>>, MultimapGeneratorSpec<K, V> {

    private GeneratorContext context;
    private int minSize = Constants.MIN_SIZE;
    private int maxSize = Constants.MAX_SIZE;

    @Initializer
    @Override
    public void init(final GeneratorContext context) {
        this.context = context;
    }

    @Override
    public MultimapGeneratorSpec<K, V> size(final int size) {
        this.minSize = ApiValidator.validateSize(size);
        this.maxSize = size;
        return this;
    }

    @Override
    public MultimapGeneratorSpec<K, V> minSize(final int size) {
        this.minSize = ApiValidator.validateSize(size);
        this.maxSize = NumberUtils.calculateNewMaxSize(maxSize, minSize);
        return this;
    }

    @Override
    public MultimapGeneratorSpec<K, V> maxSize(final int size) {
        this.maxSize = ApiValidator.validateSize(size);
        this.minSize = NumberUtils.calculateNewMinSize(minSize, maxSize);
        return this;
    }

    @Override
    public Multimap<K, V> generate(final Random random) {
        return ArrayListMultimap.create();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Hints hints() {
        final int generateEntries = context.random().intRange(minSize, maxSize);

        return Hints.builder()
                .with(InternalContainerHint.builder()
                        .generateEntries(generateEntries)
                        .addFunction((Multimap<K, V> map, Object... args) ->
                                map.put(
                                        (K) args[0],
                                        (V) args[1]))
                        .build())
                .build();
    }
}

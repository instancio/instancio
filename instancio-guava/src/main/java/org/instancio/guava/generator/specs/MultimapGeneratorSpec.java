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
package org.instancio.guava.generator.specs;

import com.google.common.collect.Multimap;
import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.specs.SizeGeneratorSpec;

/**
 * Generator spec for {@link Multimap Multimaps}.
 *
 * @param <K> the type of the key
 * @param <V> the type of the mapped values
 * @since 2.13.0
 */
@ExperimentalApi
public interface MultimapGeneratorSpec<K, V> extends SizeGeneratorSpec<Multimap<K, V>> {

    @Override
    MultimapGeneratorSpec<K, V> size(int size);

    @Override
    MultimapGeneratorSpec<K, V> minSize(int size);

    @Override
    MultimapGeneratorSpec<K, V> maxSize(int size);
}

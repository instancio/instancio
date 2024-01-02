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
package org.instancio.guava.generator.specs;

import com.google.common.collect.Table;
import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.specs.SizeGeneratorSpec;

/**
 * Generator spec for {@link Table Tables}.
 *
 * @param <R> the type of the table row keys
 * @param <C> the type of the table column keys
 * @param <V> the type of the mapped values
 * @since 2.13.0
 */
@ExperimentalApi
public interface TableGeneratorSpec<R, C, V> extends SizeGeneratorSpec<Table<R, C, V>> {

    @Override
    TableGeneratorSpec<R, C, V> size(int size);

    @Override
    TableGeneratorSpec<R, C, V> minSize(int size);

    @Override
    TableGeneratorSpec<R, C, V> maxSize(int size);
}

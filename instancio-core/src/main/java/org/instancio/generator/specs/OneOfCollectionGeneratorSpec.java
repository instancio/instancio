/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.generator.specs;

import org.instancio.generator.GeneratorSpec;

import java.util.Collection;

/**
 * Generator spec for selecting a random value from collection.
 *
 * @param <T> type of value
 * @since 1.0.1
 */
public interface OneOfCollectionGeneratorSpec<T> extends GeneratorSpec<T> {

    /**
     * Selects a random value from the given choices.
     *
     * @param values from which a random value will be selected
     * @return completed spec with no further methods
     * @since 1.0.1
     */
    GeneratorSpec<T> oneOf(Collection<T> values);
}

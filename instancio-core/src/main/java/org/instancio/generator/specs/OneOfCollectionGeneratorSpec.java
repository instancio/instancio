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
package org.instancio.generator.specs;

import java.util.Collection;

/**
 * Generator spec for selecting a random value from collection.
 *
 * @param <T> type of value
 * @since 1.0.1
 */
public interface OneOfCollectionGeneratorSpec<T> extends
        AsGeneratorSpec<T>,
        NullableGeneratorSpec<T> {

    /**
     * Selects a random value from the given choices.
     *
     * @param values from which a random value will be selected
     * @return spec builder
     * @since 1.0.1
     */
    OneOfCollectionGeneratorSpec<T> oneOf(Collection<T> values);

    /**
     * {@inheritDoc}
     */
    @Override
    OneOfCollectionGeneratorSpec<T> nullable();

    /**
     * Allows the generator to produce either one of the preset values defined
     * via {@link #oneOf(Collection)} or a random value of the target type.
     *
     * @return spec builder
     * @since 5.4.0
     */
    NullableGeneratorSpec<T> orRandom();

}

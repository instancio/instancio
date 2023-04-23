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

import org.instancio.Model;
import org.instancio.generator.ValueSpec;
import org.instancio.internal.ApiValidator;

/**
 * Spec for generating enums.
 *
 * @param <E> enum type
 * @since 2.12.0
 */
public interface EnumSpec<E extends Enum<E>> extends ValueSpec<E>, EnumGeneratorSpec<E> {

    /**
     * {@inheritDoc}
     *
     * @since 2.12.0
     */
    @SuppressWarnings("unchecked")
    @Override
    EnumSpec<E> excluding(E... values);

    /**
     * {@inheritDoc}
     *
     * @since 2.12.0
     */
    @Override
    EnumSpec<E> nullable();

    /**
     * <b>{@code toModel()} is not supported by this spec.</b>
     *
     * <p>{@inheritDoc}
     *
     * @since 2.12.0
     */
    @Override
    default Model<E> toModel() {
        return ApiValidator.valueSpecDoesNotSupportToModel("enumOf()");
    }
}

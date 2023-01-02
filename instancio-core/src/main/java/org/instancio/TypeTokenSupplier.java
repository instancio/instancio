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
package org.instancio;

import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * A supplier that provides {@link Type} information.
 *
 * @param <T> type being supplied
 * @since 1.0.1
 */
// <T> is required to be present, even though not used directly here.
@SuppressWarnings("unused")
@FunctionalInterface
public interface TypeTokenSupplier<T> extends Supplier<Type> {

    /**
     * Returns the type to be created.
     *
     * @return type to be created
     * @since 1.0.1
     */
    @Override
    Type get();
}
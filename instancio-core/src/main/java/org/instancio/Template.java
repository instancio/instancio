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
package org.instancio;

import org.instancio.documentation.ExperimentalApi;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A subset of the builder API that can be specified as a template.
 *
 * @since 6.2.0
 */
@ExperimentalApi
public interface Template {

    /**
     * @see BaseApi#withNullable(TargetSelector)
     */
    Template withNullable(TargetSelector selector);

    /**
     * @see BaseApi#ignore(TargetSelector)
     */
    Template ignore(TargetSelector selector);

    /**
     * @see BaseApi#set(TargetSelector, Object)
     */
    <V extends @Nullable Object> Template set(TargetSelector selector, V value);

    /**
     * @see BaseApi#supply(TargetSelector, Supplier)
     */
    <V extends @Nullable Object> Template supply(TargetSelector selector, Supplier<V> supplier);

    /**
     * @see BaseApi#generate(TargetSelector, GeneratorSpecProvider)
     */
    <V extends @Nullable Object> Template generate(TargetSelector selector, GeneratorSpecProvider<V> gen);
}

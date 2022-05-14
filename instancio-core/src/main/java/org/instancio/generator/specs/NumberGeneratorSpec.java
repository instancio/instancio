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
package org.instancio.generator.specs;

import org.instancio.generator.GeneratorSpec;

/**
 * Generator spec for core numeric types.
 *
 * @param <T> type of number
 */
public interface NumberGeneratorSpec<T extends Number> extends GeneratorSpec<T> {

    /**
     * Lower bound for the random number generator.
     *
     * @param min lower bound (inclusive)
     * @return spec builder
     */
    NumberGeneratorSpec<T> min(T min);

    /**
     * Upper bound for the random number generator.
     *
     * @param max upper bound (inclusive)
     * @return spec builder
     */
    NumberGeneratorSpec<T> max(T max);

    /**
     * Range for the random number generator
     *
     * @param min lower bound (inclusive)
     * @param max upper bound (inclusive)
     * @return spec builder
     */
    NumberGeneratorSpec<T> range(T min, T max);

    /**
     * Specifies the generated value can be {@code null}.
     *
     * @return spec builder
     */
    NumberGeneratorSpec<T> nullable();

}

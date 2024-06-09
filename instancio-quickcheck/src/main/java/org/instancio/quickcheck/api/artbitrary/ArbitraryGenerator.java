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
package org.instancio.quickcheck.api.artbitrary;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.quickcheck.api.Property;

/**
 * Custom sample generator for a property under the test. In general, the generator
 * is unbounded but the number of samples to generate is specified by the {@link Property}
 * annotation.
 *
 * @since 3.6.0
 * @deprecated the {@code instancio-quickcheck} module is deprecated
 *             and will be removed in version 5.
 */
@ExperimentalApi
@Deprecated
public interface ArbitraryGenerator<T> {
    /**
     * Returns next generated value
     *
     * @return next generated value
     * @since 3.6.0
     */
    T generate();
}

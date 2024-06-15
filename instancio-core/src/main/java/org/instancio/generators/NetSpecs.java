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
package org.instancio.generators;

import org.instancio.generator.specs.EmailSpec;
import org.instancio.generator.specs.Ip4Spec;
import org.instancio.generator.specs.URISpec;
import org.instancio.generator.specs.URLSpec;

/**
 * Provides generators for {@code java.net} classes.
 *
 * @since 5.0.0
 */
public interface NetSpecs extends NetGenerators {

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    EmailSpec email();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    Ip4Spec ip4();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    URISpec uri();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    URLSpec url();
}

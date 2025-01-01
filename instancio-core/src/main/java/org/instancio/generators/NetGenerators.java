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
package org.instancio.generators;

import org.instancio.generator.specs.EmailGeneratorSpec;
import org.instancio.generator.specs.Ip4GeneratorSpec;
import org.instancio.generator.specs.URIGeneratorSpec;
import org.instancio.generator.specs.URLGeneratorSpec;

import java.net.URI;
import java.net.URL;

/**
 * Contains built-in generators for {@code java.net} classes.
 *
 * @since 2.3.0
 */
public interface NetGenerators {

    /**
     * Customises generated {@link URI} objects.
     *
     * @return generator spec
     * @since 2.3.0
     */
    URIGeneratorSpec uri();

    /**
     * Customises generated email addresses.
     *
     * @return generator spec
     * @since 2.11.0
     */
    EmailGeneratorSpec email();

    /**
     * Generates IPv4 address.
     *
     * @return generator spec
     * @since 2.12.0
     */
    Ip4GeneratorSpec ip4();

    /**
     * Customises generated {@link URL} objects.
     *
     * @return generator spec
     * @since 2.3.0
     */
    URLGeneratorSpec url();
}

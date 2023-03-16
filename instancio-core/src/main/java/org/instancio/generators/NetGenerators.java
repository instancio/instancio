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
package org.instancio.generators;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.EmailAsGeneratorSpec;
import org.instancio.generator.specs.Ip4GeneratorSpec;
import org.instancio.generator.specs.URIAsGeneratorSpec;
import org.instancio.generator.specs.URLAsGeneratorSpec;
import org.instancio.internal.generator.domain.internet.EmailGenerator;
import org.instancio.internal.generator.domain.internet.Ip4Generator;
import org.instancio.internal.generator.net.URIGenerator;
import org.instancio.internal.generator.net.URLGenerator;

import java.net.URI;
import java.net.URL;

/**
 * Contains built-in generators for {@code java.net} classes.
 *
 * @since 2.3.0
 */
public class NetGenerators {

    private final GeneratorContext context;

    public NetGenerators(final GeneratorContext context) {
        this.context = context;
    }

    /**
     * Customises generated {@link URI} objects.
     *
     * @return generator spec
     * @since 2.3.0
     */
    public URIAsGeneratorSpec uri() {
        return new URIGenerator(context);
    }

    /**
     * Customises generated email addresses.
     *
     * @return generator spec
     * @since 2.11.0
     */
    public EmailAsGeneratorSpec email() {
        return new EmailGenerator(context);
    }

    /**
     * Generates IPv4 address.
     *
     * @return generator spec
     * @since 2.12.0
     */
    public Ip4GeneratorSpec ip4() {
        return new Ip4Generator(context);
    }

    /**
     * Customises generated {@link URL} objects.
     *
     * @return generator spec
     * @since 2.3.0
     */
    public URLAsGeneratorSpec url() {
        return new URLGenerator(context);
    }
}

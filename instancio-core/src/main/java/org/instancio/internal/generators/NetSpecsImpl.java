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
package org.instancio.internal.generators;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.EmailSpec;
import org.instancio.generator.specs.Ip4Spec;
import org.instancio.generator.specs.URISpec;
import org.instancio.generator.specs.URLSpec;
import org.instancio.generators.NetSpecs;
import org.instancio.internal.generator.domain.internet.EmailGenerator;
import org.instancio.internal.generator.domain.internet.Ip4Generator;
import org.instancio.internal.generator.net.URIGenerator;
import org.instancio.internal.generator.net.URLGenerator;

final class NetSpecsImpl implements NetSpecs {

    private final GeneratorContext context;

    NetSpecsImpl(final GeneratorContext context) {
        this.context = context;
    }

    @Override
    public EmailSpec email() {
        return new EmailGenerator(context);
    }

    @Override
    public Ip4Spec ip4() {
        return new Ip4Generator(context);
    }

    @Override
    public URISpec uri() {
        return new URIGenerator(context);
    }

    @Override
    public URLSpec url() {
        return new URLGenerator(context);
    }

}

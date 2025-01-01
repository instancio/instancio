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
package org.instancio.internal.generators;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.pol.NipSpec;
import org.instancio.generator.specs.pol.PeselSpec;
import org.instancio.generator.specs.pol.RegonSpec;
import org.instancio.generators.pol.PolIdSpecs;
import org.instancio.internal.generator.domain.id.pol.NipGenerator;
import org.instancio.internal.generator.domain.id.pol.PeselGenerator;
import org.instancio.internal.generator.domain.id.pol.RegonGenerator;

final class PolIdSpecsImpl implements PolIdSpecs {

    private final GeneratorContext context;

    PolIdSpecsImpl(final GeneratorContext context) {
        this.context = context;
    }

    @Override
    public NipSpec nip() {
        return new NipGenerator(context);
    }

    @Override
    public PeselSpec pesel() {
        return new PeselGenerator(context);
    }

    @Override
    public RegonSpec regon() {
        return new RegonGenerator(context);
    }
}

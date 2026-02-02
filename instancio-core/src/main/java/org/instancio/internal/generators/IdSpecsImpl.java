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
package org.instancio.internal.generators;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.EanSpec;
import org.instancio.generator.specs.IsbnSpec;
import org.instancio.generators.IdSpecs;
import org.instancio.generators.bra.BraIdSpecs;
import org.instancio.generators.can.CanIdSpecs;
import org.instancio.generators.pol.PolIdSpecs;
import org.instancio.generators.rus.RusIdSpecs;
import org.instancio.generators.usa.UsaIdSpecs;
import org.instancio.internal.generator.domain.id.EanGenerator;
import org.instancio.internal.generator.domain.id.IsbnGenerator;

final class IdSpecsImpl implements IdSpecs {

    private final GeneratorContext context;

    IdSpecsImpl(final GeneratorContext context) {
        this.context = context;
    }

    @Override
    public EanSpec ean() {
        return new EanGenerator(context);
    }

    @Override
    public IsbnSpec isbn() {
        return new IsbnGenerator(context);
    }

    @Override
    public CanIdSpecs can() {
        return new CanIdSpecsImpl(context);
    }

    @Override
    public PolIdSpecs pol() {
        return new PolIdSpecsImpl(context);
    }

    @Override
    public UsaIdSpecs usa() {
        return new UsaIdSpecsImpl(context);
    }

    @Override
    public BraIdSpecs bra() {
        return new BraIdSpecsImpl(context);
    }

    @Override
    public RusIdSpecs rus() {
        return new RusIdSpecsImpl(context);
    }
}

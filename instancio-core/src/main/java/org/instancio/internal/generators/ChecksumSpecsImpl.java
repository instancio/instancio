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
import org.instancio.generator.specs.LuhnSpec;
import org.instancio.generator.specs.Mod10Spec;
import org.instancio.generator.specs.Mod11Spec;
import org.instancio.generators.ChecksumSpecs;
import org.instancio.internal.generator.checksum.LuhnGenerator;
import org.instancio.internal.generator.checksum.Mod10Generator;
import org.instancio.internal.generator.checksum.Mod11Generator;

final class ChecksumSpecsImpl implements ChecksumSpecs {

    private final GeneratorContext context;

    ChecksumSpecsImpl(final GeneratorContext context) {
        this.context = context;
    }

    @Override
    public LuhnSpec luhn() {
        return new LuhnGenerator(context);
    }

    @Override
    public Mod10Spec mod10() {
        return new Mod10Generator(context);
    }

    @Override
    public Mod11Spec mod11() {
        return new Mod11Generator(context);
    }
}

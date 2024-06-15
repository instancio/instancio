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
import org.instancio.generator.specs.CsvSpec;
import org.instancio.generator.specs.LoremIpsumSpec;
import org.instancio.generator.specs.TextPatternSpec;
import org.instancio.generator.specs.UUIDStringSpec;
import org.instancio.generators.TextSpecs;
import org.instancio.internal.generator.text.CsvGenerator;
import org.instancio.internal.generator.text.LoremIpsumGenerator;
import org.instancio.internal.generator.text.TextPatternGenerator;
import org.instancio.internal.generator.text.UUIDStringGenerator;

final class TextSpecsImpl implements TextSpecs {

    private final GeneratorContext context;

    TextSpecsImpl(final GeneratorContext context) {
        this.context = context;
    }

    @Override
    public CsvSpec csv() {
        return new CsvGenerator(context);
    }

    @Override
    public LoremIpsumSpec loremIpsum() {
        return new LoremIpsumGenerator(context);
    }

    @Override
    public TextPatternSpec pattern(final String pattern) {
        return new TextPatternGenerator(context, pattern);
    }

    @Override
    public UUIDStringSpec uuid() {
        return new UUIDStringGenerator(context);
    }
}

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
package org.instancio.internal.generator.checksum;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.LuhnSpec;

public class LuhnGenerator extends VariableLengthModCheckGenerator
        implements LuhnSpec {

    public LuhnGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "luhn()";
    }

    @Override
    public LuhnGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    public LuhnGenerator nullable(final boolean isNullable) {
        super.nullable(isNullable);
        return this;
    }

    @Override
    public LuhnGenerator length(final int length) {
        super.length(length);
        return this;
    }

    @Override
    public LuhnGenerator length(final int min, final int max) {
        super.length(min, max);
        return this;
    }

    @Override
    public LuhnGenerator checkDigitIndex(final int idx) {
        super.checkDigitIndex(idx);
        return this;
    }

    @Override
    public LuhnGenerator startIndex(final int idx) {
        super.startIndex(idx);
        return this;
    }

    @Override
    public LuhnGenerator endIndex(final int idx) {
        super.endIndex(idx);
        return this;
    }
}
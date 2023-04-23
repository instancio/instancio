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
package org.instancio.internal.generator.text;

import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.VariableLengthModuleGenerator;

public class Mod10Generator extends VariableLengthModuleGenerator<Mod10Generator> {

    private int multiplier;
    private int weight;

    public Mod10Generator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return null;
    }

    @Override
    public Mod10Generator nullable() {
        super.nullable();
        return this;
    }

    @Override
    public Mod10Generator nullable(final boolean isNullable) {
        super.nullable(isNullable);
        return this;
    }

    public Mod10Generator multiplier(final int multiplier) {
        ApiValidator.isTrue(multiplier >= 0, "Multiplier must not be negative: %s", multiplier);
        this.multiplier = multiplier;
        return this;
    }

    public Mod10Generator weight(final int weight) {
        ApiValidator.isTrue(weight >= 0, "Weight must not be negative: %s", weight);
        this.weight = weight;
        return this;
    }

    @Override
    protected int even(final int position) {
        return multiplier;
    }

    @Override
    protected int odd(final int position) {
        return weight;
    }

    @Override
    protected boolean sumDigits() {
        return false;
    }
}
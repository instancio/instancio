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
package org.instancio.internal.generator.checksum;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.Mod10Spec;
import org.instancio.internal.ApiValidator;

public class Mod10Generator extends VariableLengthModCheckGenerator
        implements Mod10Spec {

    private static final int DEFAULT_MULTIPLIER = 3;
    private static final int DEFAULT_WEIGHT = 1;

    private int multiplier = DEFAULT_MULTIPLIER;
    private int weight = DEFAULT_WEIGHT;

    public Mod10Generator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "mod10()";
    }

    @Override
    public Mod10Generator length(final int length) {
        super.length(length);
        return this;
    }

    @Override
    public Mod10Generator multiplier(final int multiplier) {
        ApiValidator.isTrue(multiplier >= 0, "multiplier must not be negative: %s", multiplier);
        this.multiplier = multiplier;
        return this;
    }

    @Override
    public Mod10Generator weight(final int weight) {
        ApiValidator.isTrue(weight >= 0, "weight must not be negative: %s", weight);
        this.weight = weight;
        return this;
    }

    @Override
    public Mod10Generator startIndex(final int idx) {
        super.startIndex(idx);
        return this;
    }

    @Override
    public Mod10Generator endIndex(final int idx) {
        super.endIndex(idx);
        return this;
    }

    @Override
    public Mod10Generator checkDigitIndex(final int idx) {
        super.checkDigitIndex(idx);
        return this;
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
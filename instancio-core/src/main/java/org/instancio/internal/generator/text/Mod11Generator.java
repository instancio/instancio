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

public class Mod11Generator extends VariableLengthModuleGenerator<Mod11Generator> {

    private int threshold;
    private char treatCheck10As;
    private char treatCheck11As;
    private boolean reverse;

    public Mod11Generator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return null;
    }

    @Override
    public Mod11Generator nullable() {
        super.nullable();
        return this;
    }

    @Override
    public Mod11Generator nullable(final boolean isNullable) {
        super.nullable(isNullable);
        return this;
    }

    public Mod11Generator threshold(final int threshold) {
        ApiValidator.isTrue(threshold >= 0, "Threshold must not be negative: %s", threshold);
        this.threshold = threshold;
        return this;
    }

    public Mod11Generator treatCheck10As(final char treatCheck10As) {
        this.treatCheck10As = treatCheck10As;
        return this;
    }

    public Mod11Generator treatCheck11As(final char treatCheck11As) {
        this.treatCheck11As = treatCheck11As;
        return this;
    }

    public Mod11Generator reverse(final boolean reverse) {
        this.reverse = reverse;
        return this;
    }

    @Override
    protected int even(final int position) {
        return weight(position);
    }

    @Override
    protected int odd(final int position) {
        return weight(position);
    }

    private int weight(final int position) {
        return position % (threshold - 1) + 2;
    }

    @Override
    protected boolean sumDigits() {
        return false;
    }

    @Override
    protected boolean reverse() {
        return reverse;
    }

    @Override
    protected int base() {
        return 11;
    }

    @Override
    protected char treat10As() {
        return treatCheck10As;
    }

    @Override
    protected char treat11As() {
        return treatCheck11As;
    }
}
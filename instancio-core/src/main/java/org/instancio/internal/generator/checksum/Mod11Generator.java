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
import org.instancio.generator.specs.Mod11Spec;
import org.instancio.internal.ApiValidator;

public class Mod11Generator extends VariableLengthModCheckGenerator
        implements Mod11Spec {

    private static final int DEFAULT_THRESHOLD = Integer.MAX_VALUE;
    private static final char DEFAULT_TREAT_10_AS = 'X';
    private static final char DEFAULT_TREAT_11_AS = '0';

    private int threshold = DEFAULT_THRESHOLD;
    private char treatCheck10As = DEFAULT_TREAT_10_AS;
    private char treatCheck11As = DEFAULT_TREAT_11_AS;
    private Direction direction = Direction.RIGHT_TO_LEFT;

    public Mod11Generator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "mod11()";
    }

    @Override
    public Mod11Generator length(final int length) {
        super.length(length);
        return this;
    }

    @Override
    public Mod11Generator threshold(final int threshold) {
        ApiValidator.isTrue(threshold >= 0, "threshold must not be negative: %s", threshold);
        this.threshold = threshold;
        return this;
    }

    @Override
    public Mod11Generator treatCheck10As(final char treatCheck10As) {
        this.treatCheck10As = treatCheck10As;
        return this;
    }

    @Override
    public Mod11Generator treatCheck11As(final char treatCheck11As) {
        this.treatCheck11As = treatCheck11As;
        return this;
    }

    @Override
    public Mod11Generator startIndex(final int idx) {
        super.startIndex(idx);
        return this;
    }

    @Override
    public Mod11Generator endIndex(final int idx) {
        super.endIndex(idx);
        return this;
    }

    @Override
    public Mod11Generator checkDigitIndex(final int idx) {
        super.checkDigitIndex(idx);
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

    @Override
    public Mod11Generator leftToRight() {
        direction(Direction.LEFT_TO_RIGHT);
        return this;
    }

    public Mod11Generator direction(final Direction direction) {
        this.direction = direction;
        return this;
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

    @Override
    protected boolean sumDigits() {
        return false;
    }

    @Override
    protected Direction direction() {
        return direction;
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

    private int weight(final int position) {
        return position % (threshold - 1) + 2;
    }
}
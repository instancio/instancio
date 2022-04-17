/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.generator.lang;

import org.instancio.generator.AbstractGenerator;
import org.instancio.generator.GeneratedHints;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.random.RandomProvider;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.instancio.util.Verify;

public class StringGenerator extends AbstractGenerator<String> implements StringGeneratorSpec {

    private static final String NEGATIVE_LENGTH = "Length must be negative: %s";
    private int minLength;
    private int maxLength;
    private boolean nullable;
    private boolean allowEmpty;
    private String prefix = "";
    private StringType stringType;

    public StringGenerator(final GeneratorContext context) {
        super(context);

        final Settings settings = context.getSettings();
        this.minLength = settings.get(Setting.STRING_MIN_LENGTH);
        this.maxLength = settings.get(Setting.STRING_MAX_LENGTH);
        this.nullable = settings.get(Setting.STRING_NULLABLE);
        this.allowEmpty = settings.get(Setting.STRING_ALLOW_EMPTY);
    }

    @Override
    public StringGeneratorSpec prefix(final String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public StringGeneratorSpec allowEmpty() {
        this.allowEmpty = true;
        return this;
    }

    @Override
    public StringGeneratorSpec nullable() {
        this.nullable = true;
        return this;
    }

    @Override
    public StringGeneratorSpec length(final int length) {
        Verify.isTrue(length >= 0, NEGATIVE_LENGTH, length);
        this.minLength = length;
        this.maxLength = length;
        return this;
    }

    @Override
    public StringGeneratorSpec minLength(final int length) {
        Verify.isTrue(length >= 0, NEGATIVE_LENGTH, length);
        this.minLength = length;
        this.maxLength = Math.max(length, maxLength);
        return this;
    }

    @Override
    public StringGeneratorSpec maxLength(final int length) {
        Verify.isTrue(length >= 0, NEGATIVE_LENGTH, length);
        this.maxLength = length;
        this.minLength = Math.min(minLength, length);
        return this;
    }

    @Override
    public StringGeneratorSpec lowerCase() {
        stringType = StringType.LOWER_CASE;
        return this;
    }

    @Override
    public StringGeneratorSpec upperCase() {
        stringType = StringType.UPPER_CASE;
        return this;
    }

    @Override
    public StringGeneratorSpec mixedCase() {
        stringType = StringType.MIXED_CASE;
        return this;
    }

    @Override
    public StringGeneratorSpec alphaNumeric() {
        stringType = StringType.ALPHANUMERIC;
        return this;
    }

    @Override
    public StringGeneratorSpec digits() {
        stringType = StringType.DIGITS;
        return this;
    }

    @Override
    public String generate(final RandomProvider random) {
        if (random.diceRoll(nullable)) {
            return null;
        }
        if (random.diceRoll(allowEmpty)) {
            return "";
        }

        final int length = random.intRange(minLength, maxLength + 1);
        return prefix + generateString(random, length);
    }

    private String generateString(final RandomProvider random, final int length) {
        if (stringType == null || stringType == StringType.UPPER_CASE) {
            return random.upperCaseAlphabetic(length);
        }
        if (stringType == StringType.LOWER_CASE) {
            return random.lowerCaseAlphabetic(length);
        }
        if (stringType == StringType.MIXED_CASE) {
            return random.mixedCaseAlphabetic(length);
        }
        if (stringType == StringType.ALPHANUMERIC) {
            return random.alphaNumeric(length);
        }
        if (stringType == StringType.DIGITS) {
            return random.digits(length);
        }

        throw new IllegalStateException("Unknown StringType: " + stringType); // unreachable
    }

    @Override
    public GeneratedHints getHints() {
        return GeneratedHints.builder()
                .nullableResult(nullable)
                .ignoreChildren(true)
                .build();
    }
}

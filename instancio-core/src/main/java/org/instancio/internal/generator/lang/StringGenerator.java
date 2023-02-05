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
package org.instancio.internal.generator.lang;

import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.StringSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.context.Global;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.specs.InternalLengthGeneratorSpec;
import org.instancio.internal.util.Constants;
import org.instancio.internal.util.NumberUtils;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

public class StringGenerator extends AbstractGenerator<String> implements StringSpec {

    private enum StringType {
        LOWER_CASE, UPPER_CASE, MIXED_CASE, ALPHANUMERIC, DIGITS
    }

    protected int minLength;
    protected int maxLength;
    private boolean allowEmpty;
    private String prefix;
    private String suffix;
    private StringType stringType;

    /**
     * Delegate for internal use only. It is used to support Bean Validation.
     * If delegate is set, then it will be used for generating the value,
     * which is then converted {@code toString()}.
     */
    private Generator<?> delegate;

    public void setDelegate(final Generator<?> delegate) {
        this.delegate = delegate;
    }

    public StringGenerator() {
        this(Global.generatorContext());
    }

    public StringGenerator(final GeneratorContext context) {
        super(context);

        final Settings settings = context.getSettings();
        this.minLength = settings.get(Keys.STRING_MIN_LENGTH);
        this.maxLength = settings.get(Keys.STRING_MAX_LENGTH);
        super.nullable(settings.get(Keys.STRING_NULLABLE));
        this.allowEmpty = settings.get(Keys.STRING_ALLOW_EMPTY);
    }

    public final int getMinLength() {
        return minLength;
    }

    @Override
    public String apiMethod() {
        return "string()";
    }

    @Override
    public StringGenerator prefix(final String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public StringGenerator suffix(final String suffix) {
        this.suffix = suffix;
        return this;
    }

    @Override
    public StringGenerator allowEmpty() {
        this.allowEmpty = true;
        return this;
    }

    @Override
    public StringSpec allowEmpty(final boolean isAllowed) {
        this.allowEmpty = isAllowed;
        return this;
    }

    @Override
    public StringGenerator nullable() {
        super.nullable();
        if (delegate instanceof AbstractGenerator<?>) {
            ((AbstractGenerator<?>) delegate).nullable();
        }
        return this;
    }

    @Override
    public StringGenerator nullable(final boolean isNullable) {
        super.nullable(isNullable);
        if (delegate instanceof AbstractGenerator<?>) {
            ((AbstractGenerator<?>) delegate).nullable(isNullable);
        }
        return this;
    }

    @Override
    public StringGenerator length(final int length) {
        this.minLength = ApiValidator.validateLength(length);
        this.maxLength = length;
        return this;
    }

    @Override
    public StringGenerator length(final int minLength, final int maxLength) {
        this.minLength = ApiValidator.validateLength(minLength);
        this.maxLength = ApiValidator.validateLength(maxLength);
        ApiValidator.isTrue(minLength <= maxLength,
                "Min length must be less than or equal to max (%s, %s)", minLength, maxLength);

        if (delegate instanceof InternalLengthGeneratorSpec<?>) {
            ((InternalLengthGeneratorSpec<?>) delegate).length(minLength, maxLength);
        }
        return this;
    }

    @Override
    public StringGenerator minLength(final int length) {
        this.minLength = ApiValidator.validateLength(length);
        this.maxLength = NumberUtils.calculateNewMax(maxLength, minLength, Constants.RANGE_ADJUSTMENT_PERCENTAGE);
        return this;
    }

    @Override
    public StringGenerator maxLength(final int length) {
        this.maxLength = ApiValidator.validateLength(length);
        this.minLength = NumberUtils.calculateNewMin(minLength, maxLength, Constants.RANGE_ADJUSTMENT_PERCENTAGE);
        return this;
    }

    @Override
    public StringGenerator lowerCase() {
        stringType = StringType.LOWER_CASE;
        return this;
    }

    @Override
    public StringGenerator upperCase() {
        stringType = StringType.UPPER_CASE;
        return this;
    }

    @Override
    public StringGenerator mixedCase() {
        stringType = StringType.MIXED_CASE;
        return this;
    }

    @Override
    public StringGenerator alphaNumeric() {
        stringType = StringType.ALPHANUMERIC;
        return this;
    }

    @Override
    public StringGenerator digits() {
        stringType = StringType.DIGITS;
        return this;
    }

    @Override
    public String generate(final Random random) {
        if (delegate != null) {
            final Object result = delegate.generate(random);
            return result == null ? null : result.toString();
        }
        if (random.diceRoll(isNullable())) {
            return null;
        }
        if (random.diceRoll(allowEmpty)) {
            return "";
        }

        final int length = random.intRange(minLength, maxLength);
        String result = generateString(random, length);
        if (prefix != null) {
            result = prefix + result;
        }
        if (suffix != null) {
            result = result + suffix;
        }
        return result;
    }

    private String generateString(final Random random, final int length) {
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
            return random.alphanumeric(length);
        }
        if (stringType == StringType.DIGITS) {
            return random.digits(length);
        }

        throw new IllegalStateException("Unknown StringType: " + stringType); // unreachable
    }
}

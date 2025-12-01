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
package org.instancio.internal.generator.lang;

import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.StringSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.specs.InternalLengthGeneratorSpec;
import org.instancio.internal.util.Constants;
import org.instancio.internal.util.NumberUtils;
import org.instancio.internal.util.UnicodeBlocks;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("PMD.GodClass")
public class StringGenerator extends AbstractGenerator<String>
        implements StringSpec, InternalLengthGeneratorSpec<String> {

    protected int minLength;
    protected int maxLength;
    private long sequence;
    private boolean allowEmpty;
    private String prefix;
    private String suffix;
    private StringType stringType;
    private StringCase stringCase;
    private List<Character.UnicodeBlock> unicodeBlocks = Collections.emptyList();

    /**
     * Delegate for internal use only. It is used to support Bean Validation.
     * If delegate is set, then it will be used for generating the value,
     * which is then converted {@code toString()}.
     */
    private Generator<?> delegate;

    public StringGenerator(final GeneratorContext context) {
        super(context);

        final Settings settings = context.getSettings();
        this.minLength = settings.get(Keys.STRING_MIN_LENGTH);
        this.maxLength = settings.get(Keys.STRING_MAX_LENGTH);
        super.nullable(settings.get(Keys.STRING_NULLABLE));
        this.allowEmpty = settings.get(Keys.STRING_ALLOW_EMPTY);
        this.stringCase = StringCase.valueOf(settings.get(Keys.STRING_CASE).name());
        this.stringType = StringType.valueOf(settings.get(Keys.STRING_TYPE).name());
    }

    public final int getMinLength() {
        return minLength;
    }

    public void setDelegate(final Generator<?> delegate) {
        this.delegate = delegate;
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
        if (delegate instanceof AbstractGenerator<?> g) {
            g.nullable();
        }
        return this;
    }

    @Override
    public StringGenerator nullable(final boolean isNullable) {
        super.nullable(isNullable);
        if (delegate instanceof AbstractGenerator<?> g) {
            g.nullable(isNullable);
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
                "min length must be less than or equal to max (%s, %s)", minLength, maxLength);

        if (delegate instanceof InternalLengthGeneratorSpec<?> lengthSpec) {
            lengthSpec.length(minLength, maxLength);
        }
        return this;
    }

    @Override
    public StringGenerator minLength(final int length) {
        this.minLength = ApiValidator.validateLength(length);
        this.maxLength = NumberUtils.calculateNewMaxSize(maxLength, minLength);
        return this;
    }

    @Override
    public StringGenerator maxLength(final int length) {
        this.maxLength = ApiValidator.validateLength(length);
        this.minLength = NumberUtils.calculateNewMinSize(minLength, maxLength);
        return this;
    }

    @Override
    public StringGenerator lowerCase() {
        stringCase = StringCase.LOWER;
        return this;
    }

    @Override
    public StringGenerator upperCase() {
        stringCase = StringCase.UPPER;
        return this;
    }

    @Override
    public StringGenerator mixedCase() {
        stringCase = StringCase.MIXED;
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
    public StringGenerator hex() {
        stringType = StringType.HEX;
        return this;
    }

    @Override
    public StringGenerator numericSequence() {
        stringType = StringType.NUMERIC_SEQUENCE;
        return this;
    }

    @Override
    public StringGenerator unicode(Character.UnicodeBlock... blocks) {
        stringType = StringType.UNICODE;
        unicodeBlocks = Arrays.asList(blocks);
        return this;
    }

    // Hot path - benchmark when making changes.
    @Override
    public String tryGenerateNonNull(final Random random) {
        if (delegate != null) {
            final Object result = delegate.generate(random);
            return result == null ? null : result.toString();
        }
        if (random.diceRoll(allowEmpty)) {
            return "";
        }

        String result = generateString(random);

        if (prefix != null) {
            result = prefix + result;
        }
        if (suffix != null) {
            result = result + suffix;
        }
        return result;
    }

    @NotNull
    private String generateString(final Random random) {
        if (stringType == StringType.NUMERIC_SEQUENCE) {
            return String.valueOf(++sequence);
        }
        final int length = random.intRange(minLength, maxLength);

        return stringType == StringType.UNICODE
                ? generateUnicodeString(random, length)
                : generateAsciiString(random, length);
    }

    @NotNull
    private String generateAsciiString(final Random random, final int length) {
        final char[] fromChars = getStringCharacters();
        final char[] s = new char[length];

        for (int i = 0; i < length; i++) {
            s[i] = fromChars[random.intRange(0, fromChars.length - 1)];
        }

        return new String(s);
    }

    @SuppressWarnings("PMD.AvoidReassigningLoopVariables")
    private String generateUnicodeString(final Random random, final int length) {
        final StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; ) {
            final int codePoint = getCodePoint(random);
            int type = Character.getType(codePoint);
            if (type == Character.PRIVATE_USE
                    || type == Character.SURROGATE
                    || type == Character.UNASSIGNED) {
                continue;
            }

            sb.appendCodePoint(codePoint);
            i++; // NOSONAR
        }
        return sb.toString();
    }

    private int getCodePoint(final Random random) {
        if (unicodeBlocks.isEmpty()) {
            return random.intRange(0, Constants.MAX_CODE_POINT);
        }
        final Character.UnicodeBlock block = random.oneOf(unicodeBlocks);
        final UnicodeBlocks.BlockRange range = UnicodeBlocks.getInstance().getRange(block);
        return random.intRange(range.min(), range.max());
    }

    private char[] getStringCharacters() {
        if (stringCase == StringCase.UPPER) {
            return stringType.upperCaseChars;
        }
        if (stringCase == StringCase.LOWER) {
            return stringType.lowerCaseChars;
        }
        if (stringCase == StringCase.MIXED) {
            return stringType.mixedCaseChars;
        }
        throw new IllegalStateException("Unknown StringType: " + stringType); // unreachable
    }

    private enum StringCase {
        LOWER, UPPER, MIXED
    }

    private enum StringType {
        ALPHABETIC(
                Chars.LC_ALPHABETIC,
                Chars.UC_ALPHABETIC,
                Chars.MC_ALPHABETIC),

        ALPHANUMERIC(
                Chars.LC_ALPHANUMERIC,
                Chars.UC_ALPHANUMERIC,
                Chars.MC_ALPHANUMERIC),

        DIGITS(
                Chars.DIGIT_CHARS,
                Chars.DIGIT_CHARS,
                Chars.DIGIT_CHARS),

        HEX(
                Chars.LC_HEX,
                Chars.UC_HEX,
                Chars.MC_HEX),

        NUMERIC_SEQUENCE(),

        UNICODE();

        final char[] lowerCaseChars;
        final char[] upperCaseChars;
        final char[] mixedCaseChars;

        @SuppressWarnings("PMD.UseVarargs")
        StringType(final char[] lowerCaseChars, final char[] upperCaseChars, final char[] mixedCaseChars) {
            this.lowerCaseChars = lowerCaseChars;
            this.upperCaseChars = upperCaseChars;
            this.mixedCaseChars = mixedCaseChars;
        }

        StringType() {
            this(new char[0], new char[0], new char[0]);
        }

        private static final class Chars {
            private static final String DIGITS = "0123456789";
            private static final String ALPHABETIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

            // Digits
            private static final char[] DIGIT_CHARS = DIGITS.toCharArray();
            // Alphabetic
            private static final char[] UC_ALPHABETIC = ALPHABETIC.toCharArray();
            private static final char[] LC_ALPHABETIC = ALPHABETIC.toLowerCase(Locale.ROOT).toCharArray();
            private static final char[] MC_ALPHABETIC = (ALPHABETIC + ALPHABETIC.toLowerCase(Locale.ROOT)).toCharArray();
            // Alphanumeric
            private static final char[] LC_ALPHANUMERIC = (DIGITS + ALPHABETIC.toLowerCase(Locale.ROOT)).toCharArray();
            private static final char[] UC_ALPHANUMERIC = (DIGITS + ALPHABETIC).toCharArray();
            private static final char[] MC_ALPHANUMERIC = (DIGITS + ALPHABETIC + ALPHABETIC.toLowerCase(Locale.ROOT)).toCharArray();
            // Hex
            private static final char[] LC_HEX = (DIGITS + "abcdef").toCharArray();
            private static final char[] UC_HEX = (DIGITS + "ABCDEF").toCharArray();
            private static final char[] MC_HEX = (DIGITS + "abcdefABCDEF").toCharArray();
        }
    }
}

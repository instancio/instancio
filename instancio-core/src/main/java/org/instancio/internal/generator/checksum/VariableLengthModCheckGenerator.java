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
package org.instancio.internal.generator.checksum;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.specs.InternalLengthGeneratorSpec;

import static org.instancio.internal.util.Constants.NL;

abstract class VariableLengthModCheckGenerator
        extends BaseModCheckGenerator implements InternalLengthGeneratorSpec<String> {

    private static final int DEFAULT_SIZE = 16;

    private int minSize = DEFAULT_SIZE;
    private int maxSize = DEFAULT_SIZE;
    private int startIndex;
    private int endIndex = -1;
    private int checkDigitIndex = -1;
    private int size;

    protected VariableLengthModCheckGenerator(final GeneratorContext context) {
        super(context);
    }

    public VariableLengthModCheckGenerator startIndex(final int idx) {
        ApiValidator.isTrue(idx >= 0, "start index must not be negative: %s", idx);
        this.startIndex = idx;
        return this;
    }

    public VariableLengthModCheckGenerator endIndex(final int idx) {
        ApiValidator.isTrue(idx >= 0, "end index must not be negative: %s", idx);
        // Avoid generating large strings
        // The default value of Hibernate "endIndex" is Integer.MAX_VALUE
        this.endIndex = idx == Integer.MAX_VALUE ? -1 : idx;
        return this;
    }

    public VariableLengthModCheckGenerator checkDigitIndex(final int idx) {
        ApiValidator.isTrue(idx >= 0, "check digit index must not be negative: %s", idx);
        this.checkDigitIndex = idx;
        return this;
    }

    public VariableLengthModCheckGenerator length(final int length) {
        ApiValidator.isTrue(length > 1, "number length must be greater than 1, but was: %s", length);
        this.minSize = length;
        this.maxSize = length;
        return this;
    }

    @Override
    public VariableLengthModCheckGenerator length(final int min, final int max) {
        ApiValidator.isTrue(min > 0 && max > 1,
                "number length must be greater than 1, but was: length(%s, %s)", min, max);
        ApiValidator.isTrue(min <= max, "min must be less than or equal to max");

        this.minSize = min;
        this.maxSize = max;
        return this;
    }

    @Override
    protected String tryGenerateNonNull(final Random random) {
        size = random.intRange(minSize, maxSize);
        endIndex = endIndex == -1 ? size - 1 : endIndex;
        checkDigitIndex = checkDigitIndex == -1 ? endIndex : checkDigitIndex;
        validateCheckDigit();

        endIndex = endIndex == checkDigitIndex ? endIndex - 1 : endIndex;
        size = Math.max(checkDigitIndex + 1, Math.max(size, endIndex + 1));

        return super.tryGenerateNonNull(random);
    }

    private void validateCheckDigit() {
        boolean isValidCheckDigit = checkDigitIndex >= 0 && (checkDigitIndex < startIndex || checkDigitIndex >= endIndex);
        ApiValidator.isTrue(isValidCheckDigit, this::getCheckDigitErrorMessage);
    }

    private String getCheckDigitErrorMessage() {
        //noinspection StringBufferReplaceableByString
        return new StringBuilder()
                .append("checkDigitIndex must satisfy condition:").append(NL)
                .append("  ->  checkDigitIndex >= 0 && (checkDigitIndex < startIndex || checkDigitIndex >= endIndex)").append(NL)
                .append(NL)
                .append("Actual values were:").append(NL)
                .append("  -> startIndex .......: ").append(startIndex).append(NL)
                .append("  -> endIndex .........: ").append(endIndex).append(NL)
                .append("  -> checkDigitIndex ..: ").append(checkDigitIndex).append(NL)
                .toString();
    }

    @Override
    protected int prefixLength() {
        return startIndex;
    }

    @Override
    protected int payloadLength() {
        return endIndex - startIndex + 1;
    }

    @Override
    protected int checkPosition() {
        return checkDigitIndex;
    }

    @Override
    protected int suffixLength() {
        return size - endIndex - 1;
    }
}

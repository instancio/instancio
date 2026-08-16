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

    protected VariableLengthModCheckGenerator(final GeneratorContext context) {
        super(context);
    }

    VariableLengthModCheckGenerator startIndex(final int idx) {
        ApiValidator.isTrue(idx >= 0, "start index must not be negative: %s", idx);
        this.startIndex = idx;
        return this;
    }

    VariableLengthModCheckGenerator endIndex(final int idx) {
        ApiValidator.isTrue(idx >= 0, "end index must not be negative: %s", idx);
        // Avoid generating large strings
        // The default value of Hibernate "endIndex" is Integer.MAX_VALUE
        this.endIndex = idx == Integer.MAX_VALUE ? -1 : idx;
        return this;
    }

    VariableLengthModCheckGenerator checkDigitIndex(final int idx) {
        ApiValidator.isTrue(idx >= 0, "check digit index must not be negative: %s", idx);
        this.checkDigitIndex = idx;
        return this;
    }

    VariableLengthModCheckGenerator length(final int length) {
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

    /**
     * Resolves the configured indices against the size of the number
     * about to be generated. The {@code -1} defaults mean "not specified",
     * in which case the values are derived from the size.
     */
    @Override
    protected Layout layout(final Random random) {
        final int resolvedStartIndex = startIndex;
        final int generatedSize = random.intRange(minSize, maxSize);
        final int configuredEndIndex = endIndex;
        final int configuredCheckDigitIndex = checkDigitIndex;

        final int resolvedEndIndex = configuredEndIndex == -1 ? generatedSize - 1 : configuredEndIndex;
        final int resolvedCheckDigitIndex = configuredCheckDigitIndex == -1
                ? resolvedEndIndex
                : configuredCheckDigitIndex;

        // The check digit overwrites the last payload digit if they overlap,
        // therefore the payload must stop one digit short of the check digit
        final int payloadEndIndex = resolvedEndIndex == resolvedCheckDigitIndex
                ? resolvedEndIndex - 1
                : resolvedEndIndex;

        validateIndices(resolvedStartIndex, resolvedEndIndex, resolvedCheckDigitIndex, payloadEndIndex);

        // The number must be long enough to hold the payload and the check digit
        final int size = Math.max(resolvedCheckDigitIndex + 1,
                Math.max(generatedSize, payloadEndIndex + 1));

        return new Layout(
                /* prefixLength  = */ resolvedStartIndex,
                /* payloadLength = */ payloadEndIndex - resolvedStartIndex + 1,
                /* suffixLength  = */ size - payloadEndIndex - 1,
                /* checkPosition = */ resolvedCheckDigitIndex);
    }

    private void validateIndices(
            final int resolvedStartIndex,
            final int resolvedEndIndex,
            final int resolvedCheckDigitIndex,
            final int payloadEndIndex) {

        final boolean isValidCheckDigitIndex = resolvedCheckDigitIndex < resolvedStartIndex
                || resolvedCheckDigitIndex >= resolvedEndIndex;

        ApiValidator.isTrue(isValidCheckDigitIndex, () -> getErrorMessage(
                "checkDigitIndex must satisfy condition:" + NL
                        + "  ->  checkDigitIndex < startIndex || checkDigitIndex >= endIndex",
                resolvedStartIndex, resolvedEndIndex, resolvedCheckDigitIndex));

        // the check digit must be calculated from at least one digit
        ApiValidator.isTrue(payloadEndIndex >= resolvedStartIndex, () -> getErrorMessage(
                "startIndex and endIndex must satisfy condition:" + NL
                        + "  ->  startIndex < endIndex || (startIndex <= endIndex && checkDigitIndex != endIndex)",
                resolvedStartIndex, resolvedEndIndex, resolvedCheckDigitIndex));
    }

    @SuppressWarnings({"StringBufferReplaceableByString", "UnnecessaryStringBuilder"})
    private String getErrorMessage(
            final String reason,
            final int resolvedStartIndex,
            final int resolvedEndIndex,
            final int resolvedCheckDigitIndex) {

        return new StringBuilder()
                .append(reason).append(NL)
                .append(NL)
                .append("Actual values were:").append(NL)
                .append("  -> startIndex .......: ").append(resolvedStartIndex).append(NL)
                .append("  -> endIndex .........: ").append(resolvedEndIndex).append(NL)
                .append("  -> checkDigitIndex ..: ").append(resolvedCheckDigitIndex).append(NL)
                .toString();
    }
}

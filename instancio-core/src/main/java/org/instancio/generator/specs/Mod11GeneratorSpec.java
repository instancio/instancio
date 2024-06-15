/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.generator.specs;

/**
 * A spec for generating numbers that pass the Mod11 checksum algorithm.
 *
 * <p>The methods provided by this interface (as well as their Javadocs)
 * were copied from the {@code org.hibernate.validator.constraints.Mod11Check}
 * annotation attributes.
 *
 * @since 2.16.0
 */
public interface Mod11GeneratorSpec extends
        AsGeneratorSpec<String>,
        NullableGeneratorSpec<String> {

    /**
     * Length of the number to generate (default value is {@code 16}).
     *
     * @param length of the number to generate
     * @return spec builder
     * @since 2.16.0
     */
    Mod11GeneratorSpec length(int length);

    /**
     * The threshold for the Mod11 algorithm multiplier growth,
     * if no value is specified the multiplier will grow indefinitely
     * (default value is {@link Integer#MAX_VALUE}).
     *
     * @param threshold for the multiplier growth
     * @return spec builder
     * @since 2.16.0
     */
    Mod11GeneratorSpec threshold(int threshold);

    /**
     * The start index for calculating the checksum
     * (default value is {@code 0}).
     *
     * @param startIndex for calculating the checksum (inclusive)
     * @return spec builder
     * @since 2.16.0
     */
    Mod11GeneratorSpec startIndex(int startIndex);

    /**
     * The end index for calculating the checksum.
     *
     * @param endIndex for calculating the checksum (inclusive)
     * @return spec builder
     * @since 2.16.0
     */
    Mod11GeneratorSpec endIndex(int endIndex);

    /**
     * The index of the check digit in the input.
     * If not specified, the last digit will be used as the check digit.
     *
     * <p>If set, the digit at the specified index is used. If set
     * the following must hold true:<br>
     * {@code checkDigitIndex >= 0 && (checkDigitIndex < startIndex || checkDigitIndex >= endIndex)}.
     *
     * @param checkDigitIndex index of the check digit
     * @return spec builder
     * @since 2.16.0
     */
    Mod11GeneratorSpec checkDigitIndex(int checkDigitIndex);

    /**
     * The {@code char} that represents the check digit when the Mod11 checksum
     * equals {@code 10} (default value is {@code 'X'}).
     *
     * @param treatCheck10As check digit character to use when
     *                       the checksum is {@code 10}
     * @return spec builder
     * @since 2.16.0
     */
    Mod11GeneratorSpec treatCheck10As(char treatCheck10As);

    /**
     * The {@code char} that represents the check digit when the Mod11 checksum
     * equals {@code 11} (default value is {@code '0'}).
     *
     * @param treatCheck11As check digit character to use when
     *                       the checksum is {@code 11}
     * @return spec builder
     * @since 2.16.0
     */
    Mod11GeneratorSpec treatCheck11As(char treatCheck11As);

    /**
     * Specifies that the Mod11 checksum must be done from the leftmost
     * to the rightmost digit (default behaviour is right to left).
     *
     * <p> e.g. Code 12345-?:
     * <ul>
     * <li>Right to left: the sum (5*2 + 4*3 + 3*4 + 2*5 + 1*6) with check digit 5</li>
     * <li>Left to right: the sum (1*2 + 2*3 + 3*4 + 4*5 + 5*6) with check digit 7</li>
     * </ul>
     *
     * @return spec builder
     * @since 2.16.0
     */
    Mod11GeneratorSpec leftToRight();

    /**
     * {@inheritDoc}
     *
     * @since 2.16.0
     */
    @Override
    Mod11GeneratorSpec nullable();
}

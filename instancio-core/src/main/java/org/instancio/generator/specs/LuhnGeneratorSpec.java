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
package org.instancio.generator.specs;

/**
 * A spec for generating numbers that pass the Luhn checksum algorithm.
 *
 * <p>The following methods:
 *
 * <ul>
 *   <li>{@link #checkDigitIndex(int)}</li>
 *   <li>{@link #startIndex(int)}</li>
 *   <li>{@link #endIndex(int)}</li>
 * </ul>
 *
 * <p>provided by this interface (as well as their Javadocs)
 * were copied from the {@code org.hibernate.validator.constraints.LuhnCheck}
 * annotation attributes.
 *
 * @since 3.1.0
 */
public interface LuhnGeneratorSpec extends
        AsGeneratorSpec<String>,
        NullableGeneratorSpec<String> {

    /**
     * Length of the number to generate (default value is {@code 16}).
     *
     * @param length of the value to generate
     * @return spec builder
     * @since 3.1.0
     */
    LuhnGeneratorSpec length(int length);

    /**
     * Generate a number of random length within the specified range.
     *
     * @param min minimum length (inclusive)
     * @param max maximum length (inclusive)
     * @return spec builder
     * @since 3.1.0
     */
    LuhnGeneratorSpec length(int min, int max);

    /**
     * The start index for calculating the checksum
     * (default value is {@code 0}).
     *
     * @param startIndex for calculating the checksum (inclusive)
     * @return spec builder
     * @since 3.1.0
     */
    LuhnGeneratorSpec startIndex(int startIndex);

    /**
     * The end index for calculating the checksum.
     *
     * @param endIndex for calculating the checksum (inclusive)
     * @return spec builder
     * @since 3.1.0
     */
    LuhnGeneratorSpec endIndex(int endIndex);

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
     * @since 3.1.0
     */
    LuhnGeneratorSpec checkDigitIndex(int checkDigitIndex);

    /**
     * {@inheritDoc}
     *
     * @since 3.1.0
     */
    @Override
    LuhnGeneratorSpec nullable();
}

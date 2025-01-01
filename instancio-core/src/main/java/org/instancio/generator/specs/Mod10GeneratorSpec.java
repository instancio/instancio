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
package org.instancio.generator.specs;

/**
 * A spec for generating numbers that pass the Mod10 checksum algorithm.
 *
 * <p>The methods provided by this interface (as well as their Javadocs)
 * were copied from the {@code org.hibernate.validator.constraints.Mod10Check}
 * annotation attributes.
 *
 * @since 2.16.0
 */
public interface Mod10GeneratorSpec extends
        AsGeneratorSpec<String>,
        NullableGeneratorSpec<String> {

    /**
     * Length of the number to generate (default value is {@code 16}).
     *
     * @param length of the number to generate
     * @return spec builder
     * @since 2.16.0
     */
    Mod10GeneratorSpec length(int length);

    /**
     * Multiplier to be used for odd digits when calculating the Mod10 checksum
     * (default value is {@code 3}).
     *
     * @param multiplier for odd digits
     * @return spec builder
     * @since 2.16.0
     */
    Mod10GeneratorSpec multiplier(int multiplier);

    /**
     * The weight to be used for even digits when calculating the Mod10
     * checksum (default value is {@code 1}).
     *
     * @param weight for even digits
     * @return spec builder
     * @since 2.16.0
     */
    Mod10GeneratorSpec weight(int weight);

    /**
     * The start index for calculating the checksum
     * (default value is {@code 0}).
     *
     * @param startIndex for calculating the checksum (inclusive)
     * @return spec builder
     * @since 2.16.0
     */
    Mod10GeneratorSpec startIndex(int startIndex);

    /**
     * The end index for calculating the checksum.
     *
     * @param endIndex for calculating the checksum (inclusive)
     * @return spec builder
     * @since 2.16.0
     */
    Mod10GeneratorSpec endIndex(int endIndex);

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
    Mod10GeneratorSpec checkDigitIndex(int checkDigitIndex);

    /**
     * {@inheritDoc}
     *
     * @since 2.16.0
     */
    @Override
    Mod10GeneratorSpec nullable();
}

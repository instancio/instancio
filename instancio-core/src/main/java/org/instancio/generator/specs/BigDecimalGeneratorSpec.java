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
package org.instancio.generator.specs;

import org.instancio.documentation.ExperimentalApi;

import java.math.BigDecimal;

/**
 * Generator spec for {@link BigDecimal}.
 *
 * @since 1.5.4
 */
public interface BigDecimalGeneratorSpec extends NumberGeneratorSpec<BigDecimal> {

    /**
     * {@inheritDoc}
     *
     * <p>Note that this method is incompatible with {@link #precision(int)}.
     * For details, see {@link #precision(int)} javadoc.
     */
    @Override
    BigDecimalGeneratorSpec min(BigDecimal min);

    /**
     * {@inheritDoc}
     *
     * <p>Note that this method is incompatible with {@link #precision(int)}.
     * For details, see {@link #precision(int)} javadoc.
     */
    @Override
    BigDecimalGeneratorSpec max(BigDecimal max);

    /**
     * {@inheritDoc}
     *
     * <p>Note that this method is incompatible with {@link #precision(int)}.
     * For details, see {@link #precision(int)} javadoc.
     */
    @Override
    BigDecimalGeneratorSpec range(BigDecimal min, BigDecimal max);

    @Override
    BigDecimalGeneratorSpec nullable();

    /**
     * Scale of the generated {@link BigDecimal}.
     * Generated values will have given {@code scale}
     * as returned by {@link BigDecimal#scale()}.
     *
     * <p>If not specified, the default scale is {@code 2}.
     *
     * @param scale the scale of generated {@code BigDecimal} values
     * @return spec builder
     * @since 1.5.4
     */
    BigDecimalGeneratorSpec scale(int scale);

    /**
     * Precision of the generated {@link BigDecimal}.
     * Generated values will have given {@code precision}
     * as returned by {@link BigDecimal#precision()}.
     *
     * <p>This method is incompatible with the following methods:
     *
     * <ul>
     *   <li>{@link #min(BigDecimal)}</li>
     *   <li>{@link #min(BigDecimal)}</li>
     *   <li>{@link #range(BigDecimal, BigDecimal)}</li>
     * </ul>
     *
     * <p>If this method is combined with any of the above,
     * the last method takes precedence.
     *
     * <pre>{@code
     * // Example 1: range() takes precedence as it is specified last
     * BigDecimal result = Gen.math().bigDecimal()
     *     .precision(5)
     *     .range(BigDecimal.ZERO, BigDecimal.ONE)
     *     .get();
     *
     * // Sample output: 0.81
     *
     * // Example 2: precision() takes precedence as it is specified last
     * BigDecimal result = Gen.math().bigDecimal()
     *     .range(BigDecimal.ZERO, BigDecimal.ONE)
     *     .precision(5)
     *     .get();
     *
     * // Sample output: 394.19
     * }</pre>
     *
     * @param precision the precision of generated {@code BigDecimal} values
     * @return spec builder
     * @since 3.3.0
     */
    @ExperimentalApi
    BigDecimalGeneratorSpec precision(int precision);
}

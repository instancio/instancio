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
     * <p>Note that if {@link #precision(int)} is specified, then
     * invoking {@link #min(BigDecimal)} or {@link #max(BigDecimal)}
     * will have no effect on generated values.
     *
     * @see #precision(int)
     */
    @Override
    BigDecimalGeneratorSpec min(BigDecimal min);

    /**
     * {@inheritDoc}
     *
     * <p>Note that if {@link #precision(int)} is specified, then
     * invoking {@link #min(BigDecimal)} or {@link #max(BigDecimal)}
     * will have no effect on generated values.
     *
     * @see #precision(int)
     */
    @Override
    BigDecimalGeneratorSpec max(BigDecimal max);

    /**
     * {@inheritDoc}
     *
     * <p>Note that if {@link #precision(int)} is specified, then
     * invoking this method will have no effect on generated values.
     *
     * @see #precision(int)
     */
    @Override
    BigDecimalGeneratorSpec range(BigDecimal min, BigDecimal max);

    @Override
    BigDecimalGeneratorSpec nullable();

    /**
     * Scale of the generated {@link BigDecimal}.
     * Generated values will have given {@code scale}
     * as returned by {@link BigDecimal#scale()}
     *
     * @param scale the scale of generated {@code BigDecimal} values
     * @return spec builder
     * @since 1.5.4
     */
    BigDecimalGeneratorSpec scale(int scale);

    /**
     * Precision of the generated {@link BigDecimal}.
     * Generated values will have given {@code precision}
     * as returned by {@link BigDecimal#precision()}
     *
     * <p>Note that if this method is invoked, then the following
     * methods will have no effect on generated values:
     *
     * <ul>
     *   <li>{@link #min(BigDecimal)}</li>
     *   <li>{@link #min(BigDecimal)}</li>
     *   <li>{@link #range(BigDecimal, BigDecimal)}</li>
     * </ul>
     *
     * @param precision the precision of generated {@code BigDecimal} values
     * @return spec builder
     * @since 3.3.0
     */
    @ExperimentalApi
    BigDecimalGeneratorSpec precision(int precision);
}

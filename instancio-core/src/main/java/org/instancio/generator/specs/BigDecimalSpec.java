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

import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.ValueSpec;

import java.math.BigDecimal;

/**
 * Spec for generating {@link BigDecimal} values.
 *
 * @since 2.6.0
 */
public interface BigDecimalSpec extends ValueSpec<BigDecimal>, BigDecimalGeneratorSpec {

    /**
     * {@inheritDoc}
     *
     * <p>Note that this method is incompatible with {@link #precision(int)}.
     * For details, see {@link #precision(int)} javadoc.
     */
    @Override
    BigDecimalSpec min(BigDecimal min);

    /**
     * {@inheritDoc}
     *
     * <p>Note that this method is incompatible with {@link #precision(int)}.
     * For details, see {@link #precision(int)} javadoc.
     */
    @Override
    BigDecimalSpec max(BigDecimal max);

    /**
     * {@inheritDoc}
     *
     * <p>Note that this method is incompatible with {@link #precision(int)}.
     * For details, see {@link #precision(int)} javadoc.
     */
    @Override
    BigDecimalSpec range(BigDecimal min, BigDecimal max);

    /**
     * {@inheritDoc}
     *
     * @since 2.6.0
     */
    @Override
    BigDecimalSpec nullable();

    /**
     * {@inheritDoc}
     *
     * @since 2.6.0
     */
    @Override
    BigDecimalSpec scale(int scale);

    /**
     * {@inheritDoc}
     *
     * @since 3.3.0
     */
    @Override
    @ExperimentalApi
    BigDecimalSpec precision(int precision);
}

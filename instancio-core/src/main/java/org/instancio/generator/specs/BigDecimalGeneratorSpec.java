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

import java.math.BigDecimal;

/**
 * Generator spec for {@link BigDecimal}.
 *
 * @since 1.5.4
 */
public interface BigDecimalGeneratorSpec extends NumberGeneratorSpec<BigDecimal> {

    @Override
    BigDecimalGeneratorSpec min(BigDecimal min);

    @Override
    BigDecimalGeneratorSpec max(BigDecimal max);

    @Override
    BigDecimalGeneratorSpec range(BigDecimal min, BigDecimal max);

    @Override
    BigDecimalGeneratorSpec nullable();

    @Override
    BigDecimalSpec nullable(boolean isNullable);

    /**
     * Scale of the generated {@link BigDecimal}.
     *
     * @param scale to set
     * @return spec builder
     * @since 1.5.4
     */
    BigDecimalGeneratorSpec scale(int scale);

}

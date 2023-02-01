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

import org.instancio.generator.ValueSpec;

import java.math.BigDecimal;

/**
 * Spec for generating {@link BigDecimal} values.
 *
 * @since 2.6.0
 */
public interface BigDecimalSpec extends ValueSpec<BigDecimal>, BigDecimalGeneratorSpec {

    @Override
    BigDecimalSpec min(BigDecimal min);

    @Override
    BigDecimalSpec max(BigDecimal max);

    @Override
    BigDecimalSpec range(BigDecimal min, BigDecimal max);

    @Override
    BigDecimalSpec nullable();

    @Override
    BigDecimalSpec nullable(boolean isNullable);

    @Override
    BigDecimalSpec scale(int scale);
}

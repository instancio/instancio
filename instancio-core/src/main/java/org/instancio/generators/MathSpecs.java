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
package org.instancio.generators;

import org.instancio.generator.specs.BigDecimalSpec;
import org.instancio.generator.specs.BigIntegerSpec;

/**
 * Provides generators for {@code java.math} classes.
 *
 * @since 5.0.0
 */
public interface MathSpecs extends MathGenerators {

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    BigDecimalSpec bigDecimal();

    /**
     * {@inheritDoc}
     *
     * @since 5.0.0
     */
    @Override
    BigIntegerSpec bigInteger();
}

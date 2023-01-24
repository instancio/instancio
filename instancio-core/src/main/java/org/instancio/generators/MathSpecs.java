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
package org.instancio.generators;

import org.instancio.generator.specs.BigDecimalSpec;
import org.instancio.generator.specs.BigIntegerSpec;
import org.instancio.internal.generator.math.BigDecimalGenerator;
import org.instancio.internal.generator.math.BigIntegerGenerator;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Provides generators for {@code java.math} classes.
 *
 * @since 2.6.0
 */
public final class MathSpecs {

    /**
     * Generates {@link BigDecimal} values.
     *
     * @return API builder reference
     * @since 2.6.0
     */
    public BigDecimalSpec bigDecimal() {
        return new BigDecimalGenerator();
    }

    /**
     * Generates {@link BigInteger} values.
     *
     * @return API builder reference
     * @since 2.6.0
     */
    public BigIntegerSpec bigInteger() {
        return new BigIntegerGenerator();
    }
}

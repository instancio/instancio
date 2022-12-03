/*
 * Copyright 2022 the original author or authors.
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

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.BigDecimalGeneratorSpec;
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.internal.generator.math.BigDecimalGenerator;
import org.instancio.internal.generator.math.BigIntegerGenerator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains built-in generators for {@code java.math} classes.
 */
public class MathGenerators {

    private final GeneratorContext context;

    MathGenerators(final GeneratorContext context) {
        this.context = context;
    }

    static Map<Class<?>, String> getApiMethods() {
        Map<Class<?>, String> map = new HashMap<>();
        map.put(BigIntegerGenerator.class, "bigInteger()");
        map.put(BigDecimalGenerator.class, "bigDecimal()");
        return map;
    }

    /**
     * Customises generated {@link BigInteger} values.
     *
     * @return customised generator
     */
    public NumberGeneratorSpec<BigInteger> bigInteger() {
        return new BigIntegerGenerator(context);
    }

    /**
     * Customises generated {@link BigDecimal} values.
     *
     * @return customised generator
     */
    public BigDecimalGeneratorSpec bigDecimal() {
        return new BigDecimalGenerator(context);
    }
}

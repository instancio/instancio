/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.generator;

import org.instancio.generator.array.ArrayGenerator;
import org.instancio.generator.lang.BooleanGenerator;
import org.instancio.generator.lang.ByteGenerator;
import org.instancio.generator.lang.CharacterGenerator;
import org.instancio.generator.lang.DoubleGenerator;
import org.instancio.generator.lang.FloatGenerator;
import org.instancio.generator.lang.IntegerGenerator;
import org.instancio.generator.lang.LongGenerator;
import org.instancio.generator.lang.ShortGenerator;
import org.instancio.generator.lang.StringGenerator;
import org.instancio.generator.math.BigDecimalGenerator;
import org.instancio.generator.math.BigIntegerGenerator;
import org.instancio.generator.time.LocalDateGenerator;
import org.instancio.generator.time.LocalDateTimeGenerator;
import org.instancio.generator.util.CollectionGeneratorSpecImpl;
import org.instancio.generator.util.MapGeneratorSpecImpl;
import org.instancio.generator.util.concurrent.atomic.AtomicIntegerGenerator;
import org.instancio.generator.util.concurrent.atomic.AtomicLongGenerator;

import java.util.HashMap;
import java.util.Map;

public class GeneratorSpecDescription {

    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends GeneratorSpec>, String> SPECS = new HashMap<>();

    static {
        SPECS.put(ByteGenerator.class, "bytes()");
        SPECS.put(ShortGenerator.class, "shorts()");
        SPECS.put(IntegerGenerator.class, "ints()");
        SPECS.put(LongGenerator.class, "longs()");
        SPECS.put(FloatGenerator.class, "floats()");
        SPECS.put(DoubleGenerator.class, "doubles()");
        SPECS.put(CharacterGenerator.class, "chars()");
        SPECS.put(BooleanGenerator.class, "booleans()");
        SPECS.put(StringGenerator.class, "strings()");

        SPECS.put(AtomicIntegerGenerator.class, "atomicInteger()");
        SPECS.put(AtomicLongGenerator.class, "atomicLong()");
        SPECS.put(BigIntegerGenerator.class, "bigInteger()");
        SPECS.put(BigDecimalGenerator.class, "bigDecimal()");

        SPECS.put(LocalDateGenerator.class, "localDate()");
        SPECS.put(LocalDateTimeGenerator.class, "localDateTime()");

        SPECS.put(ArrayGenerator.class, "array()");
        SPECS.put(CollectionGeneratorSpecImpl.class, "collection()");
        SPECS.put(MapGeneratorSpecImpl.class, "map()");
    }

    public static String get(final Class<?> klass) {
        return SPECS.get(klass);
    }

    private GeneratorSpecDescription() {
        // non-instantiable
    }
}

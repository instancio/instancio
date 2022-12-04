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
package org.example.spi;

import org.example.generator.CustomIntegerGenerator;
import org.example.generator.CustomPersonGenerator;
import org.example.generator.LongSequenceGenerator;
import org.instancio.generator.Generator;
import org.instancio.spi.GeneratorProvider;
import org.instancio.test.support.pojo.person.Person;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CustomGeneratorProvider implements GeneratorProvider {

    public static final String STRING_GENERATOR_VALUE = "overridden string generator from GeneratorProvider SPI!";
    public static final Pattern PATTERN_GENERATOR_VALUE = Pattern.compile("foo");

    @Override
    public Map<Class<?>, Generator<?>> getGenerators() {
        final Map<Class<?>, Generator<?>> map = new HashMap<>();
        map.put(String.class, random -> STRING_GENERATOR_VALUE);
        map.put(Pattern.class, random -> PATTERN_GENERATOR_VALUE);
        map.put(Person.class, new CustomPersonGenerator());
        map.put(int.class, new CustomIntegerGenerator());
        map.put(Integer.class, new CustomIntegerGenerator());
        map.put(Long.class, new LongSequenceGenerator()); // primitive using default generator
        return map;
    }
}

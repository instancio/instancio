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
package org.example.spi;

import org.example.generator.CustomIntegerGenerator;
import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.spi.InstancioServiceProvider;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.pojo.misc.getters.BeanStylePojo;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CustomGeneratorProvider implements InstancioServiceProvider {

    public static final String STRING_GENERATOR_VALUE = "overridden string generator from SPI Generator!";
    public static final Pattern PATTERN_GENERATOR_VALUE = Pattern.compile("baz");

    private static final Map<Class<?>, Class<?>> GENERATOR_MAP = new HashMap<Class<?>, Class<?>>() {{
        put(String.class, StringGeneratorFromSpi.class);
        put(Pattern.class, PatternGeneratorFromSpi.class);
        put(int.class, CustomIntegerGenerator.class);
        put(Integer.class, CustomIntegerGenerator.class);

        // Constructor precedence
        put(StringHolder.class, GeneratorWithGeneratorContextConstructor.class);

        // Error handling: Foo is mapped to a non-generator class
        put(Foo.class, Bar.class);

        // Error handling: constructor throws an exception
        put(StringFields.class, GeneratorWithConstructorThatThrowsException.class);

        // Error handling: generator without any of the expected constructors
        put(BeanStylePojo.class, GeneratorWithoutExpectedConstructors.class);
    }};

    private boolean getGeneratorProviderInvoked;

    @Override
    public GeneratorProvider getGeneratorProvider() {
        if (getGeneratorProviderInvoked) {
            throw new AssertionError("getGeneratorProvider() should not be invoked more than once!");
        }

        getGeneratorProviderInvoked = true;
        return GENERATOR_MAP::get;
    }

    private static class StringGeneratorFromSpi implements Generator<String> {
        @Override
        public String generate(final Random random) {
            return STRING_GENERATOR_VALUE;
        }
    }

    private static class PatternGeneratorFromSpi implements Generator<Pattern> {
        @Override
        public Pattern generate(final Random random) {
            return PATTERN_GENERATOR_VALUE;
        }
    }

    /**
     * Constructor with {@link GeneratorContext} parameter should take
     * precedence over the default constructor.
     */
    public static class GeneratorWithGeneratorContextConstructor implements Generator<StringHolder> {

        private final String invokedConstructor;

        /**
         * Should not be called, but is required to verify the behaviour.
         */
        @SuppressWarnings("unused")
        public GeneratorWithGeneratorContextConstructor() {
            throw new AssertionError("Default constructor is not expected to be called!");
        }

        /**
         * Called via reflection.
         */
        @SuppressWarnings("unused")
        public GeneratorWithGeneratorContextConstructor(final GeneratorContext context) {
            invokedConstructor = "withGeneratorContextConstructor";
        }

        @Override
        public StringHolder generate(final Random random) {
            return new StringHolder(invokedConstructor);
        }
    }

    /**
     * Errors thrown when instantiating a generator should propagate all the way up.
     */
    public static class GeneratorWithConstructorThatThrowsException implements Generator<StringFields> {

        GeneratorWithConstructorThatThrowsException() {
            throw new RuntimeException("Expected error from generator constructor");
        }

        @Override
        public StringFields generate(final Random random) {
            throw new AssertionError("generate() is not expected to be called!");
        }
    }

    /**
     * This generator class does not define any of the expected constructors.
     */
    public static class GeneratorWithoutExpectedConstructors implements Generator<BeanStylePojo> {

        @SuppressWarnings("unused")
        GeneratorWithoutExpectedConstructors(String someArg) {
            throw new AssertionError("constructor is not expected to be called!");
        }

        @Override
        public BeanStylePojo generate(final Random random) {
            throw new AssertionError("generate() is not expected to be called!");
        }
    }
}
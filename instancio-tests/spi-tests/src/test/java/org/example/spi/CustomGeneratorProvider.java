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

import org.example.FooRecord;
import org.example.generator.CustomIntegerGenerator;
import org.instancio.Gen;
import org.instancio.Node;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.Hints;
import org.instancio.generators.Generators;
import org.instancio.internal.generator.sequence.IntegerSequenceGenerator;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.spi.InstancioServiceProvider;
import org.instancio.spi.ServiceProviderContext;
import org.instancio.test.support.pojo.dynamic.DynPhone;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.PersonName;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.PhoneWithType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CustomGeneratorProvider implements InstancioServiceProvider {

    public static final String STRING_GENERATOR_VALUE = "overridden string generator from SPI Generator!";
    public static final String FOO_RECORD_VALUE = "expected-foo-value";
    public static final Pattern PATTERN_GENERATOR_VALUE = Pattern.compile("baz");

    /**
     * A static generator instance for verifying {@link Generator#init(GeneratorContext)}
     * invocation counts. Should only be used by {@code SpiGeneratorInitTest}.
     */
    public static final InitCountingPojoGenerator INIT_COUNTING_GENERATOR = new InitCountingPojoGenerator();

    public static class InitCountingPojo {
        // empty
    }

    private static final Map<Class<?>, GeneratorSpec<?>> GENERATOR_MAP = new HashMap<>() {{
        put(String.class, new StringGeneratorFromSpi());
        put(Pattern.class, new PatternGeneratorFromSpi());
        put(int.class, new CustomIntegerGenerator());
        put(Integer.class, new CustomIntegerGenerator());
        put(byte.class, new IntegerSequenceGenerator().as(Integer::byteValue));
        put(Byte.class, new IntegerSequenceGenerator().as(Integer::byteValue));
        put(Address.class, new CustomAddressGenerator());
        put(Phone.class, new CustomPhoneGenerator());
        put(Field.class, new ExceptionThrowingGenerator());
        put(FooRecord.class, (Generator<?>) random -> new FooRecord(FOO_RECORD_VALUE));
        put(InitCountingPojo.class, INIT_COUNTING_GENERATOR);

        // Error-handling: generator spec does not implement the Generator interface
        put(Float.class, new CustomFloatSpec());
    }};

    private final GeneratorProvider generatorProvider = new GeneratorProviderImpl();
    private boolean getGeneratorProviderInvoked;
    private int initInvocationCount;

    @Override
    public void init(final ServiceProviderContext context) {
        initInvocationCount++;

        assertThat(initInvocationCount).isLessThanOrEqualTo(1);
        assertThat(context).isNotNull();
        assertThat(context.random()).isNotNull();

        final Settings settings = context.getSettings();
        assertThatThrownBy(() -> settings.set(Keys.STRING_MIN_LENGTH, 1))
                .isExactlyInstanceOf(UnsupportedOperationException.class)
                .hasMessage("This instance of Settings has been locked and is read-only");
    }

    @Override
    public GeneratorProvider getGeneratorProvider() {
        if (getGeneratorProviderInvoked) {
            throw new AssertionError("getGeneratorProvider() should not be invoked more than once!");
        }

        getGeneratorProviderInvoked = true;
        return generatorProvider;
    }

    private static class GeneratorProviderImpl implements GeneratorProvider {
        @Override
        public GeneratorSpec<?> getGenerator(final Node node, final Generators generators) {
            if (node.getField() != null) {
                // Set string length based on annotation attributes
                final PersonName name = node.getField().getAnnotation(PersonName.class);
                if (name != null) {
                    return generators.string()
                            .minLength(name.min())
                            .maxLength(name.max());
                }
            }
            if (node.getSetter() != null) {
                final Method setter = node.getSetter();

                if (setter.getDeclaringClass() == DynPhone.class && setter.getName().equals("setCountryCode")) {
                    return generators.string().prefix("+").digits().length(2);
                }
            }

            // verify: subtype(all(Phone.class),  PhoneWithType.class)
            if (node.getTargetClass() == PhoneWithType.class) {
                return new PhoneWithTypeGenerator();
            }
            return GENERATOR_MAP.get(node.getTargetClass());
        }
    }

    private static class StringGeneratorFromSpi implements org.instancio.generator.Generator<String> {
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

    public static class CustomAddressGenerator implements Generator<Address> {
        public static final String COUNTRY = "foo";

        @Override
        public Address generate(final Random random) {
            return Address.builder().country(COUNTRY).build();
        }

        @Override
        public Hints hints() {
            return Hints.afterGenerate(AfterGenerate.APPLY_SELECTORS);
        }
    }

    public static class CustomPhoneGenerator implements Generator<Phone> {
        public static final String NUMBER = Gen.string().digits().get();

        @Override
        public Phone generate(final Random random) {
            return Phone.builder().number(NUMBER).build();
        }
    }

    public static class PhoneWithTypeGenerator implements Generator<PhoneWithType> {
        public static final String NUMBER = Gen.string().digits().get();

        @Override
        public PhoneWithType generate(final Random random) {
            return PhoneWithType.builder().number(NUMBER).build();
        }
    }

    public static class CustomFloatSpec implements GeneratorSpec<Float> {}

    public static class InitCountingPojoGenerator implements Generator<InitCountingPojo> {

        private int initCount;

        public int getInitCount() {
            return initCount;
        }

        @Override
        public void init(final GeneratorContext context) {
            initCount++;
        }

        @Override
        public InitCountingPojo generate(final Random random) {
            return new InitCountingPojo();
        }
    }

    public static class ExceptionThrowingGenerator implements Generator<Field> {
        @Override
        public Field generate(final Random random) {
            throw new RuntimeException("Expected error from SPI generator");
        }
    }
}
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
package org.instancio.test.contract;

import org.instancio.generator.ValueSpec;
import org.instancio.generator.specs.BigDecimalSpec;
import org.instancio.generator.specs.BigIntegerSpec;
import org.instancio.generator.specs.BooleanGeneratorSpec;
import org.instancio.generator.specs.BooleanSpec;
import org.instancio.generator.specs.ByteSpec;
import org.instancio.generator.specs.CharacterGeneratorSpec;
import org.instancio.generator.specs.CharacterSpec;
import org.instancio.generator.specs.DoubleSpec;
import org.instancio.generator.specs.FileSpec;
import org.instancio.generator.specs.FloatSpec;
import org.instancio.generator.specs.InstantSpec;
import org.instancio.generator.specs.IntegerSpec;
import org.instancio.generator.specs.LocalDateSpec;
import org.instancio.generator.specs.LocalDateTimeSpec;
import org.instancio.generator.specs.LocalTimeSpec;
import org.instancio.generator.specs.LongSpec;
import org.instancio.generator.specs.LoremIpsumGeneratorSpec;
import org.instancio.generator.specs.LoremIpsumSpec;
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.generator.specs.OffsetDateTimeSpec;
import org.instancio.generator.specs.OffsetTimeSpec;
import org.instancio.generator.specs.PathGeneratorSpec;
import org.instancio.generator.specs.PathSpec;
import org.instancio.generator.specs.ShortSpec;
import org.instancio.generator.specs.StringGeneratorSpec;
import org.instancio.generator.specs.StringSpec;
import org.instancio.generator.specs.TemporalGeneratorSpec;
import org.instancio.generator.specs.URIGeneratorSpec;
import org.instancio.generator.specs.URISpec;
import org.instancio.generator.specs.URLGeneratorSpec;
import org.instancio.generator.specs.URLSpec;
import org.instancio.generator.specs.UUIDStringGeneratorSpec;
import org.instancio.generator.specs.UUIDStringSpec;
import org.instancio.generator.specs.YearMonthSpec;
import org.instancio.generator.specs.YearSpec;
import org.instancio.generator.specs.ZonedDateTimeSpec;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that interface subtypes of {@link ValueSpec} override methods in generator
 * specs to return the subtype interface class. This ensures consistent return
 * types from the API, for example:
 *
 * <pre>{@code
 *   // Good: want to return the specific spec type (should pass the test)
 *   ZonedDateTimeSpec spec = ints().range(0, 9);
 *
 *   // Bad: returns the parent spec (should fail the test)
 *   TemporalSpec<ZonedDateTime> spec = ints().range(0, 9);
 * }</pre>
 */
class ValueSpecSubtypesOverrideMethodsTest {

    @Test
    void booleanSpec() {
        assertSpecSubtypeOverrides(BooleanGeneratorSpec.class, BooleanSpec.class);
    }

    @Test
    void characterSpec() {
        assertSpecSubtypeOverrides(CharacterGeneratorSpec.class, CharacterSpec.class);
    }

    @Test
    void numericSpec() {
        assertSpecSubtypeOverrides(NumberGeneratorSpec.class,
                BigDecimalSpec.class,
                BigIntegerSpec.class,
                ByteSpec.class,
                DoubleSpec.class,
                FloatSpec.class,
                IntegerSpec.class,
                LongSpec.class,
                ShortSpec.class
        );
    }

    @Test
    void pathSpec() {
        assertSpecSubtypeOverrides(PathGeneratorSpec.class, PathSpec.class, FileSpec.class);
    }

    @Test
    void stringSpec() {
        assertSpecSubtypeOverrides(StringGeneratorSpec.class, StringSpec.class);
    }

    @Test
    void temporalSpec() {
        assertSpecSubtypeOverrides(TemporalGeneratorSpec.class,
                InstantSpec.class,
                LocalDateSpec.class,
                LocalDateTimeSpec.class,
                LocalTimeSpec.class,
                OffsetDateTimeSpec.class,
                OffsetTimeSpec.class,
                YearMonthSpec.class,
                YearSpec.class,
                ZonedDateTimeSpec.class
        );
    }

    @Test
    void text() {
        assertSpecSubtypeOverrides(LoremIpsumGeneratorSpec.class, LoremIpsumSpec.class);
        assertSpecSubtypeOverrides(UUIDStringGeneratorSpec.class, UUIDStringSpec.class);
    }

    @Test
    void net() {
        assertSpecSubtypeOverrides(URIGeneratorSpec.class, URISpec.class);
        assertSpecSubtypeOverrides(URLGeneratorSpec.class, URLSpec.class);
    }

    private void assertSpecSubtypeOverrides(final Class<?> generatorSpec, final Class<?>... specSubtypes) {

        for (Class<?> specSubtype : specSubtypes) {

            // The spec subtype should either
            //  - override generator spec's methods with itself as the return type
            //  - or, if it's a terminal method, it should return ValueSpec
            final Map<String, Method> methods = getSpecMethodsThatAreTerminalOrReturnSelf(specSubtype);

            final Method[] genSpecMethods = generatorSpec.getDeclaredMethods();

            for (Method genSpecMethod : genSpecMethods) {

                if (genSpecMethod.getName().toLowerCase(Locale.ROOT).contains("jacoco")) {
                    // Jacoco modifies classes when tests are run via Maven
                    // Ignore any methods added by Jacoco
                    continue;
                }

                final Method specSubtypeMethod = methods.get(toMethodKey(genSpecMethod));

                assertThat(specSubtypeMethod)
                        .as("Generator spec method '%s' not overridden by %s",
                                genSpecMethod.getName(), specSubtype.getSimpleName())
                        .isNotNull();
            }
        }
    }

    private Map<String, Method> getSpecMethodsThatAreTerminalOrReturnSelf(final Class<?> klass) {
        assertThat(klass).isAssignableTo(ValueSpec.class);

        return Arrays.stream(klass.getDeclaredMethods())
                .filter(m -> m.getReturnType().equals(klass) || m.getReturnType().equals(ValueSpec.class))
                .collect(Collectors.toMap(this::toMethodKey, Function.identity()));
    }

    private String toMethodKey(final Method method) {
        // NOTE: this assumes that method name and
        // parameter count combination is unique per class
        return method.getName() + method.getParameters().length;
    }
}

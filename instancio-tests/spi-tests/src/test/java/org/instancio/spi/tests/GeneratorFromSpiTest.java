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
package org.instancio.spi.tests;

import org.example.generator.CustomIntegerGenerator;
import org.example.spi.CustomGeneratorProvider;
import org.example.spi.CustomGeneratorProvider.GeneratorWithConstructorThatThrowsException;
import org.example.spi.CustomGeneratorProvider.GeneratorWithGeneratorContextConstructor;
import org.example.spi.CustomGeneratorProvider.GeneratorWithoutExpectedConstructors;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.generator.Generator;
import org.instancio.spi.InstancioSpiException;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.pojo.misc.getters.BeanStylePojo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;

class GeneratorFromSpiTest {

    @Test
    void overrideBuiltInGenerator() {
        assertThat(Instancio.create(String.class))
                .isEqualTo(CustomGeneratorProvider.STRING_GENERATOR_VALUE);
    }

    @Test
    void defineNewGenerator() {
        assertThat(Instancio.create(Pattern.class))
                .isSameAs(CustomGeneratorProvider.PATTERN_GENERATOR_VALUE);
    }

    @Test
    void shouldUseCustomIntegerGenerator() {
        assertThat(Instancio.create(int.class))
                .isBetween(CustomIntegerGenerator.MIN, CustomIntegerGenerator.MAX);
    }

    /**
     * @see GeneratorWithGeneratorContextConstructor
     */
    @Test
    void shouldPreferConstructorWithGeneratorContextParameter() {
        final StringHolder result = Instancio.create(StringHolder.class);
        assertThat(result.getValue()).isEqualTo("withGeneratorContextConstructor");
    }

    /**
     * @see GeneratorWithConstructorThatThrowsException
     */
    @Test
    void shouldPropagateErrorIfInstantiatingGeneratorFails() {
        assertThatThrownBy(() -> Instancio.create(StringFields.class))
                .isExactlyInstanceOf(InstancioSpiException.class)
                .hasMessage("Error instantiating generator %s", GeneratorWithConstructorThatThrowsException.class)
                .hasRootCauseExactlyInstanceOf(RuntimeException.class)
                .hasRootCauseMessage("Expected error from generator constructor");
    }

    /**
     * @see GeneratorWithoutExpectedConstructors
     */
    @Test
    void shouldThrowErrorIfGeneratorHasNoneOfTheExpectedConstructors() {
        assertThatThrownBy(() -> Instancio.create(BeanStylePojo.class))
                .isExactlyInstanceOf(InstancioSpiException.class)
                .hasMessage(String.format("%n" +
                        "Generator class:%n" +
                        " -> %s%n" +
                        "does not define any of the expected constructors:%n" +
                        " -> constructor with GeneratorContext as the only argument, or%n" +
                        " -> default no-argument constructor", GeneratorWithoutExpectedConstructors.class.getName()))
                .hasNoCause();
    }

    @Test
    void shouldThrowErrorIfClassIsNotAGenerator() {
        final TypeToken<Foo<String>> type = new TypeToken<Foo<String>>() {};

        assertThatThrownBy(() -> Instancio.create(type))
                .isExactlyInstanceOf(InstancioSpiException.class)
                .hasMessage(String.format(
                        "%s returned an invalid generator class:%n" +
                                " -> org.instancio.test.support.pojo.generics.foobarbaz.Bar%n" +
                                "The class does not implement the interface %s interface",
                        CustomGeneratorProvider.class, Generator.class.getName()))
                .hasNoCause();
    }

    @Test
    @DisplayName("Having overridden int generator, should still be able to use built-in generators, if needed")
    void builtInGeneratorStillAvailableAfterOverride() {
        final int result = Instancio.of(int.class)
                .generate(allInts(), gen -> gen.ints().range(100, 105))
                .create();

        assertThat(result).isBetween(100, 105);
    }

    @Test
    void customGeneratorTakesPrecedenceOverBuiltInt() {
        final int expectedSize = 1000;
        final List<Integer> result = Instancio.of(new TypeToken<List<Integer>>() {})
                .supply(allInts(), new CustomIntegerGenerator().evenNumbers())
                // should be ignored, custom has higher precedence
                .generate(allInts(), gen -> gen.ints().range(100, 105))
                .generate(all(List.class), gen -> gen.collection().size(expectedSize))
                .create();

        assertThat(result)
                .hasSize(expectedSize)
                .allSatisfy(n -> assertThat(n)
                        .isEven()
                        .isBetween(CustomIntegerGenerator.MIN, CustomIntegerGenerator.MAX));
    }
}

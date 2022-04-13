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
package org.instancio.test.features.validation;

import org.instancio.Generators;
import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.arrays.TwoArraysOfItemString;
import org.instancio.test.support.pojo.arrays.object.WithIntegerArray;
import org.instancio.test.support.pojo.arrays.primitive.WithIntArray;
import org.instancio.test.support.pojo.basic.BooleanHolder;
import org.instancio.test.support.pojo.basic.CharacterHolder;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.basic.SupportedNumericTypes;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Bindings.all;
import static org.instancio.Bindings.allInts;

@FeatureTag(Feature.VALIDATION)
@ExtendWith(InstancioExtension.class)
class GeneratorMismatchTest {

    @Test
    @DisplayName("Full error message for reference")
    void fullErrorMessage() {
        assertThatThrownBy(() -> Instancio.of(SupportedNumericTypes.class)
                .generate(allInts(), Generators::doubles)
                .create())
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll(
                        "Generator type mismatch:\n",
                        "Method 'doubles()' cannot be used for type: int\n",
                        "Field: private int org.instancio.test.support.pojo.basic.SupportedNumericTypes.primitiveInt");
    }

    @Test
    void assertNumericTypes() {
        final Class<?> createClass = SupportedNumericTypes.class;
        assertMessageContains(createClass, short.class, "bytes()", Generators::bytes);
        assertMessageContains(createClass, BigDecimal.class, "shorts()", Generators::shorts);
        assertMessageContains(createClass, long.class, "ints()", Generators::ints);
        assertMessageContains(createClass, double.class, "longs()", Generators::longs);
        assertMessageContains(createClass, int.class, "floats()", Generators::floats);
        assertMessageContains(createClass, BigInteger.class, "doubles()", Generators::doubles);
        assertMessageContains(createClass, byte.class, "bigDecimal()", Generators::bigDecimal);
        assertMessageContains(createClass, long.class, "bigInteger()", Generators::bigInteger);
    }

    @Test
    void assertString() {
        assertMessageContains(StringHolder.class, String.class, "ints()", Generators::ints);
    }

    @Test
    void assertBoolean() {
        assertMessageContains(BooleanHolder.class, boolean.class, "ints()", Generators::ints);
    }

    @Test
    void assertCharacter() {
        assertMessageContains(CharacterHolder.class, char.class, "ints()", Generators::ints);
    }

    @Test
    void assertArrays() {
        assertMessageContains(WithIntArray.class, int[].class, "string()", Generators::string);
        assertMessageContains(WithIntegerArray.class, Integer[].class, "string()", Generators::string);
        assertMessageContains(TwoArraysOfItemString.class, Item[].class, "string()", Generators::string);
    }

    private static <T> void assertMessageContains(final Class<?> typeToCreate,
                                                  final Class<?> bindingType,
                                                  final String expectedGeneratorMethod,
                                                  final Function<Generators, GeneratorSpec<T>> genFn) {

        assertThatThrownBy(() -> Instancio.of(typeToCreate)
                .generate(all(bindingType), genFn)
                .create())
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Generator type mismatch:\n",
                        String.format("Method '%s' cannot be used for type: %s\n",
                                expectedGeneratorMethod, bindingType.getCanonicalName()));
    }

}

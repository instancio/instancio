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

import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.arrays.TwoArraysOfItemString;
import org.instancio.test.support.pojo.arrays.object.WithIntegerArray;
import org.instancio.test.support.pojo.arrays.primitive.WithIntArray;
import org.instancio.test.support.pojo.basic.CharacterHolder;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.basic.SupportedAtomicTypes;
import org.instancio.test.support.pojo.basic.SupportedMathTypes;
import org.instancio.test.support.pojo.basic.SupportedNumericTypes;
import org.instancio.test.support.pojo.basic.SupportedTemporalTypes;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Year;
import java.time.YearMonth;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;

@FeatureTag(Feature.VALIDATION)
@ExtendWith(InstancioExtension.class)
class GeneratorMismatchTest {

    @Test
    @DisplayName("Full error message for reference")
    void fullErrorMessage() {
        assertThatThrownBy(() -> Instancio.of(SupportedNumericTypes.class)
                .generate(all(int.class), Generators::doubles)
                .create())
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll(
                        "Generator type mismatch:",
                        "Method 'doubles()' cannot be used for type: int",
                        "Field: private int org.instancio.test.support.pojo.basic.SupportedNumericTypes.primitiveInt");
    }

    @Test
    void assertNumericTypes() {
        assertMessageContains(SupportedNumericTypes.class, short.class, "bytes()", Generators::bytes);
        assertMessageContains(SupportedNumericTypes.class, long.class, "ints()", Generators::ints);
        assertMessageContains(SupportedNumericTypes.class, double.class, "longs()", Generators::longs);
        assertMessageContains(SupportedNumericTypes.class, int.class, "floats()", Generators::floats);
        assertMessageContains(SupportedNumericTypes.class, byte.class, "bigDecimal()", gen -> gen.math().bigDecimal());
        assertMessageContains(SupportedNumericTypes.class, long.class, "bigInteger()", gen -> gen.math().bigInteger());
    }

    @Test
    void assertMathTypes() {
        assertMessageContains(SupportedMathTypes.class, BigDecimal.class, "shorts()", Generators::shorts);
        assertMessageContains(SupportedMathTypes.class, BigInteger.class, "doubles()", Generators::doubles);
    }

    @Test
    void assertAtomicTypes() {
        assertMessageContains(SupportedAtomicTypes.class, AtomicLong.class, "atomicInteger()", gen -> gen.atomic().atomicInteger());
        assertMessageContains(SupportedAtomicTypes.class, AtomicInteger.class, "atomicLong()", gen -> gen.atomic().atomicLong());
    }

    @Test
    void assertTemporalTypes() {
        assertMessageContains(SupportedTemporalTypes.class, Year.class, "instant()", gen -> gen.temporal().instant());
        assertMessageContains(SupportedTemporalTypes.class, Year.class, "localTime()", gen -> gen.temporal().localTime());
        assertMessageContains(SupportedTemporalTypes.class, Year.class, "localDate()", gen -> gen.temporal().localDate());
        assertMessageContains(SupportedTemporalTypes.class, Year.class, "localDateTime()", gen -> gen.temporal().localDateTime());
        assertMessageContains(SupportedTemporalTypes.class, Year.class, "zonedDateTime()", gen -> gen.temporal().zonedDateTime());
        assertMessageContains(SupportedTemporalTypes.class, Year.class, "yearMonth()", gen -> gen.temporal().yearMonth());
        assertMessageContains(SupportedTemporalTypes.class, YearMonth.class, "year()", gen -> gen.temporal().year());
        assertMessageContains(SupportedTemporalTypes.class, Year.class, "duration()", gen -> gen.temporal().duration());
        assertMessageContains(SupportedTemporalTypes.class, Year.class, "period()", gen -> gen.temporal().period());
        assertMessageContains(SupportedTemporalTypes.class, Year.class, "date()", gen -> gen.temporal().date());
        assertMessageContains(SupportedTemporalTypes.class, Year.class, "sqlDate()", gen -> gen.temporal().sqlDate());
        assertMessageContains(SupportedTemporalTypes.class, Year.class, "timestamp()", gen -> gen.temporal().timestamp());
        assertMessageContains(SupportedTemporalTypes.class, Year.class, "calendar()", gen -> gen.temporal().calendar());
    }

    @Test
    void assertString() {
        assertMessageContains(StringHolder.class, String.class, "ints()", Generators::ints);
    }

    @Test
    void assertText() {
        assertMessageContains(Person.class, Address.class, "loremIpsum()", gen -> gen.text().loremIpsum());
        assertMessageContains(Person.class, Address.class, "pattern()", gen -> gen.text().pattern("foo"));
        assertMessageContains(Person.class, Address.class, "uuid()", gen -> gen.text().uuid());
    }

    @Test
    void assertBoolean() {
        assertMessageContains(IntegerHolder.class, int.class, "booleans()", Generators::booleans);
    }

    @Test
    void assertCharacter() {
        // Note: there's no GeneratorSpec for chars
        assertMessageContains(CharacterHolder.class, char.class, "ints()", Generators::ints);
    }

    @Test
    void assertEnum() {
        assertMessageContains(CharacterHolder.class, char.class, "enumOf()", gen -> gen.enumOf(Gender.class));
    }

    @Test
    void assertArrays() {
        assertMessageContains(WithIntArray.class, int[].class, "string()", Generators::string);
        assertMessageContains(WithIntegerArray.class, Integer[].class, "string()", Generators::string);
        assertMessageContains(TwoArraysOfItemString.class, Item[].class, "string()", Generators::string);
    }

    private static <T> void assertMessageContains(final Class<?> typeToCreate,
                                                  final Class<?> selectedType,
                                                  final String expectedGeneratorMethod,
                                                  final Function<Generators, GeneratorSpec<T>> genFn) {

        assertThatThrownBy(() -> Instancio.of(typeToCreate)
                .generate(all(selectedType), genFn)
                .create())
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll("Generator type mismatch:",
                        String.format("%nMethod '%s' cannot be used for type: %s%n",
                                expectedGeneratorMethod, selectedType.getCanonicalName()));
    }

}
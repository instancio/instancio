/*
 *  Copyright 2022-2023 the original author or authors.
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

import org.instancio.GeneratorSpecProvider;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.generators.Generators;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.CharacterHolder;
import org.instancio.test.support.pojo.basic.SupportedNumericTypes;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;

@FeatureTag(Feature.VALIDATION)
@ExtendWith(InstancioExtension.class)
class GeneratorMismatchTest {

    private static class TypeToCreate {
        // Can use field of any type that is not compatible with the generators under test
        private CharacterHolder holder;
    }

    @Test
    @DisplayName("Full error message for reference")
    void fullErrorMessage() {
        final InstancioApi<SupportedNumericTypes> api = Instancio.of(SupportedNumericTypes.class)
                .generate(all(int.class), Generators::doubles);

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll(
                        "Generator type mismatch:",
                        "Method 'doubles()' cannot be used for type: int",
                        "Field: private int org.instancio.test.support.pojo.basic.SupportedNumericTypes.primitiveInt");
    }

    @Test
    void assertCollectionTypes() {
        assertMessageContains("collection()", Generators::collection);
        assertMessageContains("map()", Generators::map);
        assertMessageContains("enumSet()", gen -> gen.enumSet(Gender.class));
    }

    @Test
    void assertNumericTypes() {
        assertMessageContains("bytes()", Generators::bytes);
        assertMessageContains("ints()", Generators::ints);
        assertMessageContains("longs()", Generators::longs);
        assertMessageContains("floats()", Generators::floats);
        assertMessageContains("bigDecimal()", gen -> gen.math().bigDecimal());
        assertMessageContains("bigInteger()", gen -> gen.math().bigInteger());
    }

    @Test
    void assertNumericSequences() {
        assertMessageContains("intSeq()", Generators::intSeq);
        assertMessageContains("longSeq()", Generators::longSeq);
    }

    @Test
    void assertMathTypes() {
        assertMessageContains("shorts()", Generators::shorts);
        assertMessageContains("doubles()", Generators::doubles);
    }

    @Test
    void assertNetTypes() {
        assertMessageContains("uri()", gen -> gen.net().uri());
        assertMessageContains("url()", gen -> gen.net().url());
    }

    @Test
    void assertAtomicTypes() {
        assertMessageContains("atomicInteger()", gen -> gen.atomic().atomicInteger());
        assertMessageContains("atomicLong()", gen -> gen.atomic().atomicLong());
    }

    @Test
    void assertIoTypes() {
        assertMessageContains("file()", gen -> gen.io().file());
    }

    @Test
    void assertNioTypes() {
        assertMessageContains("path()", gen -> gen.nio().path());
    }

    @Test
    void assertTemporalTypes() {
        assertMessageContains("instant()", gen -> gen.temporal().instant());
        assertMessageContains("localTime()", gen -> gen.temporal().localTime());
        assertMessageContains("localDate()", gen -> gen.temporal().localDate());
        assertMessageContains("localDateTime()", gen -> gen.temporal().localDateTime());
        assertMessageContains("monthDay()", gen -> gen.temporal().monthDay());
        assertMessageContains("offsetTime()", gen -> gen.temporal().offsetTime());
        assertMessageContains("offsetDateTime()", gen -> gen.temporal().offsetDateTime());
        assertMessageContains("zonedDateTime()", gen -> gen.temporal().zonedDateTime());
        assertMessageContains("yearMonth()", gen -> gen.temporal().yearMonth());
        assertMessageContains("year()", gen -> gen.temporal().year());
        assertMessageContains("duration()", gen -> gen.temporal().duration());
        assertMessageContains("period()", gen -> gen.temporal().period());
        assertMessageContains("date()", gen -> gen.temporal().date());
        assertMessageContains("sqlDate()", gen -> gen.temporal().sqlDate());
        assertMessageContains("timestamp()", gen -> gen.temporal().timestamp());
        assertMessageContains("calendar()", gen -> gen.temporal().calendar());
    }

    @Test
    void assertString() {
        assertMessageContains("string()", Generators::string);
    }

    @Test
    void assertText() {
        assertMessageContains("csv()", gen -> gen.text().csv());
        assertMessageContains("loremIpsum()", gen -> gen.text().loremIpsum());
        assertMessageContains("pattern()", gen -> gen.text().pattern("foo"));
        assertMessageContains("uuid()", gen -> gen.text().uuid());
    }

    @Test
    void assertIdGenerators() {
        assertMessageContains("ean()", gen -> gen.id().ean());
    }

    @Test
    void assertBoolean() {
        assertMessageContains("booleans()", Generators::booleans);
    }

    @Test
    void assertCharacter() {
        assertMessageContains("chars()", Generators::chars);
    }

    @Test
    void assertEnum() {
        assertMessageContains("enumOf()", gen -> gen.enumOf(Gender.class));
    }

    private static <T> void assertMessageContains(
            final String expectedGeneratorMethod,
            final GeneratorSpecProvider<T> specProvider) {

        final Class<?> selectedType = CharacterHolder.class;

        final InstancioApi<TypeToCreate> api = Instancio.of(TypeToCreate.class)
                .generate(all(selectedType), specProvider);

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll("Generator type mismatch:",
                        String.format("%nMethod '%s' cannot be used for type: %s%n",
                                expectedGeneratorMethod, selectedType.getCanonicalName()));
    }
}
/*
 * Copyright 2022-2026 the original author or authors.
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
                        "Reason: the target type is incompatible with the generator",
                        " -> Method 'doubles()' cannot be used for type: int",
                        " -> Field: private int org.instancio.test.support.pojo.basic.SupportedNumericTypes.primitiveInt");
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
        assertMessageContains("word()", gen -> gen.text().word());
        assertMessageContains("wordTemplate()", gen -> gen.text().wordTemplate("example"));
    }

    @Test
    void assertChecksumGenerators() {
        assertMessageContains("luhn()", gen -> gen.checksum().luhn());
        assertMessageContains("mod10()", gen -> gen.checksum().mod10());
        assertMessageContains("mod11()", gen -> gen.checksum().mod11());
    }

    @Test
    void assertIdGenerators() {
        assertMessageContains("ean()", gen -> gen.id().ean());
        assertMessageContains("isbn()", gen -> gen.id().isbn());
    }

    @Test
    void assertCanIdGenerators() {
        assertMessageContains("sin()", gen -> gen.id().can().sin());
    }

    @Test
    void assertPolIdGenerators() {
        assertMessageContains("nip()", gen -> gen.id().pol().nip());
        assertMessageContains("pesel()", gen -> gen.id().pol().pesel());
        assertMessageContains("regon()", gen -> gen.id().pol().regon());
    }

    @Test
    void assertUsaIdGenerators() {
        assertMessageContains("ssn()", gen -> gen.id().usa().ssn());
    }

    @Test
    void assertBraIdGenerators() {
        assertMessageContains("cpf()", gen -> gen.id().bra().cpf());
        assertMessageContains("cnpj()", gen -> gen.id().bra().cnpj());
        assertMessageContains("tituloEleitoral()", gen -> gen.id().bra().tituloEleitoral());
    }

    @Test
    void assertRusIdGenerators() {
        assertMessageContains("inn()", gen -> gen.id().rus().inn());
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

    @Test
    void assertOptional() {
        assertMessageContains("optional()", Generators::optional);
    }

    private static <T> void assertMessageContains(
            final String expectedGeneratorMethod,
            final GeneratorSpecProvider<T> specProvider) {

        final Class<?> selectedType = CharacterHolder.class;

        final InstancioApi<TypeToCreate> api = Instancio.of(TypeToCreate.class)
                .generate(all(selectedType), specProvider);

        assertThatThrownBy(api::create)
                .isInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll("the target type is incompatible with the generator",
                        String.format("%n -> Method '%s' cannot be used for type: %s%n",
                                expectedGeneratorMethod, selectedType.getCanonicalName()));
    }
}
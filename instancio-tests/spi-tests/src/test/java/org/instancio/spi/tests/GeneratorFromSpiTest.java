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

import org.example.FooRecord;
import org.example.generator.CustomIntegerGenerator;
import org.example.spi.CustomGeneratorProvider;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.TypeToken;
import org.instancio.exception.InstancioApiException;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.generator.Generator;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodUnmatched;
import org.instancio.settings.Settings;
import org.instancio.spi.InstancioSpiException;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.pojo.dynamic.DynPhone;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.PersonName;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.PhoneWithType;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.field;

class GeneratorFromSpiTest {

    private static class PersonPojo {
        @PersonName(min = 10, max = 10)
        String name;

        @SuppressWarnings("unused") // used via reflection
        private void setName(final String name) {
            this.name = name;
        }
    }

    @EnumSource(AssignmentType.class)
    @ParameterizedTest
    void processingCustomAnnotation(final AssignmentType assignmentType) {
        final PersonPojo result = Instancio.of(PersonPojo.class)
                .withSettings(Settings.create()
                        .set(Keys.ASSIGNMENT_TYPE, assignmentType))
                .create();

        assertThat(result.name).hasSize(10);
    }

    @Test
    void withMethodAssignment() {
        final DynPhone result = Instancio.of(DynPhone.class)
                .withSettings(Settings.create()
                        .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                        .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE))
                .ignore(all(Map.class)) // data Map
                .create();

        assertThat(result.getNumber()).isEqualTo(CustomGeneratorProvider.STRING_GENERATOR_VALUE);
        assertThat(result.getCountryCode()).matches("\\+\\d{2}");
    }

    @Test
    void overrideCustomAnnotationGenerator() {
        final PersonPojo result = Instancio.of(PersonPojo.class)
                .generate(field("name"), gen -> gen.string().length(20))
                .create();

        assertThat(result.name).hasSize(20);
    }

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
     * @see CustomGeneratorProvider.ExceptionThrowingGenerator
     */
    @EnumSource(AssignmentType.class)
    @ParameterizedTest
    void shouldPropagateExceptionThrownByGenerator(final AssignmentType assignmentType) {
        final InstancioApi<Field> api = Instancio.of(Field.class)
                .withSettings(Settings.create()
                        .set(Keys.ASSIGNMENT_TYPE, assignmentType));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("exception thrown by a custom Generator or Supplier")
                .hasRootCauseMessage("Expected error from SPI generator");
    }

    /**
     * @see CustomGeneratorProvider.CustomAddressGenerator
     */
    @Nested
    class AddressGeneratorTest {
        @Test
        void generatorWithCustomAfterGenerateAction() {
            final Address address = Instancio.create(Address.class);

            // Set by the generator
            assertThat(address.getCountry()).isEqualTo(
                    CustomGeneratorProvider.CustomAddressGenerator.COUNTRY);

            // The generator has AfterGenerate hint set to APPLY_SELECTORS,
            // therefore all fields except the one set by the generator should be null
            assertThat(address).hasAllNullFieldsOrPropertiesExcept("country");
        }

        @Test
        void overrideFieldValueSetByCustomGenerator() {
            final String override = "override";
            final Address address = Instancio.of(Address.class)
                    .set(field(Address::getCountry), override)
                    .create();

            assertThat(address.getCountry()).isEqualTo(override);

            assertThat(address).hasAllNullFieldsOrPropertiesExcept("country");
        }
    }

    @Nested
    class RecordTest {
        @Test
        void createRecord() {
            final FooRecord result = Instancio.create(FooRecord.class);

            assertThat(result.value()).isEqualTo(CustomGeneratorProvider.FOO_RECORD_VALUE);
        }

        @Test
        void overrideRecordSpiGenerator() {
            final FooRecord override = new FooRecord("override");
            final FooRecord result = Instancio.of(FooRecord.class)
                    .supply(all(FooRecord.class), random -> override)
                    .create();

            assertThat(result).isSameAs(override);
        }

        /**
         * If a generator supplies an entire record, it is no longer possible
         * to customise the record's field via selectors due to its immutability.
         */
        @Test
        @FeatureTag(Feature.UNSUPPORTED)
        void overrideRecordField() {
            final InstancioApi<FooRecord> api = Instancio.of(FooRecord.class)
                    .set(field(FooRecord::value), "override");

            // Note: this test requires src/main/java16 and fails in IDE
            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(UnusedSelectorException.class)
                    .hasMessageContaining("field(FooRecord, \"value\")");
        }
    }

    /**
     * @see CustomGeneratorProvider.CustomPhoneGenerator
     */
    @Test
    void generatorWithDefaultAfterGenerateAction() {
        final Phone phone = Instancio.create(Phone.class);

        // Set by the generator
        assertThat(phone.getNumber()).isEqualTo(
                CustomGeneratorProvider.CustomPhoneGenerator.NUMBER);

        // The generator does not specify AfterGenerate hint,
        // therefore all uninitialised fields should be populated
        // (i.e. engine will use default AfterGenerate value)
        assertThat(phone).hasNoNullFieldsOrProperties();
    }

    /**
     * @see CustomGeneratorProvider.PhoneWithTypeGenerator
     */
    @Test
    void generatorWithSubtype() {
        final Phone result = Instancio.of(Phone.class)
                .subtype(all(Phone.class), PhoneWithType.class)
                .create();

        assertThat(result).isExactlyInstanceOf(PhoneWithType.class);

        final PhoneWithType phone = (PhoneWithType) result;

        assertThat(phone.getNumber()).isEqualTo(
                CustomGeneratorProvider.PhoneWithTypeGenerator.NUMBER);

        assertThat(phone.getPhoneType()).isNotNull();
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

    @Test
    void assignmentTakesPrecedenceOverSpi() {
        final int expected = -1;
        final IntegerHolder result = Instancio.of(IntegerHolder.class)
                .set(all(int.class), expected)
                .assign(valueOf(int.class).to(all(Integer.class)))
                .create();

        assertThat(result.getPrimitive())
                .isEqualTo(result.getWrapper())
                .isEqualTo(expected);
    }

    /**
     * @see CustomGeneratorProvider.CustomFloatSpec
     */
    @Test
    void generatorSpecDoesNotImplementGeneratorInterface() {
        assertThatThrownBy(() -> Instancio.create(Float.class))
                .isExactlyInstanceOf(InstancioSpiException.class)
                .hasMessage("The GeneratorSpec %s returned by %s must implement %s",
                        CustomGeneratorProvider.CustomFloatSpec.class,
                        CustomGeneratorProvider.class,
                        Generator.class);
    }

    @Test
    void byteSequenceGenerator() {
        final List<ByteHolder> result = Instancio.ofList(ByteHolder.class)
                .size(3)
                .create();

        final Byte[] expected = {1, 2, 3};
        assertThat(result).extracting("primitive").containsExactly(expected);
        assertThat(result).extracting("wrapper").containsExactly(expected);
    }

    // used via reflection
    @SuppressWarnings("unused")
    private static final class ByteHolder {
        private byte primitive;
        private Byte wrapper;
    }
}

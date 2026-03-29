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
package org.instancio.test.protobuf;

import com.google.protobuf.Duration;
import com.google.protobuf.Timestamp;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.test.prototobuf.Proto;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;

@ExtendWith(InstancioExtension.class)
class ProtoIgnoreTest {

    @Test
    void ignoreSupportedNumericTypes() {
        final Proto.SupportedNumericTypes result = Instancio.of(Proto.SupportedNumericTypes.class)
                .ignore(fields())
                .create();

        assertThat(result.getStandardInt32()).isZero();
        assertThat(result.getStandardInt64()).isZero();
        assertThat(result.getUnsignedInt32()).isZero();
        assertThat(result.getUnsignedInt64()).isZero();
        assertThat(result.getSignedInt32()).isZero();
        assertThat(result.getSignedInt64()).isZero();
        assertThat(result.getFixedInt32()).isZero();
        assertThat(result.getFixedInt64()).isZero();
        assertThat(result.getSignedFixed32()).isZero();
        assertThat(result.getSignedFixed64()).isZero();
        assertThat(result.getSinglePrecision()).isZero();
        assertThat(result.getDoublePrecision()).isZero();
        assertThat(result.getWrappedDouble().getValue()).isZero();
        assertThat(result.getWrappedFloat().getValue()).isZero();
        assertThat(result.getWrappedInt32().getValue()).isZero();
        assertThat(result.getWrappedInt64().getValue()).isZero();
        assertThat(result.getWrappedUint32().getValue()).isZero();
        assertThat(result.getWrappedUint64().getValue()).isZero();
    }

    @Test
    void ignoreSupportedOtherTypes() {
        final Proto.SupportedOtherTypes result = Instancio.of(Proto.SupportedOtherTypes.class)
                .ignore(fields())
                .create();

        assertThat(result.getBoolField()).isFalse();
        assertThat(result.getBytesField()).isEmpty();
        assertThat(result.getWrappedBool().getValue()).isFalse();
        assertThat(result.getWrappedBytes().getValue()).isEmpty();
        assertThat(result.getDuration()).isEqualTo(Duration.getDefaultInstance());
        assertThat(result.getTimestamp()).isEqualTo(Timestamp.getDefaultInstance());
    }

    @Test
    void stringField_emptyString() {
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .ignore(field(Proto.Person::getName))
                .create();

        assertThat(result.getName()).isEmpty();
    }

    @Test
    void allStrings() {
        final Proto.Person result = Instancio.of(Proto.Person.class)
                // Set map size to 0 because with ignored strings
                // we can't generate sufficient number of Map string keys
                .withSetting(Keys.MAP_MAX_SIZE, 0)
                .ignore(all(String.class))
                .create();

        assertThat(result.getName()).isEmpty();
        assertThat(result.getNickname().getValue()).isEmpty(); // protobuf StringValue
        assertThat(result.getAddress().getCity()).isEmpty();
        assertThat(result.getAddressesMap()).isEmpty();
        assertThat(result.getAttributesMap()).isEmpty();
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void enumFieldGeneratesFirstEnumConstant() {
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .ignore(all(Proto.Gender.class))
                .create();

        // Proto default for enum is the first enum value (in this case number 0 = UNKNOWN)
        assertThat(result.getGender()).isEqualTo(Proto.Gender.UNKNOWN);
    }

    @Test
    void collections() {
        final Proto.Person result = Instancio.of(Proto.Person.class)
                .ignore(all(List.class))
                .ignore(all(Map.class))
                .create();

        assertThat(result.getAddressesMap()).isEmpty();
        assertThat(result.getAddress().getPhoneNumbersList()).isEmpty();
    }

    @Test
    void supportedNumericTypesAsMapValues() {
        final Proto.SupportedNumericTypesAsMapValues result = Instancio
                .of(Proto.SupportedNumericTypesAsMapValues.class)
                .withSetting(Keys.MAP_MIN_SIZE, 100)
                .withSetting(Keys.MAP_VALUES_NULLABLE, true)
                .withNullable(all(
                        all(Integer.class),
                        all(Long.class),
                        all(Float.class),
                        all(Double.class)
                ))
                .create();

        assertThat(result.getIntsMap().values()).isNotEmpty()
                .anySatisfy(v -> assertThat(v).isZero())
                .anySatisfy(v -> assertThat(v).isNotZero());

        assertThat(result.getLongsMap().values()).isNotEmpty()
                .anySatisfy(v -> assertThat(v).isZero())
                .anySatisfy(v -> assertThat(v).isNotZero());

        assertThat(result.getFloatsMap().values()).isNotEmpty()
                .anySatisfy(v -> assertThat(v).isZero())
                .anySatisfy(v -> assertThat(v).isNotZero());

        assertThat(result.getDoublesMap().values()).isNotEmpty()
                .anySatisfy(v -> assertThat(v).isZero())
                .anySatisfy(v -> assertThat(v).isNotZero());
    }

    @Test
    void supportedNumericTypesAsCollectionValues() {
        final Proto.SupportedNumericTypesAsCollectionValues result = Instancio
                .of(Proto.SupportedNumericTypesAsCollectionValues.class)
                .withSetting(Keys.COLLECTION_MIN_SIZE, 100)
                .withSetting(Keys.COLLECTION_ELEMENTS_NULLABLE, true)
                .withNullable(all(
                        all(Integer.class),
                        all(Long.class),
                        all(Float.class),
                        all(Double.class)
                ))
                .create();

        assertThat(result.getIntsList()).isNotEmpty()
                .anySatisfy(v -> assertThat(v).isZero())
                .anySatisfy(v -> assertThat(v).isNotZero());

        assertThat(result.getLongsList()).isNotEmpty()
                .anySatisfy(v -> assertThat(v).isZero())
                .anySatisfy(v -> assertThat(v).isNotZero());

        assertThat(result.getFloatsList()).isNotEmpty()
                .anySatisfy(v -> assertThat(v).isZero())
                .anySatisfy(v -> assertThat(v).isNotZero());

        assertThat(result.getDoublesList()).isNotEmpty()
                .anySatisfy(v -> assertThat(v).isZero())
                .anySatisfy(v -> assertThat(v).isNotZero());
    }

    @Test
    void supportedOtherTypesAsMapValues() {
        final Proto.SupportedOtherTypesAsMapValues result = Instancio.of(Proto.SupportedOtherTypesAsMapValues.class)
                .withSetting(Keys.MAP_MIN_SIZE, 100)
                .withSetting(Keys.MAP_VALUES_NULLABLE, true)
                .withNullable(all(
                        all(Boolean.class),
                        all(Duration.class),
                        all(Timestamp.class)
                ))
                .create();

        assertThat(result.getBoolsMap().values()).isNotEmpty()
                .anySatisfy(v -> assertThat(v).isTrue())
                .anySatisfy(v -> assertThat(v).isFalse());

        assertThat(result.getDurationsMap().values()).isNotEmpty()
                .anySatisfy(v -> assertThat(v).isNotNull())
                .anySatisfy(v -> assertThat(v).isEqualTo(Duration.getDefaultInstance()));

        assertThat(result.getTimestampsMap().values()).isNotEmpty()
                .anySatisfy(v -> assertThat(v).isNotNull())
                .anySatisfy(v -> assertThat(v).isEqualTo(Timestamp.getDefaultInstance()));
    }

    /**
     * Ignored target results in:
     *
     * <ul>
     *   <li>default instance for fields since protobuf does not allow null</li>
     *   <li>empty map/collections if target is a collection element</li>
     * </ul>
     */
    @Nested
    class NestedMessage {

        @RepeatedTest(Constants.SAMPLE_SIZE_D)
        void field() {
            final Proto.Person result = Instancio.of(Proto.Person.class)
                    .ignore(all(Proto.Address.class))
                    .create();

            assertThat(result.getAddress()).isEqualTo(Proto.Address.getDefaultInstance());
        }

        @Test
        void mapValue() {
            final Proto.Person result = Instancio.of(Proto.Person.class)
                    .ignore(all(Proto.Address.class))
                    .create();

            assertThat(result.getAddressesMap()).isEmpty();
        }

        @Test
        void collectionElement() {
            final Proto.Person result = Instancio.of(Proto.Person.class)
                    .ignore(all(Proto.Phone.class))
                    .create();

            assertThat(result.getAddress().getPhoneNumbersList()).isEmpty();
            assertThat(result.getAddressesMap().values())
                    .allMatch(address -> address.getPhoneNumbersList().isEmpty());
        }
    }
}

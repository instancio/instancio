/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.junit.generate;

import org.instancio.junit.Generate;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.instancio.settings.Keys;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(InstancioExtension.class)
class GenerateParameterTest {

    private @Generate int primitiveField;
    private @Generate String valueField;
    private @Generate StringHolder pojoField;
    private @Generate Set<StringHolder> pojoSetField;

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void generateParams(
            @Generate final int primitive,
            @Generate final String value,
            @Generate final StringHolder pojo,
            @Generate final Set<StringHolder> pojoSet) {

        assertResults(primitive, value, pojo, pojoSet);
    }

    @InstancioSource
    @ParameterizedTest
    void generateParamsWithInstancioSource(
            final int primitive,
            final String value,
            @Generate final StringHolder pojo,
            @Generate final Set<StringHolder> pojoSet) {

        assertResults(primitive, value, pojo, pojoSet);
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void generateFields() {
        assertResults(primitiveField, valueField, pojoField, pojoSetField);
    }

    @Nested
    class GenerateFieldsViaConstructorTest {
        private final int primitiveField;
        private final String valueField;
        private final StringHolder pojoField;
        private final Set<StringHolder> pojoSetField;

        GenerateFieldsViaConstructorTest(
                @Generate final int primitiveField,
                @Generate final String valueField,
                @Generate final StringHolder pojoField,
                @Generate final Set<StringHolder> pojoSetField) {

            this.primitiveField = primitiveField;
            this.valueField = valueField;
            this.pojoField = pojoField;
            this.pojoSetField = pojoSetField;
        }

        @RepeatedTest(Constants.SAMPLE_SIZE_DD)
        void generateFields() {
            assertResults(primitiveField, valueField, pojoField, pojoSetField);
        }
    }

    private static void assertResults(
            final int primitive,
            final String value,
            final StringHolder pojo,
            final Set<StringHolder> pojoSet) {

        assertThat(primitive).isPositive();
        assertThat(value).isNotBlank();
        assertThat(pojo.getValue()).isNotBlank();
        assertThat(pojoSet).hasSizeBetween(
                Keys.COLLECTION_MIN_SIZE.defaultValue(),
                Keys.COLLECTION_MAX_SIZE.defaultValue());
    }
}

/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.junit.given;

import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.instancio.settings.Keys;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(InstancioExtension.class)
class GivenTest {

    private @Given int primitiveField;
    private @Given String valueField;
    private @Given StringHolder pojoField;
    private @Given Set<StringHolder> pojoSetField;

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void givenParams(
            @Given final int primitive,
            @Given final String value,
            @Given final StringHolder pojo,
            @Given final Set<StringHolder> pojoSet) {

        assertResults(primitive, value, pojo, pojoSet);
    }

    @InstancioSource
    @ParameterizedTest
    void givenParamsWithInstancioSource(
            final int primitive,
            final String value,
            @Given final StringHolder pojo,
            @Given final Set<StringHolder> pojoSet) {

        assertResults(primitive, value, pojo, pojoSet);
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void givenFields() {
        assertResults(primitiveField, valueField, pojoField, pojoSetField);
    }

    /**
     * @see InstancioExtension#supportsParameter(ParameterContext, ExtensionContext)
     */
    @Disabled("Using @Given with constructor parameters is not supported")
    @Nested
    class GivenFieldsViaConstructorTest {
        private final int primitiveField;
        private final String valueField;
        private final StringHolder pojoField;
        private final Set<StringHolder> pojoSetField;

        GivenFieldsViaConstructorTest(
                @Given final int primitiveField,
                @Given final String valueField,
                @Given final StringHolder pojoField,
                @Given final Set<StringHolder> pojoSetField) {

            this.primitiveField = primitiveField;
            this.valueField = valueField;
            this.pojoField = pojoField;
            this.pojoSetField = pojoSetField;
        }

        @Test
        void givenFields() {
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

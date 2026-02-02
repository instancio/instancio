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
package org.instancio.test.features.generator.oneof;

import org.instancio.Assign;
import org.instancio.GeneratorSpecProvider;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

@FeatureTag({Feature.GENERATE, Feature.ONE_OF_ARRAY_GENERATOR, Feature.ONE_OF_COLLECTION_GENERATOR})
@ExtendWith(InstancioExtension.class)
class OneOfTypeMismatchTest {

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of((GeneratorSpecProvider<String>) gen -> gen.oneOf("bad-value")),
                Arguments.of((GeneratorSpecProvider<String>) gen -> gen.oneOf(Collections.singleton("bad-value")))
        );
    }

    @MethodSource("args")
    @ParameterizedTest
    void viaGenerate(final GeneratorSpecProvider<String> specProvider) {
        final InstancioApi<Person> api = Instancio.of(Person.class)
                .generate(field(Person::getAddress), specProvider);

        assertIncompatibleTypeError(api);
    }

    @MethodSource("args")
    @ParameterizedTest
    void viaAssign(final GeneratorSpecProvider<String> specProvider) {
        final InstancioApi<Person> api = Instancio.of(Person.class)
                .assign(Assign.valueOf(Person::getAddress).generate(specProvider));

        assertIncompatibleTypeError(api);
    }

    private void assertIncompatibleTypeError(final InstancioApi<Person> api) {
        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("error assigning value");
    }

}

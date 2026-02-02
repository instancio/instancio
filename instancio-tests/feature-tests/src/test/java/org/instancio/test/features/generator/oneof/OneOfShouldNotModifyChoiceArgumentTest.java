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

import org.instancio.GeneratorSpecProvider;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag({Feature.GENERATE, Feature.ONE_OF_ARRAY_GENERATOR, Feature.ONE_OF_COLLECTION_GENERATOR})
@ExtendWith(InstancioExtension.class)
class OneOfShouldNotModifyChoiceArgumentTest {

    private static Stream<Arguments> args() {
        final Phone argument = Phone.builder()
                .countryCode("+1")
                .number(null)
                .build();

        return Stream.of(
                Arguments.of((GeneratorSpecProvider<Phone>) gen -> gen.oneOf(argument)),
                Arguments.of((GeneratorSpecProvider<Phone>) gen -> gen.oneOf(Collections.singleton(argument)))
        );
    }

    @MethodSource("args")
    @ParameterizedTest
    void shouldNotModifyChoiceArgument(final GeneratorSpecProvider<Phone> specProvider) {
        final Address result = Instancio.of(Address.class)
                .generate(all(Phone.class), specProvider)
                .set(field(Phone::getNumber), "foo") // unused!
                .lenient()
                .create();

        assertThat(result.getPhoneNumbers()).isNotEmpty().allSatisfy(p -> {
            assertThat(p.getCountryCode()).isEqualTo("+1");
            assertThat(p.getNumber()).as("should not be populated").isNull();
        });
    }
}

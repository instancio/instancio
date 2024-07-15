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
import org.instancio.test.support.conditions.Conditions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(InstancioExtension.class)
class GenerateParameterWithMethodSourceTest {

    record Pojo(Integer methodSourceNumber, String methodSourceString, String generatedString) {}

    private static Stream<Arguments> args() {
        return Stream.of(
                Arguments.of(1, "A"),
                Arguments.of(2, "B"));
    }

    private final List<Pojo> results = new ArrayList<>();

    @Order(1)
    @MethodSource("args")
    @ParameterizedTest
    void methodSourceWithGeneratedParams(
            final Integer methodSourceNumber,
            final String methodSourceString,
            @Generate final String generatedString) {

        assertThat(generatedString).isNotBlank();

        // Since this method is run more than once, collect the results
        // from multiple invocations and verify in another method
        results.add(new Pojo(methodSourceNumber, methodSourceString, generatedString));
    }

    @Order(2)
    @Test
    void verify() {
        assertThat(results).extracting(Pojo::methodSourceNumber).containsExactly(1, 2);
        assertThat(results).extracting(Pojo::methodSourceString).containsExactly("A", "B");
        assertThat(results).extracting(Pojo::generatedString).are(Conditions.RANDOM_STRING);
    }
}

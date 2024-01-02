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
package org.instancio.spi.tests;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@ExtendWith(InstancioExtension.class)
class GetterMethodFieldResolverTest {

    private static class Pojo {
        private String foo_;

        String getFoo() {
            return foo_;
        }
    }

    @Test
    @DisplayName("Should map 'getFoo' to field 'foo_'")
    void shouldResolveFieldFromMethodReference() {
        final String expected = "foo";

        final Pojo result = Instancio.of(Pojo.class)
                .set(field(Pojo::getFoo), expected)
                .create();

        assertThat(result.getFoo()).isEqualTo(expected);
    }
}

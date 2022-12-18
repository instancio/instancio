/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.test.java17.generator;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GenericSealedInterfaceTest {

    private sealed interface BaseSealedInterface<T> {}

    private non-sealed interface BaseNonSealedInterface<T> extends BaseSealedInterface<T> {}

    private static class Holder<S, T extends BaseSealedInterface<S>> {
        private S valueOne;
        private T valueTwo;
    }

    private static class Foo implements BaseNonSealedInterface<String> {
        private String fooValue;
    }

    @Test
    void createContainer() {
        final Holder<String, Foo> result = Instancio.create(new TypeToken<>() {});

        assertThat(result.valueOne).isNotBlank();
        assertThat(result.valueTwo.fooValue).isNotBlank();
    }
}

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
package org.instancio.test.groovy


import org.instancio.Instancio
import org.junit.jupiter.api.Test

import static org.assertj.core.api.Assertions.assertThat
import static org.instancio.Select.field

class GroovySelectorTest {

    class GPerson {
        String name
    }

    @Test
    void propertyReference() {
        def result = Instancio.of(GPerson.class)
                .set(field(GPerson.&name), "foo")
                .create()
        assertThat(result.name).isEqualTo("foo")
    }

    @Test
    void methodReference() {
        def result = Instancio.of(GPerson.class)
                .set(field(GPerson::name), "foo")
                .create()

        assertThat(result.name).isEqualTo("foo")
    }
}

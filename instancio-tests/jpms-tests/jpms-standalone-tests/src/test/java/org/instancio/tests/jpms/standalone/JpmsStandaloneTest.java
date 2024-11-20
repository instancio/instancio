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
package org.instancio.tests.jpms.standalone;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class JpmsStandaloneTest {

    /**
     * Should instantiate bypassing constructor to verify
     * {@code Unsafe.allocateInstance()} works as expected.
     */
    @Test
    void createClassWithoutAvailableConstructor() {
        class ClassWithoutAvailableConstructor {
            private String value;

            public ClassWithoutAvailableConstructor(final String value) {
                throw new AssertionError("Expected error from constructor");
            }
        }

        var result = Instancio.create(ClassWithoutAvailableConstructor.class);

        assertThat(result.value).isNotBlank();
    }

    @Test
    void createPersonRecord() {
        record PersonRecord(String name, int age) {
        }

        var result = Instancio.create(PersonRecord.class);

        assertThat(result.name).isNotBlank();
        assertThat(result.age).isPositive();
    }

    @Test
    void wordGenerator() {
        // Verify that loading resources works on module path
        final String result = Instancio.gen().text().word().get();

        assertThat(result).isNotBlank();
    }
}

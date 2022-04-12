/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class MetaModelClassTest {

    @ValueSource(strings = {"org.foo.Example", "Example"})
    @ParameterizedTest
    void getSimpleName(String className) {
        assertThat(new MetaModelClass(className, Collections.emptyList()).getSimpleName()).isEqualTo("Example");
    }

    @Test
    void getPackageName() {
        assertThat(new MetaModelClass("org.foo.Example", Collections.emptyList()).getPackageName())
                .isEqualTo("org.foo");

        assertThat(new MetaModelClass("Example", Collections.emptyList()).getPackageName())
                .isNull();
    }
}

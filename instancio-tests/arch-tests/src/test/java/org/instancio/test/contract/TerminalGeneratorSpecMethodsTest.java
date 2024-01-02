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
package org.instancio.test.contract;

import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.ValueSpec;
import org.instancio.generator.specs.AsGeneratorSpec;
import org.instancio.generator.specs.Ip4GeneratorSpec;
import org.instancio.generator.specs.NullableGeneratorSpec;
import org.instancio.generator.specs.OneOfArrayGeneratorSpec;
import org.instancio.generator.specs.OneOfArraySpec;
import org.instancio.generator.specs.OneOfCollectionGeneratorSpec;
import org.instancio.generator.specs.OneOfCollectionSpec;
import org.instancio.generator.specs.PathAsGeneratorSpec;
import org.instancio.generator.specs.PathGeneratorSpec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.instancio.test.support.asserts.ClassAssert.assertThatClass;

class TerminalGeneratorSpecMethodsTest {

    @Test
    void as() {
        assertThatClass(AsGeneratorSpec.class)
                .withMethodNameMatching("as")
                .hasSize(1)
                .haveReturnType(GeneratorSpec.class);
    }

    @Test
    void nullable() {
        assertThatClass(NullableGeneratorSpec.class)
                .withMethodNameMatching("nullable")
                .hasSize(1)
                .haveReturnType(GeneratorSpec.class);
    }

    @Test
    void ip4() {
        assertThatClass(Ip4GeneratorSpec.class)
                .withMethodNameMatching("fromCidr")
                .hasSize(1)
                .haveReturnType(GeneratorSpec.class);
    }

    @ValueSource(classes = {PathGeneratorSpec.class, PathAsGeneratorSpec.class})
    @ParameterizedTest
    void path(final Class<?> specClass) {
        assertThatClass(specClass)
                .withMethodsMatching(m -> m.getName().startsWith("create"))
                .hasSize(3)
                .haveReturnType(GeneratorSpec.class);
    }

    @ValueSource(classes = {OneOfArraySpec.class, OneOfCollectionSpec.class})
    @ParameterizedTest
    void oneOfValueSpec(final Class<?> specClass) {
        assertThatClass(specClass)
                .withMethodsMatching(m -> m.getName().equals("oneOf") && m.getReturnType() == ValueSpec.class)
                .hasSize(1);
    }

    @ValueSource(classes = {OneOfArrayGeneratorSpec.class, OneOfCollectionGeneratorSpec.class})
    @ParameterizedTest
    void oneOfGeneratorSpec(final Class<?> specClass) {
        assertThatClass(specClass)
                .withMethodsMatching(m -> m.getName().equals("oneOf") && m.getReturnType() == GeneratorSpec.class)
                .hasSize(1);
    }
}

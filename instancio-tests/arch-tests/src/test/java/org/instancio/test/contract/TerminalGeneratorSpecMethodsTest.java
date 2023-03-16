/*
 * Copyright 2022-2023 the original author or authors.
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
import org.instancio.generator.specs.AsGeneratorSpec;
import org.instancio.generator.specs.Ip4GeneratorSpec;
import org.instancio.generator.specs.OneOfArraySpec;
import org.instancio.generator.specs.OneOfCollectionSpec;
import org.instancio.generator.specs.PathAsGeneratorSpec;
import org.instancio.generator.specs.PathGeneratorSpec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

class TerminalGeneratorSpecMethodsTest {

    @Test
    void as() {
        methods().that().areDeclaredIn(AsGeneratorSpec.class)
                .and().haveName("as")
                .should().haveRawReturnType(GeneratorSpec.class);
    }

    @Test
    void ip4() {
        methods().that().areDeclaredIn(Ip4GeneratorSpec.class)
                .and().haveName("fromCidr")
                .should().haveRawReturnType(GeneratorSpec.class);
    }

    @ValueSource(classes = {PathGeneratorSpec.class, PathAsGeneratorSpec.class})
    @ParameterizedTest
    void path(final Class<?> specClass) {
        methods().that().areDeclaredIn(specClass)
                .and().haveNameStartingWith("create")
                .should().haveRawReturnType(GeneratorSpec.class);
    }

    @ValueSource(classes = {OneOfArraySpec.class, OneOfCollectionSpec.class})
    @ParameterizedTest
    void oneOf(final Class<?> specClass) {
        methods().that().areDeclaredIn(specClass)
                .and().haveName("oneOf")
                .should().haveRawReturnType(GeneratorSpec.class);
    }
}

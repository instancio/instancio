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

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class MetaModelSourceGeneratorTest {

    private final MetaModelSourceGenerator sourceGenerator = new MetaModelSourceGenerator();

    @Test
    void getSource() {
        final String source = sourceGenerator.getSource(
                new MetaModelClass("org.example.SomeClass", Arrays.asList("fieldOne", "fieldTwo")));

        assertThat(source).containsSubsequence(
                "package org.example;",
                "import org.instancio.Binding;",
                "import org.instancio.Bindings;",
                "public class SomeClass_ {",
                "public static final Binding fieldOne = Bindings.field(SomeClass.class, \"fieldOne\");",
                "public static final Binding fieldTwo = Bindings.field(SomeClass.class, \"fieldTwo\");",
                "}");
    }

    @Test
    void getSourceWithNullPackage() {
        final String source = sourceGenerator.getSource(
                new MetaModelClass("SomeClass", Arrays.asList("fieldOne", "fieldTwo")));

        assertThat(source).containsSubsequence(
                "import org.instancio.Binding;",
                "import org.instancio.Bindings;",
                "public class SomeClass_ {",
                "public static final Binding fieldOne = Bindings.field(SomeClass.class, \"fieldOne\");",
                "public static final Binding fieldTwo = Bindings.field(SomeClass.class, \"fieldTwo\");",
                "}");
    }

    @Test
    void getSourceWithoutFields() {
        final String source = sourceGenerator.getSource(
                new MetaModelClass("SomeClass", Collections.emptyList()));

        assertThat(source).containsSubsequence(
                "import org.instancio.Binding;",
                "import org.instancio.Bindings;",
                "public class SomeClass_ {",
                "}");
    }
}

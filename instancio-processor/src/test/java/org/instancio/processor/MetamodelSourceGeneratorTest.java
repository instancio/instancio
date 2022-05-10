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

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.QualifiedNameable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.processor.ElementMocks.mockElementWithSimpleName;
import static org.mockito.Mockito.doReturn;

class MetamodelSourceGeneratorTest {
    private static final String SUFFIX = ""; // not specified - should use default suffix "_"

    private final MetamodelSourceGenerator sourceGenerator = new MetamodelSourceGenerator();

    @Test
    void getSource() {
        final QualifiedNameable classElement = ElementMocks.mockClassElementWithPackage(
                "SomeClass",
                "org.example.SomeClass",
                "org.example");

        final List<Element> fields = Arrays.asList(
                mockElementWithSimpleName(ElementKind.FIELD, "fieldOne"),
                mockElementWithSimpleName(ElementKind.FIELD, "fieldTwo"));

        doReturn(fields).when(classElement).getEnclosedElements();

        final String source = sourceGenerator.getSource(new MetamodelClass(classElement, SUFFIX));

        assertThat(source).containsSubsequence(
                "package org.example;",
                "import org.instancio.Select;",
                "import org.instancio.Selector;",
                "public class SomeClass_ {",
                "public static final Selector fieldOne = Select.field(org.example.SomeClass.class, \"fieldOne\");",
                "public static final Selector fieldTwo = Select.field(org.example.SomeClass.class, \"fieldTwo\");",
                "}");
    }

    @Test
    void getSourceForClassInDefaultPackage() {
        final QualifiedNameable classElement = ElementMocks.mockClassElementWithPackage(
                "SomeClass",
                "SomeClass",
                "");

        doReturn(Collections.emptyList()).when(classElement).getEnclosedElements();

        final String source = sourceGenerator.getSource(new MetamodelClass(classElement, SUFFIX));

        assertThat(source.trim())
                .doesNotContain("package")
                .startsWith("import")
                .containsSubsequence(
                        "import org.instancio.Select;",
                        "import org.instancio.Selector;",
                        "public class SomeClass_ {",
                        "}");
    }
}
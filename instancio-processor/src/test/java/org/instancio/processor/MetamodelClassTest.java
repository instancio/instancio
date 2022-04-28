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

import javax.lang.model.element.QualifiedNameable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class MetamodelClassTest {

    private static final String SUFFIX = "$";

    @Test
    void topLevelClass() {
        final QualifiedNameable outerType = ElementMocks.mockClassElementWithPackage(
                "Outer", "org.example.Outer", "org.example");

        final MetamodelClass modelClass = new MetamodelClass(outerType, SUFFIX);

        assertThat(modelClass.getName()).isEqualTo("org.example.Outer");
        assertThat(modelClass.getSimpleName()).isEqualTo("Outer");
        assertThat(modelClass.getPackageName()).isEqualTo("org.example");
        assertThat(modelClass.getMetamodelSimpleName()).isEqualTo("Outer" + SUFFIX);
        assertThat(modelClass.getMetamodelClassName()).isEqualTo("org.example.Outer" + SUFFIX);
    }

    @Test
    void innerClass() {
        final QualifiedNameable outerType = ElementMocks.mockClassElementWithPackage(
                "Outer", "org.example.Outer", "org.example");

        final QualifiedNameable innerType = ElementMocks.mockQualifiedNameable("org.example.Outer.Inner");
        when(innerType.getSimpleName().toString()).thenReturn("Inner");
        when(innerType.getEnclosingElement()).thenReturn(outerType);

        final MetamodelClass modelClass = new MetamodelClass(innerType, SUFFIX);

        assertThat(modelClass.getName()).isEqualTo("org.example.Outer.Inner");
        assertThat(modelClass.getSimpleName()).isEqualTo("Inner");
        assertThat(modelClass.getPackageName()).isEqualTo("org.example");
        assertThat(modelClass.getMetamodelSimpleName()).isEqualTo("Inner" + SUFFIX);
        assertThat(modelClass.getMetamodelClassName()).isEqualTo("org.example.Outer.Inner" + SUFFIX);
    }
}

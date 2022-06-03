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

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.QualifiedNameable;

import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ElementMocks {

    static Element mockElementWithSimpleName(ElementKind elementKind, String simpleName) {
        final Element element = mock(Element.class, RETURNS_DEEP_STUBS);
        when(element.getKind()).thenReturn(elementKind);
        when(element.getSimpleName().toString()).thenReturn(simpleName);
        return element;
    }

    static QualifiedNameable mockQualifiedNameable(final String fullyQualifiedName) {
        final QualifiedNameable element = mock(QualifiedNameable.class, RETURNS_DEEP_STUBS);
        when(element.getQualifiedName().toString()).thenReturn(fullyQualifiedName);
        return element;
    }

    static QualifiedNameable mockClassElementWithPackage(
            final String simpleName, final String fullyQualifiedClassName, final String packageName) {

        final QualifiedNameable classElement = mockQualifiedNameable(fullyQualifiedClassName);
        final QualifiedNameable packageType = mockQualifiedNameable(packageName);
        when(packageType.getKind()).thenReturn(ElementKind.PACKAGE);
        when(classElement.getEnclosingElement()).thenReturn(packageType);
        when(classElement.getSimpleName().toString()).thenReturn(simpleName);
        return classElement;
    }

    static Element mockFieldWithModifiers(final String simpleName, final Modifier... modifiers) {
        final Element element = mock(Element.class, RETURNS_DEEP_STUBS);
        when(element.getKind()).thenReturn(ElementKind.FIELD);
        when(element.getModifiers()).thenReturn(new HashSet<>(Arrays.asList(modifiers)));
        when(element.getSimpleName().toString()).thenReturn(simpleName);
        return element;
    }
}

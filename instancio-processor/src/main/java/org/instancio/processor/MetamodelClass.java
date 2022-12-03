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

import org.instancio.internal.util.StringUtils;

import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.QualifiedNameable;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

class MetamodelClass {
    private static final String DEFAULT_SUFFIX = "_";

    private final String name;
    private final String simpleName;
    private final String packageName;
    private final List<String> fieldNames;
    private final String metamodelClassSuffix;

    MetamodelClass(final QualifiedNameable classElement, final String metamodelClassSuffix) {
        final Element packageElement = getPackageElement(classElement);
        this.name = classElement.getQualifiedName().toString();
        this.packageName = getPackageName((QualifiedNameable) packageElement);
        this.fieldNames = getFieldNames(classElement);
        this.simpleName = classElement.getSimpleName().toString();
        this.metamodelClassSuffix = StringUtils.isBlank(metamodelClassSuffix)
                ? DEFAULT_SUFFIX : metamodelClassSuffix.trim();
    }

    private String getPackageName(final QualifiedNameable packageElement) {
        final String pkg = packageElement.getQualifiedName().toString();
        return "".equals(pkg) ? null : pkg;
    }

    private static Element getPackageElement(final Element classElement) {
        Element packageElement = classElement.getEnclosingElement();
        while (packageElement.getKind() != ElementKind.PACKAGE) {
            packageElement = packageElement.getEnclosingElement();
        }
        return packageElement;
    }

    private static List<String> getFieldNames(@Nullable final Element typeElement) {
        return typeElement == null ? Collections.emptyList() :
                typeElement.getEnclosedElements()
                        .stream()
                        .filter(e -> e.getKind() == ElementKind.FIELD && !e.getModifiers().contains(Modifier.STATIC))
                        .map(elem -> elem.getSimpleName().toString())
                        .collect(toList());
    }

    /**
     * Returns a fully qualified class name, as returned by {@link Class#getName()}.
     *
     * @return fully qualified class name
     */
    String getName() {
        return name;
    }

    /**
     * Simple name, as returned by {@link Class#getSimpleName()}.
     *
     * @return simple name
     */
    String getSimpleName() {
        return simpleName;
    }


    /**
     * Simple name of the metamodel class.
     *
     * @return simple name of the metamodel class
     */
    String getMetamodelSimpleName() {
        return simpleName + metamodelClassSuffix;
    }


    /**
     * Returns a fully qualified name of the metamodel class.
     *
     * @return fully qualified metamodel class name
     */
    String getMetamodelClassName() {
        return name + metamodelClassSuffix;
    }

    /**
     * Returns package name.
     *
     * @return package name, or {@code null} if none
     */
    String getPackageName() {
        return packageName;
    }

    /**
     * Returns a list of declared field names.
     *
     * @return declared field names
     */
    List<String> getFieldNames() {
        return fieldNames;
    }

    @Override
    public String toString() {
        return name;
    }
}

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

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class PublicApiMethodsTest {

    private static final int BALLPARK_NUMBER_OF_PUBLIC_API_CLASSES = 190;

    @Test
    @DisplayName("Public API method signature should not reference internal classes")
    void verifyMethodReturnType() {
        final JavaClasses publicApiJavaClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_PACKAGE_INFOS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .withImportOption(location -> !location.contains("internal"))
                .importPackages("org.instancio");

        assertThat(publicApiJavaClasses)
                .hasSizeGreaterThan(BALLPARK_NUMBER_OF_PUBLIC_API_CLASSES)
                .allSatisfy(this::assertDeclaredMethodsDoNotReferenceInternalClasses);
    }

    private void assertDeclaredMethodsDoNotReferenceInternalClasses(final JavaClass javaClass) {
        final Class<?> klass = loadClass(javaClass.getName());

        for (Method method : klass.getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers())) {
                continue;
            }

            assertClassDoesNotResideInInternalPackage(method.getReturnType(), method);

            for (Class<?> param : method.getParameterTypes()) {
                assertClassDoesNotResideInInternalPackage(param, method);
            }
        }
    }

    private static void assertClassDoesNotResideInInternalPackage(
            final Class<?> klass, final Method referencedBy) {

        final Package pkg = klass.getPackage();

        if (pkg != null && pkg.getName().contains("internal")) {
            final String failureMsg = String.format("" +
                            "Method%n%n" +
                            " -> %s%n%n" +
                            "should not reference class from internal package:%n%n" +
                            " -> %s",
                    referencedBy, klass);

            fail(failureMsg);
        }
    }

    private Class<?> loadClass(final String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException ex) {
            throw new AssertionError(ex);
        }
    }
}

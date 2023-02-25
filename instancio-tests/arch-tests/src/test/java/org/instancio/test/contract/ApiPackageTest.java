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

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.instancio.documentation.ExperimentalApi;
import org.instancio.documentation.InternalApi;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;

class ApiPackageTest {

    private static final String INTERNAL_PACKAGE = "org.instancio.internal..";

    private static final JavaClasses classes = new ClassFileImporter().importPackages("org.instancio");

    @Test
    void internalApisShouldResideWithinInternalPackage() {
        classes().that().areAnnotatedWith(InternalApi.class)
                .should().resideInAPackage(INTERNAL_PACKAGE)
                .check(classes);

        methods()
                .that().areAnnotatedWith(InternalApi.class)
                .should().beDeclaredInClassesThat()
                .resideInAPackage(INTERNAL_PACKAGE)
                .allowEmptyShould(true) // there may not be any annotated methods, which is fine
                .check(classes);
    }

    @Test
    void experimentalApisShouldResideInPublicPackages() {
        noClasses().that().areAnnotatedWith(ExperimentalApi.class)
                .should().resideInAPackage(INTERNAL_PACKAGE)
                .check(classes);

        noMethods().that().areAnnotatedWith(ExperimentalApi.class)
                .should().beDeclaredInClassesThat()
                .resideInAPackage(INTERNAL_PACKAGE)
                .allowEmptyShould(true) // there may not be any annotated methods, which is fine
                .check(classes);
    }
}

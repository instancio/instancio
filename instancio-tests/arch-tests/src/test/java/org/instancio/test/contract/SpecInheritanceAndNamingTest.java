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
import org.instancio.generator.AsStringGeneratorSpec;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.ValueSpec;
import org.instancio.generator.specs.AsGeneratorSpec;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class SpecInheritanceAndNamingTest {

    private static final JavaClasses classes = new ClassFileImporter().importPackages("org.instancio");

    @Test
    void shouldContainGeneratorSpecsOnly() {
        classes()
                .that().resideInAPackage("..specs..").and().doNotHaveSimpleName("package-info")
                .should().beAssignableTo(GeneratorSpec.class)
                .check(classes);
    }

    @Test
    void specNaming() {
        classes()
                .that().resideInAPackage("..specs..").and().doNotHaveSimpleName("package-info")
                .should().haveSimpleNameEndingWith("Spec")
                .check(classes);
    }

    @Test
    void generatorSpecsShouldNotBeAssignableToValueSpec() {
        classes()
                .that().resideInAPackage("..specs..").and().haveSimpleNameEndingWith("GeneratorSpec")
                .should().notBeAssignableTo(ValueSpec.class)
                .check(classes);
    }

    /**
     * Value specs should not expose methods defined by {@link AsStringGeneratorSpec},
     * since they return {@link GeneratorSpec} and not String.
     */
    @Test
    void valueSpecsShouldNotImplementAsStringGeneratorSpec() {
        classes()
                .that().resideInAPackage("..specs..").and().areAssignableTo(ValueSpec.class)
                .should().notBeAssignableTo(AsStringGeneratorSpec.class)
                .check(classes);
    }

    /**
     * Value specs should not expose methods defined by {@link AsGeneratorSpec},
     * since they return {@link GeneratorSpec} and not String.
     */
    @Test
    void valueSpecsShouldNotImplementAsGeneratorSpec() {
        classes()
                .that().resideInAPackage("..specs..").and().areAssignableTo(ValueSpec.class)
                .should().notBeAssignableTo(AsGeneratorSpec.class)
                .check(classes);
    }
}

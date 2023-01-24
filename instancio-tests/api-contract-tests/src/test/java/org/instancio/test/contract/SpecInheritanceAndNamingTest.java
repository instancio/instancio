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
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.instancio.generator.AsStringGeneratorSpec;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.ValueSpec;
import org.junit.jupiter.api.Test;

class SpecInheritanceAndNamingTest {

    private static final JavaClasses classes = new ClassFileImporter().importPackages("org.instancio");

    @Test
    void shouldContainGeneratorSpecsOnly() {
        ArchRule rule = ArchRuleDefinition.classes()
                .that().resideInAPackage("..specs..").and().doNotHaveSimpleName("package-info")
                .should().beAssignableTo(GeneratorSpec.class);

        rule.check(classes);
    }

    @Test
    void specNaming() {
        ArchRule rule = ArchRuleDefinition.classes()
                .that().resideInAPackage("..specs..").and().doNotHaveSimpleName("package-info")
                .should().haveSimpleNameEndingWith("Spec");

        rule.check(classes);
    }

    @Test
    void generatorSpecsShouldNotBeAssignableToValueSpec() {
        ArchRule rule = ArchRuleDefinition.classes()
                .that().resideInAPackage("..specs..").and().haveSimpleNameEndingWith("GeneratorSpec")
                .should().notBeAssignableTo(ValueSpec.class);

        rule.check(classes);
    }

    /**
     * Value specs should not expose methods defined by {@link AsStringGeneratorSpec},
     * since they return {@link GeneratorSpec} and not String.
     */
    @Test
    void valueSpecsShouldNotImplementAsStringGeneratorSpec() {
        ArchRule rule = ArchRuleDefinition.classes()
                .that().resideInAPackage("..specs..").and().areAssignableTo(ValueSpec.class)
                .should().notBeAssignableTo(AsStringGeneratorSpec.class);

        rule.check(classes);
    }
}

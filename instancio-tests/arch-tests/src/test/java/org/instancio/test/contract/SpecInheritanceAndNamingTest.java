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

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.ValueSpec;
import org.instancio.generators.AtomicGenerators;
import org.instancio.generators.ChecksumGenerators;
import org.instancio.generators.FinanceGenerators;
import org.instancio.generators.Generators;
import org.instancio.generators.IdGenerators;
import org.instancio.generators.IoGenerators;
import org.instancio.generators.NioGenerators;
import org.instancio.generators.SpatialGenerators;
import org.instancio.generators.TemporalGenerators;
import org.instancio.generators.TextGenerators;
import org.instancio.generators.can.CanIdGenerators;
import org.instancio.generators.pol.PolIdGenerators;
import org.instancio.generators.usa.UsaIdGenerators;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.instancio.test.support.asserts.ClassAssert.assertThatClass;

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
     * The return type of methods declared by  {@link Generators}
     * (and friends) should not be a {@link ValueSpec}.
     */
    @ValueSource(classes = {
            Generators.class,
            AtomicGenerators.class,
            ChecksumGenerators.class,
            IoGenerators.class,
            NioGenerators.class,
            TemporalGenerators.class,
            TextGenerators.class,
            FinanceGenerators.class,
            SpatialGenerators.class,
            IdGenerators.class,
            CanIdGenerators.class,
            PolIdGenerators.class,
            UsaIdGenerators.class
    })
    @ParameterizedTest
    void generatorsShouldNotReturnValueSpecs(final Class<?> klass) {
        assertThatClass(klass).hasAllMethodsSatisfying(method ->
                !ValueSpec.class.isAssignableFrom(method.getReturnType()));
    }
}

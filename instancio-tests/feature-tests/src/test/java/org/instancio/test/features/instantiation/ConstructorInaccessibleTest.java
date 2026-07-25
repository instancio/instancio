/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.test.features.instantiation;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.InstantiationStrategies;
import org.instancio.settings.Keys;
import org.instancio.settings.OnConstructorError;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.settings.InstantiationStrategy.NO_ARGS;

/**
 * A constructor that cannot be made accessible is not invoked; the next
 * instantiation strategy is attempted instead.
 *
 * <p>This requires a class in a named module that does not open its package.
 * A class declared by the tests cannot be used, since everything on the
 * classpath is in the unnamed module, which opens all of its packages.
 * The module is therefore compiled and loaded into its own module layer.
 */
@FeatureTag(Feature.INSTANTIATION_STRATEGIES)
@ExtendWith(InstancioExtension.class)
class ConstructorInaccessibleTest {

    private static final String MODULE_NAME = "not.open.module";
    private static final String PACKAGE_NAME = "notopen";
    private static final String SIMPLE_CLASS_NAME = "NoArgsPojo";

    // The class has no fields, since populating them would fail
    // for the same reason the constructor cannot be invoked
    private static final String SOURCE = "package " + PACKAGE_NAME + ";\n"
            + "public class " + SIMPLE_CLASS_NAME + " {}\n";

    private static Class<?> pojoClass;

    @BeforeAll
    static void compileAndLoadModule(@TempDir final Path dir) throws Exception {
        final Path sourceDir = dir.resolve("source");
        final Path classesDir = dir.resolve("classes");
        final Path moduleInfoPath = sourceDir.resolve("module-info.java");
        final Path pojoPath = sourceDir.resolve(PACKAGE_NAME).resolve(SIMPLE_CLASS_NAME + ".java");

        Files.createDirectories(sourceDir.resolve(PACKAGE_NAME));

        Files.writeString(moduleInfoPath, "module " + MODULE_NAME + " {}\n");
        Files.writeString(pojoPath, SOURCE);

        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final int compilationStatus = compiler.run(null, null, null,
                "-d", classesDir.toString(), moduleInfoPath.toString(), pojoPath.toString());

        assertThat(compilationStatus)
                .as("Compilation of the test fixture should succeed")
                .isZero();

        final Configuration configuration = ModuleLayer.boot().configuration().resolve(
                ModuleFinder.of(classesDir), ModuleFinder.of(), Set.of(MODULE_NAME));

        final ModuleLayer layer = ModuleLayer.boot()
                .defineModulesWithOneLoader(configuration, ClassLoader.getSystemClassLoader());

        pojoClass = layer.findLoader(MODULE_NAME)
                .loadClass(PACKAGE_NAME + '.' + SIMPLE_CLASS_NAME);
    }

    /**
     * Control: without it, the tests below would also pass if the class
     * were loaded from the unnamed module, or had no constructor at all.
     */
    @Test
    void verifyTestSetup() {
        assertThat(pojoClass.getModule().isOpen(PACKAGE_NAME))
                .as("The package must not be open, otherwise its constructor could be made accessible")
                .isFalse();

        assertThat(pojoClass.getDeclaredConstructors())
                .as("Expected the implicit no-argument constructor")
                .singleElement()
                .satisfies(ctor -> {
                    assertThat(ctor.getParameterCount()).isZero();
                    assertThat(ctor.trySetAccessible()).isFalse();
                });
    }

    @Test
    void noArgsStrategyIsNotApplicableToInaccessibleConstructor() {
        final Object result = Instancio.of(pojoClass)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, InstantiationStrategies.of(NO_ARGS))
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FAIL)
                .create();

        assertThat(result)
                .as("The constructor is not invoked, and there is no other strategy to fall back to")
                .isNull();
    }

    @Test
    void inaccessibleConstructorFallsBackToNextStrategy() {
        final Object result = Instancio.of(pojoClass)
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FAIL)
                .create();

        assertThat(result)
                .as("Created via BYPASS_CONSTRUCTOR, the last of the default strategies")
                .isNotNull();
    }
}

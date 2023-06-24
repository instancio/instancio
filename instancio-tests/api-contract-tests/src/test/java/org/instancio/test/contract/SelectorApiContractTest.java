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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for things that should not be allowed using selector public APIs.
 * <p>
 * These are to ensure that certain methods are not inadvertently exposed,
 * for example when refactoring, or introducing new classes or functionality.
 */
class SelectorApiContractTest {

    private static final Path SRC_DIR = Paths.get("src", "main", "non-build");
    private static final Path OUT_DIR = Paths.get("target", "non-build");
    private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();

    @BeforeAll
    static void beforeAll() throws IOException {
        Files.createDirectories(OUT_DIR);
    }

    @Test
    @DisplayName("Cannot pass 'Selector' to 'within()' method")
    void passingSelectorToWithinNotAllowed() throws Exception {
        assertCompilationError("NonCompilable_SelectorWithinSelector.java",
                "varargs mismatch; org.instancio.Selector cannot be converted to org.instancio.Scope");
    }

    @Test
    @DisplayName("Group selector has no 'within()' method")
    void groupSelectorDoesNotHaveAWithinMethod() throws Exception {
        assertCompilationError("NonCompilable_GroupSelectorDoesNotExposeWithinMethod.java",
                "cannot find symbol",
                "symbol: ", "method within(org.instancio.Scope)",
                "location: ", "interface org.instancio.SelectorGroup");
    }

    @Test
    @DisplayName("Passing scope as a selector")
    void passingScopeAsSelector() throws Exception {
        assertCompilationError("NonCompilable_PassingScopeAsSelector.java",
                "argument mismatch; org.instancio.Scope cannot be converted to org.instancio.TargetSelector");
    }

    @Test
    @DisplayName("Passing selector group to within")
    void passingGroupToWithin() throws Exception {
        assertCompilationError("NonCompilable_PassingGroupToWithin.java",
                "varargs mismatch; org.instancio.SelectorGroup cannot be converted to org.instancio.Scope");
    }

    @Test
    @DisplayName("Selector group has no scope() method")
    void selectorGroupHasNoScope() throws Exception {
        assertCompilationError("NonCompilable_SelectorGroupHasNoScope.java",
                "cannot find symbol",
                "symbol: ", "method scope()",
                "location: ", "interface org.instancio.SelectorGroup");
    }

    @Test
    @DisplayName("Selector group has no toScope() method")
    void selectorGroupHasNoToScope() throws Exception {
        assertCompilationError("NonCompilable_SelectorGroupCannotBeConvertedToScope.java",
                "cannot find symbol",
                "symbol: ", "method toScope()",
                "location: ", "interface org.instancio.SelectorGroup");
    }

    @Test
    @DisplayName("Nested groups not allowed")
    void nestedGroups() throws Exception {
        assertCompilationError("NonCompilable_NestedGroups.java",
                "no suitable method found for all(org.instancio.SelectorGroup)");
    }

    @Test
    @DisplayName("Chaining within() methods not allowed")
    void cannotChainWithinMethods() throws Exception {
        assertCompilationError("NonCompilable_CannotChainWithinMethods.java",
                "cannot find symbol",
                "location: ", "interface org.instancio.GroupableSelector");
    }

    @Test
    @DisplayName("Cannot group predicate field selectors")
    void cannotGroupPredicateFieldSelectors() throws Exception {
        assertCompilationError("NonCompilable_GroupWithPredicateFieldSelector.java",
                "no suitable method found for all(org.instancio.PredicateSelector)");
    }

    @Test
    @DisplayName("Cannot group predicate type selectors")
    void cannotGroupPredicateTypeSelectors() throws Exception {
        assertCompilationError("NonCompilable_GroupWithPredicateTypeSelector.java",
                "no suitable method found for all(org.instancio.PredicateSelector)");
    }

    @Test
    @DisplayName("Cannot group predicate builder for fields")
    void cannotGroupFieldPredicateBuilder() throws Exception {
        assertCompilationError("NonCompilable_GroupWithFieldsPredicateBuilder.java",
                "no suitable method found for all(org.instancio.FieldSelectorBuilder)");
    }

    @Test
    @DisplayName("Cannot group predicate builder for types")
    void cannotGroupTypePredicateBuilder() throws Exception {
        assertCompilationError("NonCompilable_GroupWithTypesPredicateBuilder.java",
                "no suitable method found for all(org.instancio.TypeSelectorBuilder)");
    }

    @Test
    @DisplayName("Fields predicate builder cannot be converted to scope")
    void fieldsPredicateBuilderCannotBeConvertedToScope() throws Exception {
        assertCompilationError("NonCompilable_FieldsPredicateBuilderToScope.java",
                "cannot find symbol", "method toScope()");
    }

    @Test
    @DisplayName("Types predicate builder cannot be converted to scope")
    void typesPredicateBuilderCannotBeConvertedToScope() throws Exception {
        assertCompilationError("NonCompilable_TypesPredicateBuilderToScope.java",
                "cannot find symbol", "method toScope()");
    }

    @Test
    @DisplayName("Fields predicate selector cannot be converted to scope")
    void fieldsPredicateSelectorCannotBeConvertedToScope() throws Exception {
        assertCompilationError("NonCompilable_FieldsPredicateSelectorToScope.java",
                "cannot find symbol", "method toScope()");
    }

    @Test
    @DisplayName("Types predicate selector cannot be converted to scope")
    void typesPredicateSelectorCannotBeConvertedToScope() throws Exception {
        assertCompilationError("NonCompilable_TypesPredicateSelectorToScope.java",
                "cannot find symbol", "method toScope()");
    }

    @Test
    @DisplayName("Fields predicate builder should not expose build() method")
    void fieldsPredicateBuilderShouldNotExposeBuildMethod() throws Exception {
        assertCompilationError("NonCompilable_FieldsPredicateBuilderBuildMethod.java",
                "cannot find symbol", "method build()");
    }

    @Test
    @DisplayName("Types predicate builder should not expose build() method")
    void typesPredicateBuilderShouldNotExposeBuildMethod() throws Exception {
        assertCompilationError("NonCompilable_TypesPredicateBuilderBuildMethod.java",
                "cannot find symbol", "method build()");
    }

    @Test
    @DisplayName("GroupableSelector should not expose atDepth() method")
    void groupableSelectorShouldNotExposeAtDepthMethod() throws Exception {
        assertCompilationError("NonCompilable_SelectorGroupDoesNotSupportAtDepth.java",
                "cannot find symbol", "method atDepth");
    }

    @Test
    @DisplayName("Root selector should not expose atDepth() method")
    void rootSelectorShouldNotExposeAtDepthMethod() throws Exception {
        assertCompilationError("NonCompilable_RootSelectorDoesNotSupportAtDepth.java",
                "cannot find symbol", "method atDepth");
    }

    @Test
    @DisplayName("atDepth() cannot be called more than once")
    void atDepthCannotBeCalledMoreThanOnce() throws Exception {
        assertCompilationError("NonCompilable_SelectorAtDepthCannotBeCalledMoreThanOnce.java",
                "cannot find symbol", "method atDepth");
    }

    @Test
    @DisplayName("ofList() should not expose withTypeParameters() method")
    void ofListWithTypeParametersNotAllowed() throws Exception {
        assertCompilationError("NonCompilable_OfListWithTypeParameters.java",
                "cannot find symbol", "method withTypeParameters");
    }

    @Test
    @DisplayName("ofSet() should not expose withTypeParameters() method")
    void ofSetWithTypeParametersNotAllowed() throws Exception {
        assertCompilationError("NonCompilable_OfSetWithTypeParameters.java",
                "cannot find symbol", "method withTypeParameters");
    }

    @Test
    @DisplayName("ofMap() should not expose withTypeParameters() method")
    void ofMapWithTypeParametersNotAllowed() throws Exception {
        assertCompilationError("NonCompilable_OfMapWithTypeParameters.java",
                "cannot find symbol", "method withTypeParameters");
    }

    @Test
    @DisplayName("assign() should not accept an incomplete Assign.given(TargetSelector) assignment")
    void incompleteGivenWithOneArgNotAllowed() throws Exception {
        assertCompilationError("NonCompilable_IncompleteGivenWithOneArg.java",
                "required:", "org.instancio.Assignment",
                "found:", "org.instancio.GivenOriginPredicate");
    }

    @Test
    @DisplayName("assign() should not accept an incomplete Assign.given(TargetSelector, TargetSelector) assignment")
    void incompleteGivenWithTwoArgsNotAllowed() throws Exception {
        assertCompilationError("NonCompilable_IncompleteGivenWithTwoArgs.java",
                "required:", "org.instancio.Assignment",
                "found:", "org.instancio.GivenOriginDestination");
    }

    @Test
    @DisplayName("assign() should not accept an incomplete Assign.valueOf(TargetSelector) assignment")
    void incompleteValueOf() throws Exception {
        assertCompilationError("NonCompilable_IncompleteValueOf.java",
                "required:", "org.instancio.Assignment",
                "found:", "org.instancio.ValueOf");
    }

    private static void assertCompilationError(final String sourceFile, final String... expectedErrorMsg) throws Exception {
        final DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();

        try (StandardJavaFileManager fileManager = COMPILER.getStandardFileManager(collector, null, null)) {
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singleton(OUT_DIR.toFile()));
            final Path sourcePath = SRC_DIR.resolve(sourceFile);
            final Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(
                    Collections.singleton(sourcePath.toString()));
            final Set<String> options = Collections.singleton("-Xdiags:verbose");
            final JavaCompiler.CompilationTask task = COMPILER.getTask(null, fileManager, collector, options,
                    null, compilationUnits);

            assertThat(task.call()).as("Expected a compilation error").isFalse();

            assertThat(collector.getDiagnostics()).hasSize(1).first().satisfies(diagnostic -> {
                assertThat(diagnostic.getKind()).isEqualTo(Diagnostic.Kind.ERROR);
                assertThat(diagnostic.getMessage(Locale.getDefault())).containsSubsequence(expectedErrorMsg);
            });
        }
    }
}

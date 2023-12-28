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
package org.instancio.api;

import org.instancio.Assignment;
import org.instancio.CartesianProductApi;
import org.instancio.FieldSelectorBuilder;
import org.instancio.GivenOriginDestination;
import org.instancio.GivenOriginPredicate;
import org.instancio.GroupableSelector;
import org.instancio.InstancioOfCollectionApi;
import org.instancio.PredicateSelector;
import org.instancio.Scope;
import org.instancio.ScopeableSelector;
import org.instancio.SelectorGroup;
import org.instancio.TargetSelector;
import org.instancio.TypeSelectorBuilder;
import org.instancio.ValueOf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.instancio.test.support.asserts.ClassAssert.assertThatClass;

/**
 * Tests for things that should not be allowed using selector public APIs.
 *
 * <p>These are to ensure that certain methods are not inadvertently exposed,
 * for example when refactoring, or introducing new classes or functionality.
 */
class ApiContractTest {

    /**
     * <pre>{@code
     * SelectorGroup group = Select.all(Select.all(String.class));
     *
     * // not allowed:
     * group.within(Select.scope(String.class));
     * group.toScope();
     * }</pre>
     */
    @Test
    @DisplayName("Methods not supported by group selector")
    void groupSelectorUnsupportedMethods() {
        assertThatClass(SelectorGroup.class).hasNoMethodsNamed("within", "toScope");
    }

    @Test
    @DisplayName("Should not allow passing scope as a selector and vice versa")
    void shouldNotAllowPassingScopeAsSelectorAndViceVersa() {
        assertThatClass(TargetSelector.class).isNotAssignableFromOrTo(Scope.class);
    }

    /**
     * {@code Select.all(Select.all(Select.field("foo")))}
     */
    @Test
    @DisplayName("Nested groups not allowed")
    void nestedGroups() {
        assertThatClass(SelectorGroup.class).isNotAssignableFromOrTo(GroupableSelector.class);
    }

    /**
     * <pre>{@code
     * Select.field("foo")
     *     .within(Select.scope(String.class))
     *     .within(Select.scope(String.class)) // second within() not allowed
     * }</pre>
     */
    @Test
    @DisplayName("Chaining within() methods not allowed")
    void cannotChainWithinMethods() {
        assertThatClass(GroupableSelector.class).hasNoMethodsNamed("within");
    }

    /**
     * {@code Select.fields().toScope()}
     */
    @Test
    @DisplayName("Fields predicate builder cannot be converted to scope")
    void fieldsPredicateBuilderCannotBeConvertedToScope() {
        assertThatClass(FieldSelectorBuilder.class).hasNoMethodsNamed("toScope");
    }

    /**
     * {@code Select.types().toScope()}
     */
    @Test
    @DisplayName("Types predicate builder cannot be converted to scope")
    void typesPredicateBuilderCannotBeConvertedToScope() {
        assertThatClass(TypeSelectorBuilder.class).hasNoMethodsNamed("toScope");
    }

    /**
     * {@code Select.fields(p -> true).toScope()}
     * {@code Select.types(p -> true).toScope()}
     */
    @Test
    @DisplayName("Predicate selector cannot be converted to scope")
    void fieldsPredicateSelectorCannotBeConvertedToScope() {
        assertThatClass(PredicateSelector.class).hasNoMethodsNamed("toScope");
    }

    /**
     * {@code Select.fields().build()}
     * {@code Select.types().build()}
     */
    @Test
    @DisplayName("Predicate selector builders should not expose build() method")
    void predicateBuildersShouldNotExposeBuildMethod() {
        assertThatClass(FieldSelectorBuilder.class).hasNoMethodsNamed("build");
        assertThatClass(TypeSelectorBuilder.class).hasNoMethodsNamed("build");
    }

    /**
     * {@code Select.all(Select.allStrings()).atDepth(1)}
     */
    @Test
    @DisplayName("GroupableSelector should not expose atDepth() method")
    void groupableSelectorShouldNotExposeAtDepthMethod() {
        assertThatClass(GroupableSelector.class).hasNoMethodsNamed("atDepth");
    }

    /**
     * E.g. {@code Select.root().atDepth(1)}
     */
    @Test
    @DisplayName("Root selector should not expose atDepth() method")
    void targetSelectorShouldNotExposeAtDepthMethod() {
        assertThatClass(TargetSelector.class).hasNoMethodsNamed("atDepth");
    }

    /**
     * {@code Select.all(String.class).atDepth(1).atDepth(2)}
     */
    @Test
    @DisplayName("atDepth() cannot be called more than once")
    void atDepthCannotBeCalledMoreThanOnce() {
        assertThatClass(ScopeableSelector.class).hasNoMethodsNamed("atDepth");
    }

    /**
     * {@code Instancio.ofList(String.class).withTypeParameters(String.class)}
     */
    @Test
    @DisplayName("ofList(), ofSet(), ofMap() should not expose withTypeParameters() method")
    void ofListWithTypeParametersNotAllowed() {
        assertThatClass(InstancioOfCollectionApi.class).hasNoMethodsNamed("withTypeParameters");
    }

    /**
     * Example of incomplete assignments:
     *
     * <pre>{@code
     *
     * Instancio.of(Person.class)
     *     .assign(Assign.given(Select.all(String.class)).satisfies(c -> true));
     *
     * Instancio.of(Person.class)
     *     .assign(Assign.given(Select.allStrings(), Select.allStrings()));
     *
     * Instancio.of(Person.class)
     *     .assign(Assign.valueOf(Select.all(String.class)));
     * }</pre>
     */
    @Test
    @DisplayName("assign() should not accept an incomplete assignment")
    void incompleteAssignmentsNotAllowed() {
        assertThatClass(Assignment.class)
                .isNotAssignableFromOrTo(GivenOriginPredicate.class)
                .isNotAssignableFromOrTo(GivenOriginDestination.class)
                .isNotAssignableFromOrTo(ValueOf.class);
    }

    /**
     * Ensure these methods are not exposed by mistake.
     */
    @Test
    void methodsNotSupportedByCartesianProductApi() {
        assertThatClass(CartesianProductApi.class)
                .hasNoMethodsNamed("create", "asResult", "toModel");
    }
}

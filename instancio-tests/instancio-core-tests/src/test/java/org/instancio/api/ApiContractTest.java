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
package org.instancio.api;

import org.instancio.Assignment;
import org.instancio.CartesianProductApi;
import org.instancio.DepthPredicateSelector;
import org.instancio.FieldSelectorBuilder;
import org.instancio.GivenOrigin;
import org.instancio.GivenOriginDestination;
import org.instancio.GivenOriginDestinationAction;
import org.instancio.GivenOriginPredicate;
import org.instancio.GivenOriginPredicateAction;
import org.instancio.GroupableSelector;
import org.instancio.InstancioOfCollectionApi;
import org.instancio.LenientSelector;
import org.instancio.PredicateSelector;
import org.instancio.Scope;
import org.instancio.ScopeableSelector;
import org.instancio.Select;
import org.instancio.Selector;
import org.instancio.SelectorGroup;
import org.instancio.TargetSelector;
import org.instancio.TypeSelectorBuilder;
import org.instancio.ValueOf;
import org.instancio.ValueOfOriginDestination;
import org.instancio.When;
import org.instancio.generator.GeneratorSpec;
import org.instancio.internal.util.Sonar;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.instancio.Assign.given;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ClassAssert.assertThatClass;

/**
 * Tests for things that should not be allowed using selector public APIs.
 *
 * <p>These are to ensure that certain methods are not inadvertently exposed,
 * for example when refactoring, or introducing new classes or functionality.
 */
class ApiContractTest {

    @Test
    void generatorSpec() {
        assertThatClass(GeneratorSpec.class).hasNoMethods();
    }

    @Test
    void assignment() {
        assertThatClass(Assignment.class).hasNoMethods();
    }

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

    /**
     * {@code Select.all(Select.all(Select.field("foo")))}
     */
    @Test
    @DisplayName("Nested groups not allowed")
    void nestedGroups() {
        assertThatClass(SelectorGroup.class).isNotAssignableFromOrTo(GroupableSelector.class);
    }

    @Test
    @DisplayName("Should not allow passing scope as a selector and vice versa")
    void shouldNotAllowPassingScopeAsSelectorAndViceVersa() {
        assertThatClass(TargetSelector.class).isNotAssignableFromOrTo(Scope.class);
    }

    @Test
    @DisplayName("Root selector should not expose any methods")
    void targetSelectorShouldNotExposeAnyMethods() {
        assertThatClass(TargetSelector.class).hasNoMethods();
    }

    /**
     * E.g. {@code lenient()} should not allow the following usage:
     *
     * <pre>{@code
     * all(Person.class).lenient().toScope();
     * all(Person.class).lenient().within(...);
     * }
     */
    @Test
    @DisplayName("Lenient selector should not expose the lenient() method")
    void lenientSelectorShouldOnlyExposeLenientMethod() {
        assertThatClass(LenientSelector.class).hasOnlyMethodsNamed("lenient");
    }

    /**
     * <pre>{@code
     * Select.field("foo")
     *     .within(Select.scope(String.class))
     *     .within(Select.scope(String.class)) // second within() not allowed
     *
     * // Selectors that have scopes should not be allowed to be converted toScope()
     * Select.field("foo")
     *     .within(Select.scope(String.class))
     *     .toScope() // not allowed!
     * }</pre>
     */
    @Test
    @DisplayName("Groupable selectors should not allow these methods")
    void groupableSelectorUnsupportedMethod() {
        assertThatClass(GroupableSelector.class).hasNoMethodsNamed("within", "toScope");
    }

    @Test
    @DisplayName("Methods supported by Selector")
    void selectorSupportedMethods() {
        assertThatClass(Selector.class)
                .hasOnlyMethodsNamed("atDepth", "lenient", "within", "toScope");
    }

    @Test
    @DisplayName("Methods supported by PredicateSelector")
    void predicateSelectorSupportedMethods() {
        assertThatClass(PredicateSelector.class)
                .hasOnlyMethodsNamed("atDepth", "lenient", "within", "toScope");
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
    @DisplayName("GroupableSelector should only expose lenient() method")
    void groupableSelectorShouldOnlyExposeLenientMethod() {
        assertThatClass(GroupableSelector.class).hasOnlyMethodsNamed("lenient");
    }

    /**
     * {@code Select.all(String.class).atDepth(1).atDepth(2)}
     */
    @Test
    @DisplayName("atDepth() cannot be called more than once")
    void atDepthCannotBeCalledMoreThanOnce() {
        // Not allowing atDepth() to be chained more than once
        // is especially important for predicate selectors,
        // that express the depth condition, such as 'depth(3)',
        // as an integer predicate: 'depth -> depth == 3'.
        //
        // Setting depth more than once would lead to hard-to-debug issues
        // because the selector toString() would be,
        // e.g. 'fields(Predicate<Field>).atDepth(3)',
        // while the underlying predicate is comprised of multiple,
        // potentially conflicting, depth conditions.
        assertThatClass(ScopeableSelector.class).hasNoMethodsNamed("atDepth");
    }

    @Test
    void scopeableSelectorSupportedMethods() {
        assertThatClass(ScopeableSelector.class)
                .hasOnlyMethodsNamed("lenient", "toScope", "within");
    }

    @Test
    void depthPredicateSelectorSupportedMethods() {
        assertThatClass(DepthPredicateSelector.class).hasOnlyMethodsNamed("atDepth");
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
                .hasNoMethodsNamed("asResult", "toModel");
    }

    /**
     * Not tests, but a bunch of statements to quickly catch potentially
     * breaking changes when refactoring selector/assignment APIs.
     *
     * <p>For compatibility changes, build instancio-core
     * with the {@code japicmp} profile.
     */
    @SuppressWarnings({Sonar.ADD_ASSERTION, "unused"})
    @Nested
    class ApiChangesTest {

        @Nested
        class SelectTest {
            @Test
            void selector() {
                final Selector s1 = Select.allInts();
                final Selector s2 = allStrings();
                final Selector s3 = Select.all(String.class);
                final Selector s4 = field("foo");
            }

            @Test
            void selectorAtDepth() {
                final ScopeableSelector ss1 = Select.allInts().atDepth(1);
                final ScopeableSelector ss2 = allStrings().atDepth(1);
                final ScopeableSelector ss3 = Select.all(String.class).atDepth(1);
                final ScopeableSelector ss4 = field("foo").atDepth(1);
            }

            @Test
            void selectorToScope() {
                final Scope scope2 = allStrings().toScope();
                final Scope scope3 = Select.all(String.class).toScope();
                final Scope scope4 = field("foo").toScope();
            }

            @Test
            void selectorAtDepthToScope() {
                final Scope scope2 = allStrings().atDepth(1).toScope();
                final Scope scope3 = Select.all(String.class).atDepth(1).toScope();
                final Scope scope4 = field("foo").atDepth(1).toScope();
            }

            @Test
            void predicateSelector() {
                final PredicateSelector p1 = Select.fields(f -> true);
                final PredicateSelector p2 = Select.types(f -> true);
            }

            @Test
            void predicateSelectorAtDepth() {
                final PredicateSelector p = Select.fields(f -> true);

                // before 4.2.0
                final GroupableSelector gs1 = p.atDepth(1);
                final GroupableSelector gs2 = p.atDepth(d -> d > 1);

                // from 4.2.0 returns ScopeableSelector
                final ScopeableSelector ss1 = p.atDepth(1);
                final ScopeableSelector ss2 = p.atDepth(d -> d > 1);
            }

            @Test
            void predicateSelectorToScope() {
                final PredicateSelector p = Select.fields(f -> true);

                final Scope scope = p.toScope();
            }

            @Test
            void predicateSelectorAtDepthToScope() {
                final ScopeableSelector ss = Select.fields(f -> true).atDepth(1);

                final Scope scope = ss.toScope();
            }

            @Test
            void predicateSelectorBuilder() {
                final FieldSelectorBuilder b1 = Select.fields();
                final TypeSelectorBuilder b2 = Select.types();
            }

            @Test
            void predicateSelectorBuilderToScope() {
                final FieldSelectorBuilder b1 = Select.fields();
                final TypeSelectorBuilder b2 = Select.types();

                final Scope scope1 = b1.toScope();
                final Scope scope2 = b2.toScope();
            }

            @Test
            void predicateSelectorBuilderWithin() {
                final FieldSelectorBuilder b1 = Select.fields();
                final TypeSelectorBuilder b2 = Select.types();

                final GroupableSelector within1 = b1.within();
                final GroupableSelector within2 = b2.within();
            }

            @Test
            void predicateSelectorBuilderAtDepth() {
                // before 4.2.0
                final GroupableSelector gs1 = Select.fields().atDepth(1);
                final GroupableSelector gs2 = Select.fields().atDepth(f -> true);
                final GroupableSelector gs3 = Select.types().atDepth(1);
                final GroupableSelector gs4 = Select.types().atDepth(t -> true);

                // from 4.2.0
                final ScopeableSelector ss1 = Select.fields().atDepth(1);
                final ScopeableSelector ss2 = Select.fields().atDepth(f -> true);
                final ScopeableSelector ss3 = Select.types().atDepth(1);
                final ScopeableSelector ss4 = Select.types().atDepth(t -> true);
            }

            @Test
            void predicateSelectorBuilderAtDepthToScope() {
                final ScopeableSelector ss = Select.fields().atDepth(1);

                final Scope scope = ss.toScope();
            }
        }

        @Nested
        class AssignTest {
            private final TargetSelector origin = field(StringHolder::getValue);
            private final TargetSelector destination1 = allStrings();
            private final TargetSelector destination2 = allStrings();

            @Test
            void givenOrigin() {
                final GivenOrigin given = given(origin);

                final GivenOriginPredicate requiredAction = given.is("foo");

                final GivenOriginPredicateAction valueOfAction1 = requiredAction.set(destination1, null);
                final GivenOriginPredicateAction valueOfAction2 = valueOfAction1.set(destination2, null);
            }

            @Test
            void givenOriginDestination() {
                final GivenOriginDestination given = given(origin, destination1);

                final GivenOriginDestinationAction givenAction1 = given.set(When.is("foo"), "F");
                final GivenOriginDestinationAction givenAction2 = givenAction1.set(When.is("bar"), "B");
                final Assignment assignment = givenAction2.elseSet(null);
            }


            @Test
            void valueOfTo() {
                final ValueOf valueOf = valueOf(origin);
                final ValueOfOriginDestination valueOfOriginDestination = valueOf.to(destination1);
                final Assignment assignment = valueOfOriginDestination.as(Function.identity());
            }

            @Test
            void valueOfSet() {
                final ValueOf valueOf = valueOf(origin);
                final Assignment assignment = valueOf.set("foo");
            }
        }
    }
}

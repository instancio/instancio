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
package org.instancio.internal.context;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Scope;
import org.instancio.TargetSelector;
import org.instancio.internal.selectors.InternalSelector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;
import static org.instancio.testsupport.asserts.ScopeAssert.assertScope;

class SetModelSelectorHelperTest {

    private static final Class<Object> ROOT_CLASS = Object.class;

    //@formatter:off
    record Container(Outer outer) {}
    record Outer(Inner inner) {}
    record Inner(Foo foo) {}
    record Foo(InnerFoo innerFoo, String value) {}
    record InnerFoo(String value) {}
    //@formatter:on

    private final SetModelSelectorHelper setModelSelectorHelper = new SetModelSelectorHelper(ROOT_CLASS);

    @Test
    void applyModelSelectorScopes() {
        // Given
        final TargetSelector modelTarget = field(Inner.class, "foo")
                .within(scope(Outer.class), scope(Inner.class));

        final TargetSelector modelSelector = allStrings().within(scope(InnerFoo.class));

        // When
        final InternalSelector result = (InternalSelector) setModelSelectorHelper
                .applyModelSelectorScopes(modelTarget, modelSelector);

        // Then
        final List<Scope> scopes = result.getScopes();
        assertThat(scopes).hasSize(4);
        assertScope(scopes.get(0)).hasTargetClass(Outer.class);
        assertScope(scopes.get(1)).hasTargetClass(Inner.class);
        assertScope(scopes.get(2)).hasTargetClass(Inner.class).hasField("foo");
        assertScope(scopes.get(3)).hasTargetClass(InnerFoo.class);
    }

    /**
     * An example based on {@link #applyModelSelectorScopes}.
     */
    @Test
    void shouldSetInnerFooValue() {
        final String expected = "foo";

        final Model<Foo> fooModel = Instancio.of(Foo.class)
                .set(allStrings().within(scope(InnerFoo.class)), expected) // 'modelSelector'
                .toModel();

        final Container result = Instancio.of(Container.class)
                .setModel(field(Inner::foo).within(scope(Outer.class), scope(Inner.class)), fooModel) // 'modelTarget'
                .create();

        assertThat(result.outer.inner.foo.innerFoo.value).isEqualTo(expected);
        assertThat(result.outer.inner.foo.value).isNotEqualTo(expected);
    }
}

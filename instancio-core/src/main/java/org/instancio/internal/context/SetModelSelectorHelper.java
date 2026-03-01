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

import org.instancio.Scope;
import org.instancio.TargetSelector;
import org.instancio.internal.selectors.InternalSelector;
import org.instancio.internal.selectors.SelectorImpl;
import org.instancio.internal.selectors.TargetClass;

import java.util.ArrayList;
import java.util.List;

final class SetModelSelectorHelper {

    private final Class<?> rootClass;

    SetModelSelectorHelper(final Class<?> rootClass) {
        this.rootClass = rootClass;
    }

    /**
     * Creates a new selector by applies scopes to the given {@code modelSelector}.
     *
     * <p>For example, given:
     *
     * <pre>{@code
     * record Container(Outer outer) {}
     * record Outer(Inner inner) {}
     * record Inner(Foo foo) {}
     * record Foo(InnerFoo innerFoo, String value) {}
     * record InnerFoo(String value) {}
     *
     * Model<Foo> fooModel = Instancio.of(Foo.class)
     *     .set(allStrings().within(scope(InnerFoo.class)), "foo") // 'modelSelector'
     *     .toModel();
     *
     * // when calling
     * Container container = Instancio.of(Container.class)
     *   .setModel(field(Inner::foo).within(scope(Outer.class), scope(Inner.class)), fooModel) // 'modelTarget'
     *   .create();
     * }</pre>
     *
     * <p>this method will return a {@code List} of scopes in the following order
     * (outermost to innermost):
     *
     * <pre>{@code
     *  0. scope(Outer.class)
     *  1. scope(Inner.class)
     *  2. scope(Inner::foo) // i.e. field(Inner::foo) itself converted to a Scope
     *  3. scope(InnerFoo.class)
     * }</pre>
     *
     * <p>The list of scopes is then applied to the given {@code modelSelector}.
     *
     * <p>The {@code setModel()} example above is equivalent to:
     *
     * <pre>{@code
     * Container container = Instancio.of(Container.class)
     *     .set(allStrings().within(
     *             scope(Outer.class),
     *             scope(Inner.class),
     *             scope(Inner::foo),
     *             scope(InnerFoo.class)), "foo")
     *     .create();
     * }</pre>
     *
     * @param modelTarget   the model selector from {@code setModel(TargetSelector, Model)}
     * @param modelSelector a selector within the Model provided to {@code setModel(TargetSelector, Model)}
     * @return a copy of {@code modelSelector} with updated scopes
     */
    TargetSelector applyModelSelectorScopes(
            final TargetSelector modelTarget,
            final TargetSelector modelSelector) {

        final InternalSelector internalModelTarget = (InternalSelector) modelTarget;
        final InternalSelector internalModelSelector = (InternalSelector) modelSelector;

        final List<Scope> scopes = new ArrayList<>(4);
        scopes.addAll(internalModelTarget.getScopes());
        scopes.add(internalModelTarget.toScope());
        scopes.addAll(internalModelSelector.getScopes());

        if (internalModelSelector.isRootSelector()) {
            // convert root() to a regular class selector
            return SelectorImpl.builder(new TargetClass(rootClass))
                    .scopes(scopes)
                    .apiMethodSelector(internalModelTarget.getApiMethodSelector())
                    .build();
        }

        return internalModelSelector.within(scopes.toArray(new Scope[0]));
    }
}

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
import org.instancio.internal.selectors.ElementFrameStack;
import org.instancio.internal.selectors.ElementOfDescriptor;
import org.instancio.internal.selectors.PredicateScopeImpl;
import org.instancio.internal.selectors.PredicateSelectorImpl;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

final class ElementOfState {

    /**
     * How a selector map's entries contribute to elementOf bookkeeping.
     */
    enum SelectorMapRole {
        /**
         * Contributes presence and, via the index spec's min size, container widening.
         */
        STANDARD,

        /**
         * {@code ignore()} entries: contribute presence and frame-dependence, never widening.
         */
        IGNORE
    }

    private final ElementFrameStack elementFrameStack;
    private final List<ElementOfDescriptor> minSizeDescriptors;
    private boolean hasElementOfSelectors;
    private boolean hasFrameDependentIgnores;

    ElementOfState() {
        this.elementFrameStack = ElementFrameStack.create();
        this.minSizeDescriptors = new ArrayList<>();
    }

    ElementFrameStack.@Nullable Frame peekActiveFrame() {
        return elementFrameStack.peek();
    }

    ElementFrameStack getElementFrameStack() {
        return hasElementOfSelectors ? elementFrameStack : ElementFrameStack.NOOP;
    }

    void onPut(final PredicateSelectorImpl selector, final SelectorMapRole selectorMapRole) {
        final ElementOfDescriptor eod = selector.getElementOfDescriptor();
        if (eod != null) {
            hasElementOfSelectors = true;
            if (selectorMapRole == SelectorMapRole.STANDARD && eod.requiredMinSize() > 0) {
                minSizeDescriptors.add(eod);
            }
        } else if (hasElementOfScope(selector)) {
            // Needed for setBlank() and setModel() with elementOf selector
            // where the latter is carried as a scope rather than a descriptor
            hasElementOfSelectors = true;
        }
        if (selectorMapRole == SelectorMapRole.IGNORE && !hasFrameDependentIgnores) {
            hasFrameDependentIgnores = eod != null || hasElementOfScope(selector);
        }
    }

    /**
     * Although elementOf does not support scoping via public APIs, internally
     * it can carry scopes from {@code setModel()} and {@code setBlank()} APIs.
     */
    private static boolean hasElementOfScope(final PredicateSelectorImpl selector) {
        for (Scope scope : selector.getScopes()) {
            if (((PredicateScopeImpl) scope).getElementOfDescriptor() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether any elementOf() selector is present (broader than {@link #getMinSizeDescriptors()}).
     */
    boolean hasElementOfSelectors() {
        return hasElementOfSelectors;
    }

    /**
     * Whether any ignore() entry is frame-dependent (elementOf selector or elementOf-derived scope).
     */
    boolean hasFrameDependentIgnores() {
        return hasFrameDependentIgnores;
    }

    /**
     * Descriptors of elementOf() selectors whose index spec implies a minimum container size.
     */
    List<ElementOfDescriptor> getMinSizeDescriptors() {
        return minSizeDescriptors;
    }
}

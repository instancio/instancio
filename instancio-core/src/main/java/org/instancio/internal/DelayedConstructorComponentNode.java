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
package org.instancio.internal;

import org.instancio.internal.nodes.InternalNode;

/**
 * A component of a constructor-created object whose generation was delayed:
 * either a constructor argument (identified by {@code argIndex}), or
 * a pre-generated non-parameter field (see {@link #forNonParameterField}).
 */
record DelayedConstructorComponentNode(InternalNode node, int argIndex) {

    private static final int NON_PARAMETER = -1;

    static DelayedConstructorComponentNode forNonParameterField(final InternalNode node) {
        return new DelayedConstructorComponentNode(node, NON_PARAMETER);
    }

    boolean isNonParameterField() {
        return argIndex == NON_PARAMETER;
    }

    @Override
    public String toString() {
        return String.format("DelayedComponentNode[%s, %s]", node, argIndex);
    }
}

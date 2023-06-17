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
package org.instancio.testsupport.templates;

import org.instancio.TypeTokenSupplier;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeFactory;
import org.instancio.test.support.tags.NodeTag;
import org.instancio.testsupport.fixtures.Nodes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Type;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

/**
 * Test models created by the {@link NodeFactory}.
 *
 * @param <T> type being verified
 */
@NodeTag
@TestInstance(PER_CLASS)
@SuppressWarnings("unused") // false positive (type T is used by TypeContext)
public abstract class NodeTestTemplate<T> {

    private final TypeContext typeContext = new TypeContext(this.getClass());

    @Test
    protected final void verifyingModelFromTypeToken() {
        final NodeFactory nodeFactory = Nodes.nodeFactory();
        final TypeTokenSupplier<Type> typeSupplier = typeContext::getGenericType;
        final InternalNode rootNode = nodeFactory.createRootNode(typeSupplier.get());
        verify(rootNode);
    }

    protected int withMaxDepth() {
        return Integer.MAX_VALUE;
    }

    /**
     * A method for verifying created node.
     */
    protected abstract void verify(InternalNode rootNode);
}

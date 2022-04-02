/*
 * Copyright 2022 the original author or authors.
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

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.TypeTokenSupplier;
import org.instancio.internal.InternalModel;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.nodes.NodeFactory;
import org.instancio.testsupport.tags.NodeTag;
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
public abstract class NodeTestTemplate<T> {

    private final TypeContext typeContext = new TypeContext(this.getClass());

    @Test
    protected final void verifyingModelFromTypeToken() {
        Model<?> model = Instancio.of((TypeTokenSupplier<Type>) typeContext::getGenericType).toModel();
        verify(((InternalModel<?>) model).getRootNode());
    }

    /**
     * A method for verifying created node.
     */
    protected abstract void verify(Node rootNode);
}

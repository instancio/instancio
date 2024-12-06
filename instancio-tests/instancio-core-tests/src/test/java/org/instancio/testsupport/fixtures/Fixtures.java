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
package org.instancio.testsupport.fixtures;

import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeFactory;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodUnmatched;
import org.instancio.settings.Settings;

import java.lang.reflect.Type;

public final class Fixtures {

    public static InternalNode node(final Type type) {
        return nodeFactory().createRootNode(type);
    }

    public static NodeFactory nodeFactory() {
        return new NodeFactory(modelContext());
    }

    public static ModelContext modelContext() {
        return ModelContext.builder(Object.class)
                .withMaxDepth(Integer.MAX_VALUE)
                .withSettings(Settings.create()
                        .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                        .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE))
                .build();
    }

    private Fixtures() {
        // non-instantiable
    }
}

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
package org.instancio.internal;

import org.instancio.InstancioApi;
import org.instancio.Model;
import org.instancio.TargetSelector;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.Format;
import org.instancio.internal.util.TypeUtils;

import java.lang.reflect.Type;

class SetModelValidatingListener implements GenerationListener {

    private final ModelContext<?> context;

    SetModelValidatingListener(final ModelContext<?> context) {
        this.context = context;
    }

    /**
     * Checks that the model provided via
     * {@link InstancioApi#setModel(TargetSelector, Model)}
     * is compatible with the selector target's type.
     */
    @Override
    public void objectCreated(final InternalNode node, final GeneratorResult result) {
        final ModelContext<?> otherContext = context.getSetModelSelectorMap().getContext(node);

        if (otherContext == null) {
            return;
        }

        final Type modelRootType = otherContext.getRootType();
        final Class<?> modelRawType = TypeUtils.getRawType(modelRootType);

        if (!modelRawType.isAssignableFrom(node.getTargetClass())) {
            final String modelTypeName = Format.withoutPackage(modelRootType);

            throw Fail.withUsageError(
                    "Model<%s> specified in setModel() method is incompatible with the selector target"
                            + "%n"
                            + "%n -> Model type ............: %s"
                            + "%n -> Selector target type ..: %s",
                    modelTypeName, modelTypeName, Format.withoutPackage(node.getType()));
        }
    }
}

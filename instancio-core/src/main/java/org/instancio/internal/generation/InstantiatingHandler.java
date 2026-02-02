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
package org.instancio.internal.generation;

import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Hints;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.instantiation.Instantiator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeKind;
import org.instancio.internal.util.ReflectionUtils;
import org.jetbrains.annotations.NotNull;

class InstantiatingHandler implements NodeHandler {

    private static final Hints POPULATE_ALL_HINT = Hints.builder()
            .afterGenerate(AfterGenerate.POPULATE_ALL)
            .build();

    private final Instantiator instantiator;

    InstantiatingHandler(final ModelContext context) {
        this.instantiator = new Instantiator(context.getServiceProviders().getTypeInstantiators());

    }

    @NotNull
    @Override
    public GeneratorResult getResult(@NotNull final InternalNode node) {
        if (node.is(NodeKind.RECORD) && !node.getChildren().isEmpty()) {
            // If a record has args, it must be populated
            // via the canonical constructor by the engine
            return GeneratorResult.emptyResult();
        }

        final Class<?> targetClass = node.getTargetClass();

        if (ReflectionUtils.isArrayOrConcrete(targetClass)) {
            final Object object = instantiator.instantiate(targetClass);
            return GeneratorResult.create(object, POPULATE_ALL_HINT);
        }

        return GeneratorResult.emptyResult();
    }
}

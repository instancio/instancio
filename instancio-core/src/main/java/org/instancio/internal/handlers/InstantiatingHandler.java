/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.internal.handlers;

import org.instancio.generator.GeneratorResult;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.reflection.instantiation.Instantiator;
import org.instancio.util.ReflectionUtils;

import java.util.Optional;

public class InstantiatingHandler implements NodeHandler {

    private final ModelContext<?> context;
    private final Instantiator instantiator;

    public InstantiatingHandler(final ModelContext<?> context, final Instantiator instantiator) {
        this.context = context;
        this.instantiator = instantiator;
    }

    @Override
    public Optional<GeneratorResult> getResult(final Node node) {
        final Class<?> effectiveType = context.getSubtypeMapping(node.getTargetClass());
        if (ReflectionUtils.isConcrete(effectiveType)) {
            final GeneratorResult result = GeneratorResult.create(instantiator.instantiate(effectiveType));
            return Optional.of(result);
        }
        return Optional.empty();
    }


}

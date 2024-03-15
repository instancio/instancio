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

import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.spi.InternalServiceProvider;
import org.instancio.internal.spi.InternalServiceProvider.InternalContainerFactoryProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

class ContainerFactoriesHandler {

    private final List<InternalServiceProvider> internalServiceProviders;

    ContainerFactoriesHandler(final List<InternalServiceProvider> providers) {
        this.internalServiceProviders = providers;
    }

    /**
     * Replaces the original result with another type. For example, converts
     * a Map to ImmutableMap using {@code ImmutableMap.copyOf(Map)}).
     *
     * @param node   the container node
     * @param result the result to be replaced
     * @return the replacement, or the original result if there was no substitution
     */
    GeneratorResult substituteResult(
            final InternalNode node,
            final GeneratorResult result) {

        if (internalServiceProviders.isEmpty()) {
            return result;
        }

        final List<Class<?>> typeArgs = new ArrayList<>(node.getChildren().size());
        for (InternalNode child : node.getChildren()) {
            typeArgs.add(child.getTargetClass());
        }

        for (InternalServiceProvider isp : internalServiceProviders) {
            final InternalContainerFactoryProvider cfp = isp.getContainerFactoryProvider();
            if (cfp == null) {
                continue;
            }

            final Function<Object, ?> fn = cfp.getMappingFunction(node.getTargetClass(), typeArgs);

            if (fn != null) {
                final Object replacement = fn.apply(result.getValue());
                return GeneratorResult.create(replacement, result.getHints());
            }
        }
        return result;
    }
}

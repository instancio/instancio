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
package org.instancio.internal.nodes.resolvers;

import org.instancio.internal.nodes.NodeKind;
import org.instancio.internal.nodes.NodeKindResolver;
import org.instancio.internal.spi.InternalContainerFactoryProvider;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class NodeKindContainerResolver implements NodeKindResolver {

    private final List<InternalContainerFactoryProvider> containerFactories;

    public NodeKindContainerResolver(final List<InternalContainerFactoryProvider> containerFactories) {
        this.containerFactories = containerFactories;
    }

    @Override
    public Optional<NodeKind> resolve(final Class<?> targetClass) {
        if (targetClass == Optional.class
                || targetClass == EnumSet.class
                || containerFactories.stream().anyMatch(p -> p.isContainerClass(targetClass))) {
            return Optional.of(NodeKind.CONTAINER);
        }

        return Optional.empty();
    }
}

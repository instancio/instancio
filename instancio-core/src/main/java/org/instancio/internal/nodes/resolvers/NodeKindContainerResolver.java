/*
 * Copyright 2022-2025 the original author or authors.
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
import org.instancio.internal.spi.InternalServiceProvider;
import org.instancio.internal.spi.InternalServiceProvider.InternalContainerFactoryProvider;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class NodeKindContainerResolver implements NodeKindResolver {

    private final List<InternalServiceProvider> serviceProviders;

    NodeKindContainerResolver(final List<InternalServiceProvider> serviceProviders) {
        this.serviceProviders = serviceProviders;
    }

    @Override
    public Optional<NodeKind> resolve(final Class<?> targetClass) {
        if (targetClass == Optional.class
                || targetClass == EnumSet.class
                || Map.Entry.class.isAssignableFrom(targetClass)
                || isSpiDefinedContainer(targetClass)) {
            return Optional.of(NodeKind.CONTAINER);
        }

        return Optional.empty();
    }

    private boolean isSpiDefinedContainer(final Class<?> targetClass) {
        for (InternalServiceProvider p : serviceProviders) {
            final InternalContainerFactoryProvider provider = p.getContainerFactoryProvider();
            if (provider != null && provider.isContainer(targetClass)) {
                return true;
            }
        }
        return false;
    }
}

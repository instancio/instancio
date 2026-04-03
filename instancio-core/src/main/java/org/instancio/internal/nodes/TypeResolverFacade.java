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
package org.instancio.internal.nodes;

import org.instancio.internal.spi.ProviderEntry;
import org.instancio.internal.util.TypeUtils;
import org.instancio.spi.InstancioServiceProvider.TypeResolver;
import org.instancio.spi.InstancioSpiException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

class TypeResolverFacade {

    private final List<ProviderEntry<TypeResolver>> providerEntries;

    TypeResolverFacade(final List<ProviderEntry<TypeResolver>> providerEntries) {
        this.providerEntries = providerEntries;
    }

    Optional<Class<?>> resolve(final InternalNode node) {
        final Class<?> type = resolveViaSPI(node);
        return Optional.ofNullable(type);
    }

    private Class<?> resolveViaSPI(final InternalNode node) {
        final Class<?> typeToResolve = node.getRawType();
        for (ProviderEntry<TypeResolver> entry : providerEntries) {
            final TypeResolver typeResolver = entry.getProvider();
            Type resolved = typeResolver.getSubtype(node);
            if (resolved == null) {
                //noinspection removal
                resolved = typeResolver.getSubtype(typeToResolve);
            }

            if (resolved != null) {
                final Class<?> resolvedClass = TypeUtils.getRawType(resolved);
                if (!typeToResolve.isAssignableFrom(resolvedClass)) {
                    throw new InstancioSpiException(String.format(
                            "%n%s provided an invalid subtype:%n" +
                                    " -> %s%nis not a subtype of:%n -> %s",
                            entry.getInstancioProviderClass(), resolvedClass, typeToResolve));
                }
                return resolvedClass;
            }
        }
        return null;
    }
}

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
package org.instancio.internal.nodes;

import org.instancio.internal.spi.ProviderEntry;
import org.instancio.internal.util.ServiceLoaders;
import org.instancio.spi.InstancioServiceProvider;
import org.instancio.spi.InstancioSpiException;
import org.instancio.spi.TypeResolver;

import java.util.List;
import java.util.Optional;

class TypeResolverFacade {

    private static final List<TypeResolver> DEPRECATED_TYPE_RESOLVERS = ServiceLoaders.loadAll(TypeResolver.class);

    private final List<ProviderEntry<InstancioServiceProvider.TypeResolver>> providerEntries;

    TypeResolverFacade(final List<ProviderEntry<InstancioServiceProvider.TypeResolver>> providerEntries) {
        this.providerEntries = providerEntries;
    }

    Optional<Class<?>> resolve(final Class<?> typeToResolve) {
        final Class<?> resolvedViaNewSpi = resolveViaNewSPI(typeToResolve);
        return resolvedViaNewSpi == null
                ? resolvedViaDeprecatedSPI(typeToResolve)
                : Optional.of(resolvedViaNewSpi);
    }

    private Class<?> resolveViaNewSPI(final Class<?> typeToResolve) {
        for (ProviderEntry<InstancioServiceProvider.TypeResolver> entry : providerEntries) {
            final Class<?> resolved = entry.getProvider().getSubtype(typeToResolve);

            if (resolved != null) {
                if (!typeToResolve.isAssignableFrom(resolved)) {
                    throw new InstancioSpiException(String.format(
                            "%n%s provided an invalid subtype:%n" +
                                    " -> %s%nis not a subtype of:%n -> %s",
                            entry.getInstancioProviderClass(), resolved, typeToResolve));
                }
                return resolved;
            }
        }
        return null;
    }

    private static Optional<Class<?>> resolvedViaDeprecatedSPI(final Class<?> typeToResolve) {
        for (TypeResolver resolver : DEPRECATED_TYPE_RESOLVERS) {
            final Optional<Class<?>> resolved = resolver.resolve(typeToResolve);
            if (resolved.isPresent()) {
                return resolved;
            }
        }
        return Optional.empty();
    }
}

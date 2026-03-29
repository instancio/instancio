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
package org.instancio.internal.nodes;

import org.instancio.internal.spi.InternalTypeResolver;
import org.instancio.internal.spi.ProviderEntry;
import org.instancio.internal.util.TypeUtils;
import org.instancio.spi.InstancioServiceProvider.TypeResolver;
import org.instancio.spi.InstancioSpiException;

import java.lang.reflect.Type;
import java.util.List;

class TypeResolverFacade {

    private final List<ProviderEntry<TypeResolver>> providerEntries;

    TypeResolverFacade(final List<ProviderEntry<TypeResolver>> providerEntries) {
        this.providerEntries = providerEntries;
    }

    SubtypeResult resolve(final InternalNode node) {
        for (ProviderEntry<TypeResolver> entry : providerEntries) {
            final TypeResolver typeResolver = entry.getProvider();
            final Type resolvedType = typeResolver.getSubtype(node);

            if (resolvedType != null) {
                final Class<?> originalTargetClass = node.getTargetClass();
                final boolean validateSubtype = shouldValidateSubtype(typeResolver);

                // NOTE: we validate in this method to fail-fast at the source.
                // However, downstream also performs validation because subtypes
                // can be specified via other APIs beside TypeResolver
                // (e.g. via instancio.properties, Settings, subtype() API, etc).
                // The `validateSubtype` flag is carried forward to skip that validation.
                if (validateSubtype && !originalTargetClass.isAssignableFrom(TypeUtils.getRawType(resolvedType))) {
                    throw new InstancioSpiException(String.format(
                            "%n%s provided an invalid subtype:%n" +
                            " -> %s%nis not a subtype of:%n -> %s",
                            entry.getInstancioProviderClass(), resolvedType, originalTargetClass));
                }

                return SubtypeResult.of(resolvedType, validateSubtype);
            }
        }
        return SubtypeResult.empty();
    }

    private static boolean shouldValidateSubtype(final TypeResolver typeResolver) {
        final boolean isNonInternalResolver = !(typeResolver instanceof InternalTypeResolver);

        // Certain internal type resolvers can disable subtype
        // validation because of specific implementation quirks
        final boolean isInternalResolverWithValidation =
                typeResolver instanceof InternalTypeResolver itr
                && itr.isSubtypeValidationEnabled();

        return isNonInternalResolver || isInternalResolverWithValidation;
    }
}

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

import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.util.SealedClassUtils;
import org.instancio.internal.util.TypeUtils;
import org.instancio.settings.Settings;
import org.instancio.spi.InstancioServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

final class SubtypeResolver {

    private static final Logger LOG = LoggerFactory.getLogger(SubtypeResolver.class);

    private final ModelContext modelContext;
    private final TypeResolverFacade typeResolverFacade;

    SubtypeResolver(final ModelContext modelContext) {
        this.modelContext = modelContext;
        this.typeResolverFacade = new TypeResolverFacade(
                modelContext.getServiceProviders().getTypeResolvers());
    }

    /**
     * Returns subtype for the given node, if one is available.
     *
     * <ol>
     *   <li>{@code instancio.properties}</li>
     *   <li>from {@link Settings}</li>
     *   <li>specified via {@link InstancioServiceProvider.TypeResolver} SPI</li>
     *   <li>from {@link InstancioApi#subtype(TargetSelector, Class)}</li>
     *   <li>a generator's {@code subtype()} method, e.g. {@code gen.collection().subtype()}</li>
     * </ol>
     */
    Optional<Class<?>> resolveSubtype(@NotNull final InternalNode node) {
        final Optional<Class<?>> subtype = getSubtype(node);

        if (subtype.isPresent()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Resolved subtype: {} -> {}", node.getRawType().getName(), subtype.get().getName());
            }
            return subtype;
        }
        if (SealedClassUtils.isSealedAbstractType(node.getTargetClass())
                && (node.is(NodeKind.POJO) || node.is(NodeKind.RECORD))) {
            final List<Class<?>> impls = SealedClassUtils.getSealedClassImplementations(node.getTargetClass());
            return Optional.of(modelContext.getRandom().oneOf(impls));
        }
        return Optional.ofNullable(resolveSubtypeFromAncestors(node));
    }

    private Optional<Class<?>> getSubtype(final InternalNode node) {
        final Optional<Class<?>> subtype = modelContext.getSubtypeSelectorMap().getSubtype(node);
        if (subtype.isPresent()) {
            return subtype;
        }

        final Settings settings = modelContext.getSettings();
        final Class<?> subtypeFromSettings = settings.getSubtypeMap().get(node.getRawType());

        return subtypeFromSettings == null
                ? typeResolverFacade.resolve(node.getRawType())
                : Optional.of(subtypeFromSettings);
    }

    private static Class<?> resolveSubtypeFromAncestors(final InternalNode node) {
        InternalNode next = node;
        while (next != null) {
            final Type actualType = next.getTypeMap().get(node.getRawType());
            if (actualType != null) {
                return TypeUtils.getRawType(actualType);
            }
            next = next.getParent();
        }
        return null;
    }
}

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
package org.instancio.internal.nodes;

import org.instancio.InstancioApi;
import org.instancio.Random;
import org.instancio.TargetSelector;
import org.instancio.generator.Generator;
import org.instancio.internal.context.BooleanSelectorMap;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.context.SubtypeSelectorMap;
import org.instancio.internal.spi.InternalServiceProvider;
import org.instancio.schema.Schema;
import org.instancio.settings.Settings;
import org.instancio.spi.InstancioServiceProvider.TypeResolver;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class NodeContext {
    private final ModelContext<?> modelContext;
    private final int maxDepth;
    private final Settings settings;
    private final Random random;
    private final Map<TypeVariable<?>, Type> rootTypeMap;
    private final BooleanSelectorMap ignoredSelectorMap;
    private final SubtypeSelectorMap subtypeSelectorMap;
    private final BooleanSelectorMap assignmentOriginSelectors;
    private final Map<Class<?>, Class<?>> subtypeMappingFromSettings;
    private final TypeResolverFacade typeResolverFacade;
    private final List<InternalServiceProvider> internalServiceProviders;

    public NodeContext(final ModelContext<?> modelContext) {
        this.modelContext = modelContext;
        maxDepth = modelContext.getMaxDepth();
        settings = modelContext.getSettings();
        random = modelContext.getRandom();
        rootTypeMap = modelContext.getRootTypeMap();
        ignoredSelectorMap = modelContext.getIgnoreSelectorMap();
        subtypeSelectorMap = modelContext.getSubtypeSelectorMap();
        assignmentOriginSelectors = modelContext.getAssignmentOriginSelectorMap();
        subtypeMappingFromSettings = settings.getSubtypeMap();
        internalServiceProviders = modelContext.getInternalServiceProviders();
        typeResolverFacade = new TypeResolverFacade(modelContext.getServiceProviders().getTypeResolvers());
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public Settings getSettings() {
        return settings;
    }

    public Random getRandom() {
        return random;
    }

    public Map<TypeVariable<?>, Type> getRootTypeMap() {
        return rootTypeMap;
    }

    public Set<TargetSelector> getAssignmentOriginSelectors(final InternalNode node) {
        return assignmentOriginSelectors.getSelectorMap().getSelectors(node);
    }

    void putGenerator(final TargetSelector selector, final Generator<?> generator) {
        modelContext.putGenerator(selector, generator);
    }

    /**
     * Returns subtype for the given node, if one is available.
     *
     * <ol>
     *   <li>{@code instancio.properties}</li>
     *   <li>from {@link Settings}</li>
     *   <li>specified via {@link TypeResolver} SPI</li>
     *   <li>from {@link InstancioApi#subtype(TargetSelector, Class)}</li>
     *   <li>a generator's {@code subtype()} method, e.g. {@code gen.collection().subtype()}</li>
     * </ol>
     */
    Optional<Class<?>> getSubtype(@NotNull final InternalNode node) {
        final Optional<Class<?>> subtype = subtypeSelectorMap.getSubtype(node);
        if (subtype.isPresent()) {
            return subtype;
        }

        final Class<?> subtypeFromSettings = subtypeMappingFromSettings.get(node.getRawType());

        return subtypeFromSettings == null
                ? typeResolverFacade.resolve(node.getRawType())
                : Optional.of(subtypeFromSettings);
    }

    boolean isIgnored(@NotNull final InternalNode node) {
        return ignoredSelectorMap.isTrue(node);
    }

    public List<InternalServiceProvider> getInternalServiceProviders() {
        return internalServiceProviders;
    }

    public Schema getSchema(final InternalNode node) {
        return modelContext.getSchema(node);
    }
}

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

import jakarta.validation.constraints.NotNull;
import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.internal.context.BooleanSelectorMap;
import org.instancio.internal.context.SubtypeSelectorMap;
import org.instancio.internal.spi.InternalContainerFactoryProvider;
import org.instancio.internal.spi.ProviderEntry;
import org.instancio.settings.Settings;
import org.instancio.spi.InstancioServiceProvider.TypeResolver;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class NodeContext {
    private final int maxDepth;
    private final Map<TypeVariable<?>, Type> rootTypeMap;
    private final BooleanSelectorMap ignoredSelectorMap;
    private final SubtypeSelectorMap subtypeSelectorMap;
    private final BooleanSelectorMap assignmentOriginSelectors;
    private final Map<Class<?>, Class<?>> subtypeMappingFromSettings;
    private final TypeResolverFacade typeResolverFacade;
    private final List<InternalContainerFactoryProvider> containerFactories;

    private NodeContext(final Builder builder) {
        maxDepth = builder.maxDepth;
        rootTypeMap = builder.rootTypeMap;
        ignoredSelectorMap = builder.ignoredSelectorMap;
        subtypeSelectorMap = builder.subtypeSelectorMap;
        subtypeMappingFromSettings = builder.subtypeMappingFromSettings;
        assignmentOriginSelectors = builder.assignmentOriginSelectors;
        containerFactories = builder.containerFactories;
        typeResolverFacade = new TypeResolverFacade(builder.providerEntries);
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public Map<TypeVariable<?>, Type> getRootTypeMap() {
        return rootTypeMap;
    }

    public Set<TargetSelector> getAssignmentOriginSelectors(final InternalNode node) {
        return assignmentOriginSelectors.getOriginSelectors(node);
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

    public List<InternalContainerFactoryProvider> getContainerFactories() {
        return containerFactories;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int maxDepth;
        private Map<TypeVariable<?>, Type> rootTypeMap = Collections.emptyMap();
        private BooleanSelectorMap ignoredSelectorMap;
        private SubtypeSelectorMap subtypeSelectorMap;
        private BooleanSelectorMap assignmentOriginSelectors;
        private Map<Class<?>, Class<?>> subtypeMappingFromSettings = Collections.emptyMap();
        private List<InternalContainerFactoryProvider> containerFactories = Collections.emptyList();
        private List<ProviderEntry<TypeResolver>> providerEntries = Collections.emptyList();

        private Builder() {
        }

        public Builder maxDepth(final int maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }

        public Builder rootTypeMap(final Map<TypeVariable<?>, Type> rootTypeMap) {
            this.rootTypeMap = rootTypeMap;
            return this;
        }

        public Builder ignoredSelectorMap(final BooleanSelectorMap ignoredSelectorMap) {
            this.ignoredSelectorMap = ignoredSelectorMap;
            return this;
        }

        public Builder subtypeSelectorMap(final SubtypeSelectorMap subtypeSelectorMap) {
            this.subtypeSelectorMap = subtypeSelectorMap;
            return this;
        }

        public Builder assignmentOriginSelectors(final BooleanSelectorMap assignmentOriginSelectors) {
            this.assignmentOriginSelectors = assignmentOriginSelectors;
            return this;
        }

        public Builder subtypeMappingFromSettings(final Map<Class<?>, Class<?>> subtypeMappingFromSettings) {
            this.subtypeMappingFromSettings = subtypeMappingFromSettings;
            return this;
        }

        public Builder containerFactories(final List<InternalContainerFactoryProvider> containerFactories) {
            this.containerFactories = containerFactories;
            return this;
        }

        public Builder providerEntries(final List<ProviderEntry<TypeResolver>> providerEntries) {
            this.providerEntries = providerEntries;
            return this;
        }

        public NodeContext build() {
            return new NodeContext(this);
        }
    }
}

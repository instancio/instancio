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

import org.instancio.internal.context.ModelContext;
import org.instancio.internal.spi.InternalExtension;
import org.instancio.internal.spi.InternalExtension.InternalNodeFilter;
import org.instancio.internal.spi.InternalExtension.InternalNodeFilter.Decision;
import org.instancio.internal.util.StringUtils;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

final class NodePruner {

    private final ModelContext modelContext;
    private final List<Pattern> ignorePatterns;
    private final List<InternalNodeFilter> nodeFilters;

    NodePruner(final ModelContext modelContext) {
        this.modelContext = modelContext;
        this.ignorePatterns = getIgnorePatterns(modelContext.getSettings());
        this.nodeFilters = getNodeFilters(modelContext);
    }

    @Nullable
    InternalNode apply(@Nullable final InternalNode node) {
        if (node == null) {
            return null;
        }

        final Decision decision = getDecision(node);
        return switch (decision) {
            case KEEP -> node;
            case IGNORE -> node.toBuilder().nodeKind(NodeKind.IGNORED).build();
            case PRUNE -> null;
        };
    }

    private Decision getDecision(final InternalNode node) {
        if (isExcludedViaSettingsIgnorePattern(node.getField())) {
            return Decision.PRUNE;
        }
        if (modelContext.isIgnored(node)) {
            return Decision.IGNORE;
        }

        for (InternalNodeFilter nodeFilter : nodeFilters) {
            // include node unless any filter wants to ignore or prune it
            final Decision decision = nodeFilter.resolve(node);

            if (decision != Decision.KEEP) {
                return decision;
            }
        }
        return Decision.KEEP;
    }

    private boolean isExcludedViaSettingsIgnorePattern(@Nullable final Field field) {
        if (field != null && !ignorePatterns.isEmpty()) {
            final String fieldName = field.getName();
            for (Pattern p : ignorePatterns) {
                if (p.matcher(fieldName).matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<InternalNodeFilter> getNodeFilters(final ModelContext modelContext) {
        final List<InternalExtension> extensions = modelContext.getInternalExtensions();
        final List<InternalNodeFilter> filters = new ArrayList<>(extensions.size());
        for (InternalExtension extension : extensions) {
            final InternalNodeFilter filter = extension.getNodeFilter();
            if (filter != null) {
                filters.add(filter);
            }
        }
        return filters;
    }

    private static List<Pattern> getIgnorePatterns(final Settings settings) {
        final List<String> regexes = StringUtils.split(settings.get(Keys.IGNORE_FIELD_NAME_REGEXES));
        if (regexes.isEmpty()) {
            return Collections.emptyList();
        }

        final List<Pattern> patterns = new ArrayList<>(regexes.size());
        for (String regex : regexes) {
            patterns.add(Pattern.compile(regex));
        }
        return Collections.unmodifiableList(patterns);
    }
}

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
package org.instancio.internal.selectors;

import org.instancio.PredicateSelector;
import org.instancio.TargetSelector;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Format;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static org.instancio.internal.util.ObjectUtils.defaultIfNull;

public final class PredicateSelectorImpl implements PredicateSelector, Flattener, UnusedSelectorDescription {
    private static final int FIELD_PRIORITY = 1;
    private static final int TYPE_PRIORITY = 2;
    private static final String DEFAULT_SELECTOR_DESCRIPTION = "<selector>";
    private static final Predicate<Field> NON_NULL_FIELD = Objects::nonNull;
    private static final Predicate<Class<?>> NON_NULL_TYPE = Objects::nonNull;

    private final int priority;
    private final Predicate<InternalNode> nodePredicate;
    private final String apiInvocationDescription;
    private final Throwable stackTraceHolder;

    private PredicateSelectorImpl(final Builder builder) {
        priority = builder.priority;
        nodePredicate = builder.nodePredicate;
        apiInvocationDescription = defaultIfNull(builder.apiInvocationDescription, DEFAULT_SELECTOR_DESCRIPTION);
        stackTraceHolder = defaultIfNull(builder.stackTraceHolder, Throwable::new);
    }

    @Override
    public List<TargetSelector> flatten() {
        return Collections.singletonList(this);
    }

    @Override
    public String getDescription() {
        return String.format("%s%n    at %s", this, Format.firstNonInstancioStackTraceLine(stackTraceHolder));
    }

    /**
     * Returns the priority of this predicate selector,
     * with lower numbers having a higher priority.
     *
     * @return priority of this selector
     */
    public int getPriority() {
        return priority;
    }

    public Predicate<InternalNode> getNodePredicate() {
        return nodePredicate;
    }

    @Override
    public String toString() {
        return apiInvocationDescription;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int priority;
        private Predicate<InternalNode> nodePredicate;
        private String apiInvocationDescription;
        private Throwable stackTraceHolder;

        private Builder() {
        }

        public Builder fieldPredicate(final Predicate<Field> predicate) {
            this.priority = FIELD_PRIORITY;
            this.nodePredicate = node -> NON_NULL_FIELD.and(predicate).test(node.getField());
            if (this.apiInvocationDescription == null) {
                this.apiInvocationDescription = "fields(Predicate<Field>)";
            }
            return this;
        }

        public Builder typePredicate(final Predicate<Class<?>> predicate) {
            this.priority = TYPE_PRIORITY;
            this.nodePredicate = node -> NON_NULL_TYPE.and(predicate).test(node.getTargetClass());
            if (this.apiInvocationDescription == null) {
                this.apiInvocationDescription = "types(Predicate<Class>)";
            }
            return this;
        }

        public Builder apiInvocationDescription(final String apiInvocationDescription) {
            this.apiInvocationDescription = apiInvocationDescription;
            return this;
        }

        public Builder stackTraceHolder(final Throwable stackTraceHolder) {
            this.stackTraceHolder = stackTraceHolder;
            return this;
        }

        public PredicateSelectorImpl build() {
            return new PredicateSelectorImpl(this);
        }
    }
}

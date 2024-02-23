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
package org.instancio.internal.selectors;

import org.instancio.GroupableSelector;
import org.instancio.PredicateSelector;
import org.instancio.Scope;
import org.instancio.ScopeableSelector;
import org.instancio.TargetSelector;
import org.instancio.internal.Flattener;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Format;
import org.instancio.internal.util.Verify;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static org.instancio.internal.util.ObjectUtils.defaultIfNull;

public class PredicateSelectorImpl
        implements PredicateSelector, Flattener<TargetSelector>, UnusedSelectorDescription {

    private static final int FIELD_PRIORITY = 1;
    private static final int TYPE_PRIORITY = 2;
    private static final String DEFAULT_SELECTOR_DESCRIPTION = "<selector>";
    private static final Predicate<Field> NON_NULL_FIELD = Objects::nonNull;
    private static final Predicate<Class<?>> NON_NULL_TYPE = Objects::nonNull;

    private final int priority;
    private final Predicate<InternalNode> nodePredicate;
    private final List<Scope> scopes;
    private final SelectorDepth selectorDepth;
    private final boolean isLenient;
    private final String apiInvocationDescription;
    private final Throwable stackTraceHolder;

    protected PredicateSelectorImpl(
            final int priority,
            final Predicate<InternalNode> nodePredicate,
            final List<Scope> scopes,
            final SelectorDepth selectorDepth,
            final boolean isLenient,
            final String apiInvocationDescription,
            final Throwable stackTraceHolder) {

        this.priority = priority;
        this.nodePredicate = nodePredicate;
        this.scopes = Collections.unmodifiableList(scopes);
        this.selectorDepth = selectorDepth;
        this.isLenient = isLenient;
        this.apiInvocationDescription = apiInvocationDescription;
        this.stackTraceHolder = stackTraceHolder;
    }

    private PredicateSelectorImpl(final Builder builder) {
        this(
                builder.priority,
                builder.nodePredicate,
                builder.scopes,
                builder.selectorDepth,
                builder.isLenient,
                defaultIfNull(builder.apiInvocationDescription, DEFAULT_SELECTOR_DESCRIPTION),
                defaultIfNull(builder.stackTraceHolder, Throwable::new)
        );
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

    @NotNull
    public List<Scope> getScopes() {
        return scopes;
    }

    public Predicate<InternalNode> getNodePredicate() {
        return nodePredicate;
    }

    @Override
    public ScopeableSelector atDepth(final int depth) {
        return builder(this).depth(depth).build();
    }

    @Override
    public ScopeableSelector atDepth(final Predicate<Integer> depthPredicate) {
        return builder(this).depth(depthPredicate).build();
    }

    public boolean isLenient() {
        return isLenient;
    }

    @Override
    public ScopeableSelector lenient() {
        return builder(this).lenient().build();
    }

    @Override
    public Scope toScope() {
        return new PredicateScopeImpl(this);
    }

    @Override
    public GroupableSelector within(final Scope... scopes) {
        return builder(this).scopes(Arrays.asList(scopes)).build();
    }

    @Override
    public String toString() {
        String s = apiInvocationDescription;

        // Note: currently predicate selector supports
        // either atDepth() or within() but not both
        if (selectorDepth != null) {
            final String depth = selectorDepth.getDepth() == null
                    ? "Predicate<Integer>"
                    : selectorDepth.getDepth().toString();

            s += ".atDepth(" + depth + ")";
        }
        if (!scopes.isEmpty()) {
            s += ".within(" + Format.formatScopes(scopes) + ")";
        }
        return s;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final PredicateSelectorImpl copy) {
        Builder builder = new Builder();
        builder.priority = copy.priority;
        builder.nodePredicate = copy.nodePredicate;
        builder.scopes = copy.scopes;
        builder.apiInvocationDescription = copy.apiInvocationDescription;
        builder.stackTraceHolder = copy.stackTraceHolder;
        builder.selectorDepth = copy.selectorDepth;
        builder.isLenient = copy.isLenient;
        return builder;
    }

    public static final class Builder {
        private int priority;
        private Predicate<InternalNode> nodePredicate = Objects::nonNull;
        private List<Scope> scopes = new ArrayList<>(0);
        private SelectorDepth selectorDepth;
        private boolean isLenient;
        private String apiInvocationDescription;
        private Throwable stackTraceHolder;

        private Builder() {
        }

        public Builder fieldPredicate(final Predicate<Field> predicate) {
            this.priority = FIELD_PRIORITY;
            this.nodePredicate = this.nodePredicate.and(node ->
                    NON_NULL_FIELD.and(predicate).test(node.getField()));

            if (this.apiInvocationDescription == null) {
                this.apiInvocationDescription = "fields(Predicate<Field>)";
            }
            return this;
        }

        public Builder typePredicate(final Predicate<Class<?>> predicate) {
            this.priority = TYPE_PRIORITY;
            this.nodePredicate = this.nodePredicate.and(node ->
                    NON_NULL_TYPE.and(predicate).test(node.getTargetClass()));

            if (this.apiInvocationDescription == null) {
                this.apiInvocationDescription = "types(Predicate<Class>)";
            }
            return this;
        }

        public Builder scopes(final List<Scope> scopes) {
            this.scopes = scopes;
            return this;
        }

        Builder depth(final int depth) {
            return withDepth(new SelectorDepth(depth));
        }

        Builder depth(final Predicate<Integer> predicate) {
            return withDepth(new SelectorDepth(predicate));
        }

        Builder lenient() {
            this.isLenient = true;
            return this;
        }

        private Builder withDepth(final SelectorDepth selectorDepth) {
            // This check is solely for internal code because the
            // public API shouldn't allow atDepth() to be chained multiple times.
            // Setting depth more than once would lead to hard-to-debug issues
            // because the selector toString() would be, e.g. 'atDepth(3)',
            // while the underlying predicate is comprised of multiple,
            // potentially conflicting, depth conditions.
            Verify.state(this.selectorDepth == null, "depth already set!");

            this.selectorDepth = selectorDepth;
            this.nodePredicate = this.nodePredicate.and(selectorDepth.getDepthPredicate());
            return this;
        }

        /**
         * Should only be used for setting a custom description, e.g. {@code "myPredicateSelector()"}.
         */
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

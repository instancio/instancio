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
package org.instancio.internal.selectors;

import org.instancio.GroupableSelector;
import org.instancio.PredicateSelector;
import org.instancio.Scope;
import org.instancio.ScopeableSelector;
import org.instancio.Select;
import org.instancio.TargetSelector;
import org.instancio.internal.ApiMethodSelector;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Format;
import org.instancio.internal.util.Verify;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static org.instancio.internal.util.ObjectUtils.defaultIfNull;

public class PredicateSelectorImpl implements InternalSelector, PredicateSelector {

    private static final String DEFAULT_SELECTOR_DESCRIPTION = "<selector>";
    private static final Predicate<@Nullable Field> NON_NULL_FIELD = Objects::nonNull;
    private static final Predicate<@Nullable Class<?>> NON_NULL_TYPE = Objects::nonNull;
    private static final PredicateSelectorImpl ROOT_SELECTOR = builder()
            .depth(0)
            .target(TargetRoot.INSTANCE)
            .apiInvocationDescription("root()")
            .build();

    private final @Nullable ApiMethodSelector apiMethodSelector;
    private final List<Scope> scopes;
    private final Throwable stackTraceHolder;
    private final boolean isLenient;
    private final boolean isHiddenFromVerboseOutput;
    private final int priority;
    private final Predicate<InternalNode> nodePredicate;
    private final @Nullable SelectorDepth selectorDepth;
    private final String apiInvocationDescription;
    // target can be null for lambda-style predicates, e.g. `types(t -> t == Foo.class)`
    private final @Nullable Target target;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    protected PredicateSelectorImpl(
            @Nullable final ApiMethodSelector apiMethodSelector,
            final int priority,
            final Predicate<InternalNode> nodePredicate,
            final List<Scope> scopes,
            @Nullable final SelectorDepth selectorDepth,
            final boolean isLenient,
            final boolean isHiddenFromVerboseOutput,
            final String apiInvocationDescription,
            @Nullable final Target target,
            final Throwable stackTraceHolder) {

        this.apiMethodSelector = apiMethodSelector;
        this.scopes = Collections.unmodifiableList(scopes);
        this.stackTraceHolder = stackTraceHolder;
        this.isLenient = isLenient;
        this.isHiddenFromVerboseOutput = isHiddenFromVerboseOutput;
        this.priority = priority;
        this.nodePredicate = nodePredicate;
        this.selectorDepth = selectorDepth;
        this.apiInvocationDescription = apiInvocationDescription;
        this.target = target;
    }

    private PredicateSelectorImpl(final Builder builder) {
        this(
                builder.apiMethodSelector,
                builder.priority,
                builder.nodePredicate,
                builder.scopes,
                builder.selectorDepth,
                builder.isLenient,
                builder.isHiddenFromVerboseOutput,
                defaultIfNull(builder.apiInvocationDescription, DEFAULT_SELECTOR_DESCRIPTION),
                builder.target,
                defaultIfNull(builder.stackTraceHolder, Throwable::new));
    }

    // avoid naming the method 'root()' so it doesn't appear in IDE completion suggestions
    // which can be confused with the public API method 'Select.root()'
    public static PredicateSelectorImpl createRootSelector() {
        return ROOT_SELECTOR;
    }

    @Nullable
    @Override
    public ApiMethodSelector getApiMethodSelector() {
        return apiMethodSelector;
    }

    @Override
    public final List<Scope> getScopes() {
        return scopes;
    }

    @Override
    public final List<TargetSelector> flatten() {
        return Collections.singletonList(this);
    }

    @Override
    public final boolean isLenient() {
        return isLenient;
    }

    @Override
    public final boolean isHiddenFromVerboseOutput() {
        return isHiddenFromVerboseOutput;
    }

    @Override
    public final String getDescription() {
        return String.format("%s%n    at %s", this, Format.firstNonInstancioStackTraceLine(getStackTraceHolder()));
    }

    public final Throwable getStackTraceHolder() {
        return stackTraceHolder;
    }

    @Override
    public boolean isRootSelector() {
        return target == TargetRoot.INSTANCE;
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

    protected final String getApiInvocationDescription() {
        return apiInvocationDescription;
    }

    @Nullable
    public Target getTarget() {
        return target;
    }

    public Predicate<InternalNode> getNodePredicate() {
        return nodePredicate;
    }

    @Override
    public ScopeableSelector atDepth(final int depth) {
        return toBuilder().depth(depth).build();
    }

    @Override
    public ScopeableSelector atDepth(final Predicate<Integer> depthPredicate) {
        return toBuilder().depth(depthPredicate).build();
    }

    @Override
    public ScopeableSelector lenient() {
        return toBuilder().lenient().build();
    }

    @Override
    public Scope toScope() {
        return new PredicateScopeImpl(this);
    }

    @Override
    public GroupableSelector within(final Scope... scopes) {
        return toBuilder().scopes(Arrays.asList(scopes)).build();
    }

    @Override
    public String toString() {
        String s = apiInvocationDescription;

        // Fall back to target's toString() when no explicit description was set
        // (e.g. for unprocessed selectors created via Select.setter(Person::setName))
        if (target != null && DEFAULT_SELECTOR_DESCRIPTION.equals(s)) {
            s = target.toString();
        }

        if (isRootSelector()) {
            // Scope and depth are not applicable to root selector
            // so just return the description as is.
            return s;
        }

        if (selectorDepth != null) {
            s += ".atDepth(" + selectorDepth.getDescription() + ")";
        }
        if (!getScopes().isEmpty()) {
            s += ".within(" + Format.formatScopes(getScopes()) + ")";
        }
        if (isLenient()) {
            s += ".lenient()";
        }
        return s;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof PredicateSelectorImpl that)) return false;
        return priority == that.priority
                && Objects.equals(apiInvocationDescription, that.apiInvocationDescription)
                && Objects.equals(selectorDepth, that.selectorDepth)
                && Objects.equals(target, that.target)
                && Objects.equals(apiMethodSelector, that.apiMethodSelector)
                && Objects.equals(isLenient, that.isLenient)
                && Objects.equals(scopes, that.scopes)
                && Objects.equals(isHiddenFromVerboseOutput, that.isHiddenFromVerboseOutput);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(priority, apiInvocationDescription, selectorDepth, target,
                apiMethodSelector, isLenient, scopes, isHiddenFromVerboseOutput);
    }

    /**
     * Some predicate selectors come with a {@code null} predicate because
     * the root class is not known at the time of construction,
     * e.g. {@link Select#field(String)}, {@link Select#setter(String)}, etc.
     *
     * <p>This method rebuilds it with the additional context.
     *
     * @param targetContext containing the root class
     * @return a builder with changes applied (if applicable)
     */
    public Builder toBuilderWithContext(final Target.TargetContext targetContext) {
        final Builder builder = toBuilder();

        if (target instanceof TargetFieldName || target instanceof TargetGetterReference) {
            final TargetField withRootClass = (TargetField) target.withRootClass(targetContext);

            builder.target(withRootClass)
                    .fieldPredicate(f -> f.equals(withRootClass.getField()))
                    .apiInvocationDescription(withRootClass.toString());

        } else if (target instanceof TargetSetterName || target instanceof TargetSetterReference) {
            final TargetSetter withRootClass = (TargetSetter) target.withRootClass(targetContext);

            builder.target(withRootClass)
                    .andSetterPredicate(m -> m.equals(withRootClass.getSetter()))
                    .apiInvocationDescription(withRootClass.toString());
        }

        return builder;
    }

    public Builder toBuilder() {
        Builder builder = new Builder();
        builder.apiMethodSelector = getApiMethodSelector();
        builder.priority = priority;
        builder.nodePredicate = nodePredicate;
        builder.scopes = getScopes();
        builder.apiInvocationDescription = apiInvocationDescription;
        builder.target = target;
        builder.stackTraceHolder = getStackTraceHolder();
        builder.selectorDepth = selectorDepth;
        builder.isLenient = isLenient();
        builder.isHiddenFromVerboseOutput = isHiddenFromVerboseOutput();
        return builder;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private @Nullable ApiMethodSelector apiMethodSelector;
        private int priority;
        private Predicate<InternalNode> nodePredicate = any -> true; // init for 'and' chaining
        private List<Scope> scopes = new ArrayList<>(0);
        private @Nullable SelectorDepth selectorDepth;
        private boolean isLenient;
        private boolean isHiddenFromVerboseOutput;
        private @Nullable String apiInvocationDescription;
        private @Nullable Target target;
        private @Nullable Throwable stackTraceHolder;

        private Builder() {
        }

        public Builder apiMethodSelector(@Nullable final ApiMethodSelector apiMethodSelector) {
            this.apiMethodSelector = apiMethodSelector;
            return this;
        }

        public Builder priority(final int priority) {
            this.priority = priority;
            return this;
        }

        public Builder fieldPredicate(final Predicate<Field> predicate) {
            andFieldPredicate(predicate);

            if (this.apiInvocationDescription == null) {
                this.apiInvocationDescription = "fields(Predicate<Field>)";
            }
            return this;
        }

        public Builder typePredicate(final Predicate<Class<?>> predicate) {
            this.nodePredicate = this.nodePredicate.and(node -> {
                final Class<?> targetClass = node.getTargetClass();
                final Class<?> rawType = node.getRawType();
                return NON_NULL_TYPE.and(predicate).test(targetClass)
                        || NON_NULL_TYPE.and(predicate).test(rawType);
            });

            if (this.apiInvocationDescription == null) {
                this.apiInvocationDescription = "types(Predicate<Class>)";
            }
            return this;
        }

        private Builder andFieldPredicate(final Predicate<Field> predicate) {
            this.nodePredicate = this.nodePredicate.and(node ->
                    NON_NULL_FIELD.and(predicate).test(node.getField()));
            return this;
        }

        private Builder andSetterPredicate(final Predicate<Method> predicate) {
            this.nodePredicate = this.nodePredicate.and(node -> {
                final Method setter = node.getSetter();
                return setter != null && predicate.test(setter);
            });
            return this;
        }

        public Builder scopes(final List<Scope> scopes) {
            this.scopes = scopes;
            return this;
        }

        public Builder depth(final int depth) {
            return withDepth(new SelectorDepth(depth));
        }

        Builder depth(final Predicate<Integer> predicate) {
            return withDepth(new SelectorDepth(predicate));
        }

        public Builder lenient() {
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

        public Builder apiInvocationDescription(final String apiInvocationDescription) {
            this.apiInvocationDescription = apiInvocationDescription;
            return this;
        }

        public Builder target(final Target target) {
            this.target = target;
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

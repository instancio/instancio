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
import org.instancio.Scope;
import org.instancio.ScopeableSelector;
import org.instancio.Selector;
import org.instancio.internal.ApiMethodSelector;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.util.Format;
import org.instancio.internal.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class SelectorImpl extends BaseSelector implements Selector, GroupableSelector {

    private final Target target;
    private final Selector parent;
    private final Integer depth;
    private int hash;

    /**
     * Three types of selectors can be created: field, method, class.
     * Which arguments are provided depends on the type of selector.
     *
     * @param apiMethodSelector the API method the selector was passed to
     * @param target            selector's target
     * @param scopes            optional scopes specified top-down, from outermost to innermost
     * @param parent            required only for {@link PrimitiveAndWrapperSelectorImpl} for checking unused selectors
     * @param stackTraceHolder  stacktrace for reporting locations of unused selectors
     */
    private SelectorImpl(
            final ApiMethodSelector apiMethodSelector,
            final Target target,
            @NotNull final List<Scope> scopes,
            @Nullable final Selector parent,
            @NotNull final Throwable stackTraceHolder,
            @Nullable final Integer depth,
            final boolean isLenient) {

        super(apiMethodSelector, scopes, stackTraceHolder, isLenient, /*isHiddenFromVerboseOutput*/ false);
        this.target = target;
        this.parent = parent;
        this.depth = depth;
    }

    private SelectorImpl(final Builder builder) {
        this(
                builder.apiMethodSelector,
                builder.target,
                ObjectUtils.defaultIfNull(builder.scopes, Collections.emptyList()),
                builder.parent,
                ObjectUtils.defaultIfNull(builder.stackTraceHolder, Throwable::new),
                builder.depth,
                builder.isLenient);
    }

    // avoid naming the method 'root()' so it doesn't appear in IDE completion suggestions
    // which can be confused with the public API method 'Selector.root()'
    public static SelectorImpl createRootSelector() {
        return builder(TargetRoot.INSTANCE)
                .depth(0)
                .build();
    }

    public Target getTarget() {
        return target;
    }

    @Override
    public Selector atDepth(final int depth) {
        return toBuilder()
                .depth(ApiValidator.validateDepth(depth))
                .build();
    }

    @Override
    public ScopeableSelector lenient() {
        return toBuilder().lenient().build();
    }

    @Override
    public Selector within(@NotNull final Scope... scopes) {
        return toBuilder().scopes(Arrays.asList(scopes)).build();
    }

    @Override
    public Scope toScope() {
        return new ScopeImpl(target, depth);
    }

    public Selector getParent() {
        return parent;
    }

    public Class<?> getTargetClass() {
        return target.getTargetClass();
    }

    public Integer getDepth() {
        return depth;
    }

    @Override
    public boolean isRootSelector() {
        return target == TargetRoot.INSTANCE;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof SelectorImpl that)) return false;
        return Objects.equals(getTarget(), that.getTarget())
               && Objects.equals(getScopes(), that.getScopes())
               && Objects.equals(getDepth(), that.getDepth());
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = computeHashCode();
        }
        return hash;
    }

    private int computeHashCode() {
        int result = getTarget().hashCode();
        result = 31 * result + getScopes().hashCode();
        result = 31 * result + (getDepth() == null ? 0 : getDepth().hashCode());
        return result;
    }

    @Override
    public String toString() {
        if (parent instanceof PrimitiveAndWrapperSelectorImpl) {
            return parent.toString();
        }
        if (target instanceof TargetRoot) {
            return "root()";
        }

        final StringBuilder sb = new StringBuilder(128);
        sb.append(target);

        if (depth != null) {
            sb.append(".atDepth(").append(depth).append(')');
        }
        if (!getScopes().isEmpty()) {
            sb.append(".within(").append(Format.formatScopes(getScopes())).append(')');
        }
        if (isLenient()) {
            sb.append(".lenient()");
        }
        return sb.toString();
    }

    public Builder toBuilder(final Target target) {
        Builder builder = new Builder(target);
        builder.apiMethodSelector = this.getApiMethodSelector();
        builder.scopes = this.getScopes();
        builder.parent = this.parent;
        builder.stackTraceHolder = this.getStackTraceHolder();
        builder.depth = this.depth;
        builder.isLenient = this.isLenient();
        return builder;
    }

    public Builder toBuilder() {
        return toBuilder(target);
    }

    public static Builder builder(final Target target) {
        return new Builder(target);
    }

    public static final class Builder {
        private final Target target;
        private ApiMethodSelector apiMethodSelector;
        private List<Scope> scopes;
        private Selector parent;
        private Throwable stackTraceHolder;
        private Integer depth;
        private boolean isLenient;

        private Builder(final Target target) {
            this.target = target;
        }

        public Builder apiMethodSelector(final ApiMethodSelector apiMethodSelector) {
            this.apiMethodSelector = apiMethodSelector;
            return this;
        }

        public Builder scopes(final List<Scope> scopes) {
            this.scopes = scopes;
            return this;
        }

        public Builder parent(final Selector parent) {
            this.parent = parent;
            return this;
        }

        public Builder stackTraceHolder(final Throwable stackTraceHolder) {
            this.stackTraceHolder = stackTraceHolder;
            return this;
        }

        public Builder depth(final Integer depth) {
            this.depth = depth;
            return this;
        }

        public Builder lenient() {
            this.isLenient = true;
            return this;
        }

        public SelectorImpl build() {
            return new SelectorImpl(this);
        }
    }
}

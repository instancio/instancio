/*
 * Copyright 2022 the original author or authors.
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
import org.instancio.Selector;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class SelectorImpl implements Selector, GroupableSelector, Flattener {

    private final SelectorTargetKind selectorTargetKind;
    private final Class<?> targetClass;
    private final String fieldName;
    private final List<Scope> scopes;
    private final Selector parent;

    /**
     * Constructor.
     *
     * @param selectorTargetKind selector target's kind
     * @param targetClass        target class
     * @param fieldName          field name, applicable to field selectors only
     * @param scopes             scopes specified top-down, from outermost to innermost
     * @param parent             required only for {@link PrimitiveAndWrapperSelectorImpl} for checking unused selectors
     */
    public SelectorImpl(final SelectorTargetKind selectorTargetKind,
                        @Nullable final Class<?> targetClass,
                        @Nullable final String fieldName,
                        @Nullable final List<Scope> scopes,
                        @Nullable final Selector parent) {

        this.selectorTargetKind = selectorTargetKind;
        this.targetClass = targetClass;
        this.fieldName = fieldName;
        this.scopes = scopes == null ? Collections.emptyList() : Collections.unmodifiableList(scopes);
        this.parent = parent;
    }

    public SelectorImpl(final SelectorTargetKind selectorTargetKind,
                        @Nullable final Class<?> targetClass,
                        @Nullable final String fieldName) {

        this(selectorTargetKind, targetClass, fieldName, Collections.emptyList(), null);
    }

    @Override
    public Selector within(final Scope... scopes) {
        return new SelectorImpl(selectorTargetKind, targetClass, fieldName, Arrays.asList(scopes), parent);
    }

    @Override
    public Scope toScope() {
        return new ScopeImpl(targetClass, fieldName);
    }

    @Override
    public List<SelectorImpl> flatten() {
        return Collections.singletonList(this);
    }

    public Selector getParent() {
        return parent;
    }

    public List<Scope> getScopes() {
        return scopes;
    }

    public SelectorTargetKind selectorType() {
        return selectorTargetKind;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public String getFieldName() {
        return fieldName;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Performs equality check with all fields except {@code parent}.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof SelectorImpl)) return false;
        final SelectorImpl that = (SelectorImpl) o;
        return selectorTargetKind == that.selectorTargetKind
                && Objects.equals(getTargetClass(), that.getTargetClass())
                && Objects.equals(getFieldName(), that.getFieldName())
                && Objects.equals(getScopes(), that.getScopes());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Calculates hashcode using all fields except {@code parent}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(selectorTargetKind, getTargetClass(), getFieldName(), getScopes());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Selector[(");

        if (targetClass != null) {
            sb.append(targetClass.getSimpleName());
        }
        if (fieldName != null) {
            if (targetClass != null) {
                sb.append(", ");
            }
            sb.append('"').append(fieldName).append('"');
        }
        sb.append(')');

        if (!scopes.isEmpty()) {
            sb.append(", ").append(scopesToString());
        }
        return sb.append(']').toString();
    }

    private String scopesToString() {
        return scopes.stream()
                .map(ScopeImpl.class::cast)
                .map(s -> s.getField() == null
                        ? String.format("scope(%s)", s.getTargetClass().getSimpleName())
                        : String.format("scope(%s, \"%s\")", s.getTargetClass().getSimpleName(), s.getField().getName()))
                .collect(Collectors.joining(", "));
    }
}

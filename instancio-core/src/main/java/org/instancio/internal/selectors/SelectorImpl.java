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

    private final SelectorTargetType selectorTargetType;
    private final Class<?> targetClass;
    private final String fieldName;
    private List<Scope> scopes;

    public SelectorImpl(final SelectorTargetType selectorTargetType,
                        @Nullable final Class<?> targetClass,
                        @Nullable final String fieldName,
                        @Nullable final List<Scope> scopes) {

        this.selectorTargetType = selectorTargetType;
        this.targetClass = targetClass;
        this.fieldName = fieldName;
        this.scopes = scopes;
    }

    public SelectorImpl(final SelectorTargetType selectorTargetType,
                        @Nullable final Class<?> targetClass,
                        @Nullable final String fieldName) {

        this(selectorTargetType, targetClass, fieldName, Collections.emptyList());
    }

    @Override
    public Selector within(final Scope... scopes) {
        this.scopes = Collections.unmodifiableList(Arrays.asList(scopes));
        return this;
    }

    public List<Scope> getScopes() {
        return scopes;
    }

    @Override
    public Scope toScope() {
        return new ScopeImpl(targetClass, fieldName);
    }

    public SelectorTargetType selectorType() {
        return selectorTargetType;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public List<SelectorImpl> flatten() {
        return Collections.singletonList(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof SelectorImpl)) return false;
        final SelectorImpl that = (SelectorImpl) o;
        return selectorTargetType == that.selectorTargetType
                && Objects.equals(getTargetClass(), that.getTargetClass())
                && Objects.equals(getFieldName(), that.getFieldName())
                && Objects.equals(getScopes(), that.getScopes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectorTargetType, getTargetClass(), getFieldName(), getScopes());
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

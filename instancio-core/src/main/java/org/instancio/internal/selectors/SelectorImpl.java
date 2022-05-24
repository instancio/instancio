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
import org.instancio.util.Format;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SelectorImpl implements Selector, GroupableSelector, Flattener {

    private final SelectorTargetKind selectorTargetKind;
    private final Class<?> targetClass;
    private final String fieldName;
    private final List<Scope> scopes;
    private final Selector parent;
    private final Throwable stackTraceHolder;

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
                        @Nullable final Selector parent,
                        @Nullable final Throwable stackTraceHolder) {

        this.selectorTargetKind = selectorTargetKind;
        this.targetClass = targetClass;
        this.fieldName = fieldName;
        this.scopes = scopes == null ? Collections.emptyList() : Collections.unmodifiableList(scopes);
        this.parent = parent;
        this.stackTraceHolder = stackTraceHolder;
    }

    public SelectorImpl(final SelectorTargetKind selectorTargetKind,
                        @Nullable final Class<?> targetClass,
                        @Nullable final String fieldName,
                        @Nullable final List<Scope> scopes,
                        @Nullable final Selector parent) {

        this(selectorTargetKind, targetClass, fieldName, scopes, parent, new Throwable());
    }

    public SelectorImpl(final SelectorTargetKind selectorTargetKind,
                        @Nullable final Class<?> targetClass,
                        @Nullable final String fieldName) {

        this(selectorTargetKind, targetClass, fieldName, Collections.emptyList(), null);
    }

    public Throwable getStackTraceHolder() {
        return stackTraceHolder;
    }

    /**
     * Returns the line where this selector was declared in client code.
     * Used for reporting unused selectors.
     *
     * @return the first non-Instancio stacktrace element as a string.
     */
    public String getStackTraceLine() {
        for (StackTraceElement element : stackTraceHolder.getStackTrace()) {
            if (!element.getClassName().startsWith("org.instancio")) {
                return element.toString();
            }
        }
        return "<unknown location>";
    }

    @Override
    public Selector within(final Scope... scopes) {
        return new SelectorImpl(selectorTargetKind, targetClass, fieldName, Arrays.asList(scopes), parent, stackTraceHolder);
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

    public SelectorTargetKind getSelectorTargetKind() {
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
     * Performs equality check with all fields except {@code parent} and {@code stackTraceHolder}.
     */
    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof SelectorImpl)) return false;
        final SelectorImpl that = (SelectorImpl) o;
        return selectorTargetKind == that.selectorTargetKind
                && Objects.equals(targetClass, that.targetClass)
                && Objects.equals(fieldName, that.fieldName)
                && Objects.equals(scopes, that.scopes);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Calculates hashcode using all fields except {@code parent} and {@code stackTraceHolder}.
     */
    @Override
    public final int hashCode() {
        return Objects.hash(selectorTargetKind, targetClass, fieldName, scopes);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        if (getParent() instanceof PrimitiveAndWrapperSelectorImpl) {
            if (targetClass.isPrimitive()) {
                sb.append(getParent());
            }
        } else {
            sb.append(selectorTargetKind == SelectorTargetKind.CLASS ? "all(" : "field(");

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
                sb.append(", ").append(Format.scopes(scopes));
            }
        }
        return sb.toString();
    }
}

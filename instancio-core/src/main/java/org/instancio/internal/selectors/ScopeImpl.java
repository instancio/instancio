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

import org.instancio.Scope;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.Verify;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Objects;

public final class ScopeImpl implements Scope {
    private final Class<?> targetClass;
    private final String fieldName;
    private final Integer depth;

    public ScopeImpl(@Nullable final Class<?> targetClass,
                     @Nullable final String fieldName,
                     @Nullable final Integer depth) {

        this.targetClass = targetClass;
        this.fieldName = fieldName;
        this.depth = depth;
    }

    public ScopeImpl(@Nullable final Class<?> targetClass,
                     @Nullable final String fieldName) {

        this(targetClass, fieldName, null);
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Integer getDepth() {
        return depth;
    }

    public boolean isFieldScope() {
        return fieldName != null;
    }

    public Field resolveField() {
        Verify.state(isFieldScope(), "Invalid call to resolve field on Class scope: %s", this);
        return ReflectionUtils.getField(targetClass, fieldName);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ScopeImpl)) return false;
        final ScopeImpl scope = (ScopeImpl) o;
        return Objects.equals(targetClass, scope.targetClass)
                && Objects.equals(fieldName, scope.fieldName)
                && Objects.equals(depth, scope.depth);
    }

    @Override
    public int hashCode() {
        int result = targetClass == null ? 0 : targetClass.hashCode();
        result = 31 * result + (fieldName == null ? 0 : fieldName.hashCode());
        result = 31 * result + (depth == null ? 0 : depth.hashCode());
        return result;
    }

    @Override
    public String toString() {
        String s = "scope(" + targetClass.getSimpleName();
        if (fieldName != null) {
            s += ", \"" + fieldName + '"';
        }
        if (depth != null) {
            s += ", atDepth(" + depth + ')';
        }
        return s + ')';
    }
}

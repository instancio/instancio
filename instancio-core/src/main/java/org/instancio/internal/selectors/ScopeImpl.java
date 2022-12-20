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

import org.instancio.Scope;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.Verify;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Objects;

public final class ScopeImpl implements Scope {
    private final Class<?> targetClass;
    private final String fieldName;

    public ScopeImpl(final Class<?> targetClass, @Nullable final String fieldName) {
        this.targetClass = targetClass;
        this.fieldName = fieldName;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public String getFieldName() {
        return fieldName;
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
                && Objects.equals(fieldName, scope.fieldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetClass, fieldName);
    }

    @Override
    public String toString() {
        if (fieldName == null) {
            return String.format("scope(%s)", targetClass.getSimpleName());
        }
        return String.format("scope(%s, \"%s\")", getTargetClass().getSimpleName(), fieldName);
    }
}

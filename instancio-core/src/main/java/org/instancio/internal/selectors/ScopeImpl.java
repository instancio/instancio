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
import org.instancio.util.ReflectionUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Objects;

public final class ScopeImpl implements Scope {
    private final Class<?> targetClass;
    private final Field field;

    public ScopeImpl(final Class<?> targetClass, @Nullable final String fieldName) {
        this.targetClass = targetClass;
        this.field = fieldName == null ? null : ReflectionUtils.getField(targetClass, fieldName);
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Field getField() {
        return field;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ScopeImpl)) return false;
        final ScopeImpl scope = (ScopeImpl) o;
        return Objects.equals(getTargetClass(), scope.getTargetClass()) && Objects.equals(getField(), scope.getField());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTargetClass(), getField());
    }

    @Override
    public String toString() {
        if (field == null) {
            return String.format("scope(%s)", targetClass.getSimpleName());
        }
        return String.format("scope(%s, \"%s\")", getTargetClass().getSimpleName(), field.getName());
    }
}

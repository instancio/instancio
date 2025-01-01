/*
 * Copyright 2022-2025 the original author or authors.
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

import org.instancio.internal.util.Fail;
import org.instancio.internal.util.ObjectUtils;
import org.instancio.internal.util.ReflectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Objects;

public final class TargetFieldName implements Target {

    private final Class<?> targetClass;
    private final String fieldName;

    public TargetFieldName(
            @Nullable final Class<?> targetClass,
            @NotNull final String fieldName) {

        this.targetClass = targetClass;
        this.fieldName = fieldName;
    }

    @Override
    public Class<?> getTargetClass() {
        return targetClass;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public Target withRootClass(final TargetContext targetContext) {
        final Class<?> resolvedTargetClass = ObjectUtils.defaultIfNull(
                targetClass, targetContext.getRootClass());

        final Field field = ReflectionUtils.getFieldOrNull(resolvedTargetClass, fieldName);

        if (field != null) {
            return new TargetField(field);
        }

        throw Fail.withUsageError("invalid field '%s' for %s", fieldName, resolvedTargetClass);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof TargetFieldName)) return false;

        final TargetFieldName that = (TargetFieldName) o;

        return Objects.equals(targetClass, that.targetClass)
                && Objects.equals(fieldName, that.fieldName);
    }

    @Override
    public int hashCode() {
        int result = targetClass != null ? targetClass.hashCode() : 0;
        result = 31 * result + (fieldName != null ? fieldName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String s = "field(";
        if (targetClass != null) {
            s += targetClass.getSimpleName() + ", ";
        }
        s += '"' + fieldName + "\")";
        return s;
    }
}

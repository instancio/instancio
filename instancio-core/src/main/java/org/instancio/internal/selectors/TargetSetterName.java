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

import org.instancio.internal.util.ObjectUtils;
import org.instancio.internal.util.ReflectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Objects;

public final class TargetSetterName implements Target {

    private final Class<?> targetClass;
    private final String methodName;
    private final Class<?> parameterType;

    public TargetSetterName(
            @Nullable final Class<?> targetClass,
            @NotNull final String methodName,
            @Nullable final Class<?> parameterType) {

        this.targetClass = targetClass;
        this.methodName = methodName;
        this.parameterType = parameterType;
    }

    @Override
    public Class<?> getTargetClass() {
        return targetClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    @Override
    public Target withRootClass(final TargetContext targetContext) {
        final Class<?> resolvedTargetClass = ObjectUtils.defaultIfNull(
                targetClass, targetContext.getRootClass());

        final Method method = ReflectionUtils.getSetterMethod(
                resolvedTargetClass, methodName, parameterType);

        return new TargetSetter(method);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof TargetSetterName that)) return false;

        return Objects.equals(targetClass, that.targetClass)
               && Objects.equals(methodName, that.methodName)
               && Objects.equals(parameterType, that.parameterType);
    }

    @Override
    public int hashCode() {
        int result = targetClass != null ? targetClass.hashCode() : 0;
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        result = 31 * result + (parameterType != null ? parameterType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String s = "setter(";

        if (targetClass != null) {
            s += targetClass.getSimpleName() + ", ";
        }

        s += '"' + methodName;
        if (parameterType != null) {
            s += '(' + parameterType.getSimpleName() + ')';
        }

        return s + "\")";
    }
}

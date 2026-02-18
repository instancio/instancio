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

import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Objects;

public final class TargetSetter implements Target {

    private final Method setter;
    private final @Nullable Class<?> parameterType;

    public TargetSetter(final Method setter) {
        this.setter = setter;
        this.parameterType = setter.getParameterTypes()[0];
    }

    @Override
    public Class<?> getTargetClass() {
        return setter.getDeclaringClass();
    }

    public Method getSetter() {
        return setter;
    }

    public @Nullable Class<?> getParameterType() {
        return parameterType;
    }

    @Override
    public ScopelessSelector toScopelessSelector() {
        return new ScopelessSelector(setter.getDeclaringClass(), setter);
    }

    @Override
    @SuppressWarnings("PMD.SimplifyBooleanReturns")
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof TargetSetter that)) return false;
        return Objects.equals(setter, that.setter);
    }

    @Override
    public int hashCode() {
        return setter == null ? 0 : setter.hashCode();
    }

    @Override
    public String toString() {
        String s = "setter(";
        s += setter.getDeclaringClass().getSimpleName() + ", ";
        s += '"' + setter.getName();
        s += '(' + (parameterType == null ? "n/a" : parameterType.getSimpleName()) + ')';
        return s + "\")";
    }
}

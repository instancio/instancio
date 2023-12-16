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

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Objects;

public final class TargetSetter implements Target {

    private final Method setter;
    private final Class<?> parameterType;

    public TargetSetter(@NotNull final Method setter) {
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

    public Class<?> getParameterType() {
        return parameterType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof TargetSetter)) return false;

        final TargetSetter that = (TargetSetter) o;
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
        s += '(' + parameterType.getSimpleName() + ')';
        return s + "\")";
    }
}

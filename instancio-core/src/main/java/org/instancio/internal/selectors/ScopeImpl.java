/*
 * Copyright 2022-2024 the original author or authors.
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
import org.instancio.internal.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ScopeImpl implements Scope {
    private final Target target;
    private final Integer depth;

    public ScopeImpl(@NotNull final Target target, @Nullable final Integer depth) {
        this.target = target;
        this.depth = depth;
    }

    public Target getTarget() {
        return target;
    }

    public Class<?> getTargetClass() {
        return target.getTargetClass();
    }

    public Field getField() {
        return ((TargetField) target).getField();
    }

    public String getMethodName() {
        return ((TargetSetter) target).getSetter().getName();
    }

    public Class<?> getParameterType() {
        return ((TargetSetter) target).getParameterType();
    }

    public Integer getDepth() {
        return depth;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ScopeImpl)) return false;
        final ScopeImpl scope = (ScopeImpl) o;
        return Objects.equals(target, scope.target)
                && Objects.equals(depth, scope.depth);
    }

    @Override
    public int hashCode() {
        int result = target.hashCode();
        result = 31 * result + (depth == null ? 0 : depth.hashCode());
        return result;
    }

    @Override
    public String toString() {
        final List<String> elements = new ArrayList<>(4);

        if (target.getTargetClass() != null) {
            elements.add(target.getTargetClass().getSimpleName());
        }
        if (target instanceof TargetField) {
            TargetField t = (TargetField) target;
            elements.add(StringUtils.quoteToString(t.getField().getName()));
        }
        if (target instanceof TargetFieldName) {
            TargetFieldName t = (TargetFieldName) target;
            elements.add(StringUtils.quoteToString(t.getFieldName()));
        }
        if (target instanceof TargetSetter) {
            TargetSetter t = (TargetSetter) target;
            String methodName = t.getSetter().getName();
            elements.add(t.getParameterType() == null
                    ? methodName
                    : String.format("%s(%s)", methodName, t.getParameterType().getSimpleName()));
        }
        if (target instanceof TargetSetterName) {
            TargetSetterName t = (TargetSetterName) target;
            elements.add(t.getParameterType() == null
                    ? t.getMethodName()
                    : String.format("%s(%s)", t.getMethodName(), t.getParameterType().getSimpleName()));
        }
        if (depth != null) {
            elements.add(String.format("atDepth(%s)", depth));
        }
        return "scope(" + String.join(", ", elements) + ')';
    }
}

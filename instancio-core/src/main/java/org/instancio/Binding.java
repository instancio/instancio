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
package org.instancio;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Immutable
public class Binding {
    private enum BindingType {TYPE, FIELD}

    private final List<BindingTarget> targets;

    Binding(final List<BindingTarget> targets) {
        this.targets = Collections.unmodifiableList(targets);
    }

    private Binding(final BindingTarget... targets) {
        this(Arrays.asList(targets));
    }

    private static Binding of(final BindingTarget... targets) {
        return new Binding(targets);
    }

    public static Binding fieldBinding(@Nullable final Class<?> targetType, final String fieldName) {
        return of(new BindingTarget(BindingType.FIELD, targetType, fieldName));
    }

    public static Binding fieldBinding(final String fieldName) {
        return of(new BindingTarget(BindingType.FIELD, null, fieldName));
    }

    public static Binding typeBinding(final Class<?> targetType) {
        return of(new BindingTarget(BindingType.TYPE, targetType, null));
    }

    public List<BindingTarget> getTargets() {
        return targets;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Binding)) return false;
        final Binding binding = (Binding) o;
        return Objects.equals(targets, binding.targets);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(targets);
    }

    @Immutable
    public static class BindingTarget {
        private final BindingType bindingType;
        private final Class<?> targetType;
        private final String fieldName;

        private BindingTarget(final BindingType bindingType,
                              @Nullable final Class<?> targetType,
                              @Nullable final String fieldName) {

            this.bindingType = bindingType;
            this.targetType = targetType;
            this.fieldName = fieldName;
        }

        public boolean isFieldBinding() {
            return bindingType == BindingType.FIELD;
        }

        public Class<?> getTargetType() {
            return targetType;
        }

        public String getFieldName() {
            return fieldName;
        }

        @Override
        public final boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof BindingTarget)) return false;
            final BindingTarget that = (BindingTarget) o;
            return bindingType == that.bindingType
                    && Objects.equals(targetType, that.targetType)
                    && Objects.equals(fieldName, that.fieldName);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(bindingType, targetType, fieldName);
        }
    }
}

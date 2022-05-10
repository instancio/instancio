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

import org.instancio.Select;
import org.instancio.TargetSelector;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Objects;

public final class ScopelessSelector implements TargetSelector {
    private final Class<?> targetClass;
    private final Field field;

    public ScopelessSelector(final Class<?> targetClass, @Nullable final Field field) {
        this.targetClass = targetClass;
        this.field = field;
    }

    public ScopelessSelector(final Class<?> targetClass) {
        this(targetClass, null);
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Field getField() {
        return field;
    }

    public TargetSelector asSelector() {
        return field == null ? Select.all(targetClass) : Select.field(field.getDeclaringClass(), field.getName());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ScopelessSelector)) return false;
        final ScopelessSelector selector = (ScopelessSelector) o;
        return Objects.equals(getTargetClass(), selector.getTargetClass())
                && Objects.equals(getField(), selector.getField());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTargetClass(), getField());
    }
}

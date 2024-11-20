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

import java.lang.reflect.Field;
import java.util.Objects;

public final class TargetField implements Target {

    private final Field field;

    public TargetField(final Field field) {
        this.field = field;
    }

    @Override
    public Class<?> getTargetClass() {
        return field.getDeclaringClass();
    }

    public Field getField() {
        return field;
    }

    @Override
    public ScopelessSelector toScopelessSelector() {
        return new ScopelessSelector(field.getDeclaringClass(), field);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof TargetField)) return false;

        final TargetField that = (TargetField) o;
        return Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
        return field != null ? field.hashCode() : 0;
    }

    @Override
    public String toString() {
        String s = "field(";
        s += field.getDeclaringClass().getSimpleName() + ", ";
        s += '"' + field.getName() + "\")";
        return s;
    }
}

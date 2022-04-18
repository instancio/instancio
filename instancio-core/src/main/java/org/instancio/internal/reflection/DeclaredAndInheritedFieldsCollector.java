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
package org.instancio.internal.reflection;

import org.instancio.util.Verify;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects declared and super class fields, excluding static fields.
 */
public class DeclaredAndInheritedFieldsCollector implements FieldCollector {

    private static final ClassFilter classFilter = new DefaultClassFilter();

    @Override
    public List<Field> getFields(final Class<?> klass) {
        Class<?> next = Verify.notNull(klass, "Class is null");

        final List<Field> collected = new ArrayList<>();
        while (shouldCollectFrom(next)) {
            for (Field field : next.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    collected.add(field);
                }
            }
            next = next.getSuperclass();
        }

        return collected;
    }

    private boolean shouldCollectFrom(@Nullable final Class<?> next) {
        if (next == null || next.isInterface() || next.isArray() || next == Object.class) {
            return false;
        }

        return !classFilter.isExcluded(next);
    }
}

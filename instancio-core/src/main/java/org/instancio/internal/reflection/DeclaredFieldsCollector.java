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
package org.instancio.internal.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects declared, non-static fields.
 */
public class DeclaredFieldsCollector implements FieldCollector {

    @Override
    public List<Field> getFields(Class<?> klass) {
        final List<Field> collected = new ArrayList<>();

        for (Field field : klass.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                collected.add(field);
            }
        }
        return collected;
    }
}

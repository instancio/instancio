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
package org.instancio.junit.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldAnnotationMap {

    private final Map<Field, List<Annotation>> map = new HashMap<>();

    public FieldAnnotationMap(final Class<?> klass) {
        for (Field field : klass.getDeclaredFields()) {
            map.put(field, ReflectionUtils.collectionAnnotations(field));
        }
    }

    public List<Annotation> get(final Field field) {
        return map.get(field);
    }
}

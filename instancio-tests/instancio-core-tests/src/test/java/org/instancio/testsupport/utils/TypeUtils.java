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
package org.instancio.testsupport.utils;

import java.lang.reflect.TypeVariable;

public class TypeUtils {

    public static TypeVariable<?> getTypeVar(Class<?> klass, String typeParameter) {
        for (TypeVariable<?> tvar : klass.getTypeParameters()) {
            if (tvar.getName().equals(typeParameter)) {
                return tvar;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid type parameter '%s' for %s", typeParameter, klass));
    }
}

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
package org.instancio.internal.context;

import org.instancio.internal.ApiValidator;
import org.instancio.internal.util.TypeUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class ModelContextHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ModelContextHelper.class);

    static Map<TypeVariable<?>, Type> buildRootTypeMap(
            final Type rootType,
            final List<Type> rootTypeParameters) {

        final Class<?> rootClass = TypeUtils.getRawType(rootType);
        ApiValidator.validateTypeParameters(rootClass, rootTypeParameters);

        final Class<?> targetClass = rootClass.isArray()
                ? rootClass.getComponentType()
                : rootClass;

        final TypeVariable<?>[] typeVariables = targetClass.getTypeParameters();
        final Map<TypeVariable<?>, Type> typeMap = new HashMap<>();

        for (int i = 0; i < typeVariables.length; i++) {
            final TypeVariable<?> typeVariable = typeVariables[i];
            final Type actualType = rootTypeParameters.get(i);
            LOG.trace("Mapping type variable '{}' to '{}'", typeVariable, actualType);
            typeMap.put(typeVariable, actualType);
        }

        populateTypeMapFromGenericSuperclass(rootClass, typeMap);
        return typeMap;
    }

    private static void populateTypeMapFromGenericSuperclass(
            @Nullable final Class<?> rootClass,
            final Map<TypeVariable<?>, Type> typeMap) {

        if (rootClass == null) {
            return;
        }

        final Type gsClass = rootClass.getGenericSuperclass();
        if (gsClass instanceof ParameterizedType) {
            final ParameterizedType genericSuperclass = (ParameterizedType) gsClass;
            final Class<?> rawType = TypeUtils.getRawType(genericSuperclass.getRawType());
            final TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
            final Type[] actualTypeArguments = genericSuperclass.getActualTypeArguments();

            for (int i = 0; i < typeParameters.length; i++) {
                if (actualTypeArguments[i] instanceof TypeVariable) {
                    continue; // leave resolving the type to NodeFactory
                }
                final TypeVariable<?> typeVariable = typeParameters[i];
                final Class<?> actualType = TypeUtils.getRawType(actualTypeArguments[i]);
                LOG.trace("Mapping type variable '{}' to '{}'", typeVariable, actualType);
                typeMap.put(typeVariable, actualType);
            }
        }

        if (gsClass != null) {
            populateTypeMapFromGenericSuperclass(TypeUtils.getRawType(gsClass), typeMap);
        }
    }

    private ModelContextHelper() {
        // non-instantiable
    }
}

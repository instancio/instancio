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
package org.instancio.internal.context;

import org.instancio.internal.ApiValidator;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.internal.util.ErrorMessageUtils;
import org.instancio.internal.util.Fail;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

final class FillObjectHelper {

    private FillObjectHelper() {
        // non-instantiable
    }

    static List<Type> getTypeArgs(final Object object) {
        if (object.getClass().getTypeParameters().length == 0) {
            return Collections.emptyList();
        }

        final Type[] typeArgs;

        // Supporting all generic types probably won't be possible.
        // For now, only handle known types like Collection and Map.
        // Handling other types will require a type token.
        if (object instanceof Collection<?> collection) {
            typeArgs = getCollectionTypeArgs(collection);
        } else if (object instanceof Map<?, ?> map) {
            typeArgs = getMapTypeArgs(map);
        } else {
            throw Fail.withUsageError(ErrorMessageUtils.fillParameterizedType(object.getClass()));
        }

        ApiValidator.doesNotContainNull(typeArgs, () ->
                "the fill() method cannot resolve type arguments from the given "
                        + object.getClass().getSimpleName() + " instance because it contains null value(s)");

        return CollectionUtils.asUnmodifiableList(typeArgs);
    }

    private static Type[] getCollectionTypeArgs(final Collection<?> collection) {
        ApiValidator.isFalse(collection.isEmpty(), "cannot fill() an empty collection");

        final Type[] typeArgs = new Type[1];

        for (Object element : collection) {
            if (typeArgs[0] == null) {
                typeArgs[0] = getType(element);
            }
            if (typeArgs[0] != null) {
                break;
            }
        }
        return typeArgs;
    }

    private static Type[] getMapTypeArgs(final Map<?, ?> map) {
        ApiValidator.isFalse(map.isEmpty(), "cannot fill() an empty map");

        final Type[] typeArgs = new Type[2];

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (typeArgs[0] == null) {
                typeArgs[0] = getType(entry.getKey());
            }
            if (typeArgs[1] == null) {
                typeArgs[1] = getType(entry.getValue());
            }
            if (typeArgs[0] != null && typeArgs[1] != null) {
                break;
            }
        }
        return typeArgs;
    }

    private static Class<?> getType(final Object o) {
        return o == null ? null : o.getClass();
    }
}

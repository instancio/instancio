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
package org.instancio.test.features.generator.custom.populate;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.internal.util.TypeUtils;
import org.instancio.internal.util.Verify;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import static org.instancio.Select.types;

/**
 * Helper class for creating a model from an object with the goal
 * of populating and/or customising the object's values.
 */
final class PopulateHelper {

    private PopulateHelper() {
        // non-instantiable
    }

    /**
     * Helper class for extracting the type of object returned by a supplier.
     */
    private static final class ObjectSupplier<T> implements Supplier<T> {
        private final Supplier<T> supplier;
        private final Class<?> objectType;
        private final T first;
        private boolean wasFirstSupplied;

        private ObjectSupplier(final Supplier<T> supplier) {
            this.supplier = supplier;
            this.first = supplier.get();
            this.objectType = first.getClass();
        }

        Class<?> getObjectType() {
            return objectType;
        }

        @Override
        public T get() {
            if (!wasFirstSupplied) {
                wasFirstSupplied = true;
                return first;
            }
            return supplier.get();
        }
    }

    private static class SupplierBackedGenerator<T> implements Generator<T> {
        private final Supplier<T> supplier;
        private final Hints hints;

        SupplierBackedGenerator(final Supplier<T> supplier, final Hints hints) {
            this.supplier = supplier;
            this.hints = hints;
        }

        @Override
        public T generate(final Random random) {
            return supplier.get();
        }

        @Override
        public Hints hints() {
            return hints;
        }
    }

    static <T> Model<T> populate(final T object, final AfterGenerate afterGenerate) {
        final TypeVariable<?>[] typeParams = object.getClass().getTypeParameters();
        final Class<?>[] typeArgs = new Class[typeParams.length];

        // Supporting all generic types probably won't be possible.
        // For now, only handle known types like Collection and Map.
        // Handling other types will require a type token.
        if (typeParams.length > 0) {
            if (object instanceof Collection<?>) {
                final Collection<?> collection = (Collection<?>) object;
                Verify.isFalse(collection.isEmpty(), "Empty collection");

                for (Object element : collection) {
                    if (element != null) {
                        typeArgs[0] = element.getClass();
                        break;
                    }
                }
            } else if (object instanceof Map<?, ?>) {
                final Map<?, ?> map = (Map<?, ?>) object;
                Verify.isFalse(map.isEmpty(), "Empty map");

                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    final Object k = entry.getKey();
                    final Object v = entry.getValue();

                    if (k != null && typeArgs[0] == null) {
                        typeArgs[0] = k.getClass();
                    }
                    if (v != null && typeArgs[1] == null) {
                        typeArgs[1] = v.getClass();
                    }
                    if (typeArgs[0] != null && typeArgs[1] != null) {
                        break;
                    }
                }
            }

            for (Class<?> typeArg : typeArgs) {
                Verify.notNull(typeArg, "null type argument");
            }
        }

        final Class<?> rootClass = object.getClass();
        final Supplier<T> supplier = () -> object;
        return createModel(rootClass, typeArgs, supplier, afterGenerate);
    }

    static <T> Model<T> populate(final Supplier<T> supplier, final AfterGenerate afterGenerate) {
        final ObjectSupplier<T> objectSupplier = new ObjectSupplier<>(supplier);
        final Type type = objectSupplier.getObjectType();
        final Class<?>[] typeArgs = {};
        return createModel(TypeUtils.getRawType(type), typeArgs, objectSupplier, afterGenerate);
    }

    @SuppressWarnings("unchecked")
    private static <T> Model<T> createModel(
            final Class<?> rootClass,
            final Class<?>[] typeArgs,
            final Supplier<T> supplier,
            final AfterGenerate afterGenerate) {

        final SupplierBackedGenerator<T> generator = new SupplierBackedGenerator<>(
                supplier, Hints.afterGenerate(afterGenerate));

        return (Model<T>) Instancio.of(rootClass)
                .withTypeParameters(typeArgs)
                // Usually depth will be zero since rootClass will actually be the root.
                // The exception is when using ofList()/ofSet() APIs,
                // in which case the root will be the collection,
                // and object to populate will be at depth 1.
                .generate(types().of(rootClass).atDepth(d -> d <= 1), generator)
                .toModel();
    }
}

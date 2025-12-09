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
package org.instancio.junit.internal;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.InstancioFeedApi;
import org.instancio.Random;
import org.instancio.documentation.InternalApi;
import org.instancio.feed.Feed;
import org.instancio.internal.util.TypeUtils;
import org.instancio.internal.util.Verify;
import org.instancio.junit.GivenProvider;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@InternalApi
public class ObjectCreator {

    @Nullable
    private final Settings settings;
    private final Random random;

    public ObjectCreator(final @Nullable Settings settings, final @Nullable Random random) {
        this.settings = settings;
        this.random = Verify.notNull(random, "null random");
    }

    public Object createObject(
            final AnnotatedElement annotatedElement,
            final Type targetType,
            final ElementAnnotations elementAnnotations) {

        final List<Class<? extends GivenProvider>> providerClasses = elementAnnotations.getProviderClasses();
        final Class<?> targetClass = TypeUtils.getRawType(targetType);
        final Type actualTargetType = targetClass == Supplier.class || targetClass == Stream.class
                ? ((ParameterizedType) targetType).getActualTypeArguments()[0] //NOSONAR
                : targetType;

        final Supplier<?> supplier;

        if (providerClasses.isEmpty()) {
            supplier = () -> instancioCreate(actualTargetType);
        } else {
            supplier = () -> {
                final InternalElementContext elementContext = new InternalElementContext(
                        annotatedElement, actualTargetType, elementAnnotations, random);

                final GivenProvider provider = ReflectionUtils.newInstance(random.oneOf(providerClasses));
                return provider.provide(elementContext);
            };
        }

        if (targetClass == Supplier.class) {
            return supplier;
        }
        if (targetClass == Stream.class) {
            return Stream.generate(supplier);
        }
        return supplier.get();
    }

    @SuppressWarnings("rawtypes")
    private Object instancioCreate(final Type targetType) {
        final Class targetClass = TypeUtils.getRawType(targetType);
        final long nextSeed = random.longRange(1, Long.MAX_VALUE);

        if (Feed.class.isAssignableFrom(targetClass)) {
            final InstancioFeedApi<?> api = Instancio.ofFeed(targetClass);
            if (settings != null) {
                api.withSettings(settings);
            }
            return api
                    .withSetting(Keys.SEED, nextSeed)
                    .create();
        } else {
            final InstancioApi<?> api = Instancio.of(() -> targetType);
            if (settings != null) {
                api.withSettings(settings);
            }
            return api
                    .withSeed(nextSeed)
                    .create();
        }
    }
}

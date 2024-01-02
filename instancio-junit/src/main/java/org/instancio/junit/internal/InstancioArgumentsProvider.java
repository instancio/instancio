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

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Random;
import org.instancio.junit.InstancioSource;
import org.instancio.settings.Settings;
import org.instancio.support.ThreadLocalRandom;
import org.instancio.support.ThreadLocalSettings;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * A provider that generates arguments for {@link InstancioSource}.
 */
public class InstancioArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<InstancioSource> {

    @Override
    public void accept(final InstancioSource instancioSource) {
        // no-op - don't need anything from ths annotation
    }

    @Override
    public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
        final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.getInstance();
        final ThreadLocalSettings threadLocalSettings = ThreadLocalSettings.getInstance();

        ExtensionSupport.processAnnotations(context, threadLocalRandom, threadLocalSettings);

        final Random random = threadLocalRandom.get();
        final Settings settings = threadLocalSettings.get();
        final Type[] paramTypes = context.getRequiredTestMethod().getGenericParameterTypes();
        final Object[] args = createObjects(paramTypes, random, settings);
        return Stream.of(Arguments.of(args));
    }

    static Object[] createObjects(final Type[] types, final Random random, final Settings settings) {
        return Arrays.stream(types)
                .map(type -> createObject(type, random, settings))
                .toArray();
    }

    static Object createObject(final Type type, final Random random, final Settings settings) {
        final InstancioApi<?> api = Instancio.of(() -> type);
        if (settings != null) {
            api.withSettings(settings);
        }
        return api
                .withSeed(random.longRange(1, Long.MAX_VALUE))
                .create();
    }
}
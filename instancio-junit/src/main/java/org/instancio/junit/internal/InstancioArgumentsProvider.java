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
import org.instancio.InstancioFeedApi;
import org.instancio.Random;
import org.instancio.feed.Feed;
import org.instancio.internal.util.TypeUtils;
import org.instancio.junit.InstancioSource;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.support.Global;
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

    private InstancioSource instancioSource;

    @Override
    public void accept(final InstancioSource instancioSource) {
        this.instancioSource = instancioSource;
    }

    @Override
    public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
        final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.getInstance();
        final ThreadLocalSettings threadLocalSettings = ThreadLocalSettings.getInstance();

        ExtensionSupport.processAnnotations(context, threadLocalRandom, threadLocalSettings);

        final Random random = threadLocalRandom.get();
        final Settings settings = threadLocalSettings.get();
        final Type[] paramTypes = context.getRequiredTestMethod().getGenericParameterTypes();
        final int samples = getNumberOfSamples(settings);
        return Stream
                .generate(() -> Arguments.of(createObjects(paramTypes, random, settings)))
                .limit(samples);
    }

    private int getNumberOfSamples(Settings threadLocalSettings) {
        if (instancioSource.samples() > 0) {
            return instancioSource.samples();
        }
        if (threadLocalSettings != null) {
            final Integer samples = threadLocalSettings.get(Keys.INSTANCIO_SOURCE_SAMPLES);
            if (samples != null) {
                return samples;
            }
        }
        return Global.getPropertiesFileSettings().get(Keys.INSTANCIO_SOURCE_SAMPLES);
    }

    static Object[] createObjects(final Type[] types, final Random random, final Settings settings) {
        return Arrays.stream(types)
                .map(type -> createObject(type, random, settings))
                .toArray();
    }

    @SuppressWarnings("rawtypes")
    static Object createObject(final Type type, final Random random, final Settings settings) {
        final Class rawType = TypeUtils.getRawType(type);

        if (Feed.class.isAssignableFrom(rawType)) {
            final InstancioFeedApi<?> api = Instancio.ofFeed(rawType);
            if (settings != null) {
                api.withSettings(settings);
            }
            return api
                    .withSetting(Keys.SEED, random.longRange(1, Long.MAX_VALUE))
                    .create();
        } else {
            final InstancioApi<?> api = Instancio.of(() -> type);
            if (settings != null) {
                api.withSettings(settings);
            }
            return api
                    .withSeed(random.longRange(1, Long.MAX_VALUE))
                    .create();
        }
    }
}
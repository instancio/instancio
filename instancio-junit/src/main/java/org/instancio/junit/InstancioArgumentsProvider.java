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
package org.instancio.junit;

import org.instancio.Instancio;
import org.instancio.InstancioOfClassApi;
import org.instancio.Random;
import org.instancio.internal.ThreadLocalRandom;
import org.instancio.internal.ThreadLocalSettings;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;
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
        final Object[] args = createObjectsGroupingByType(instancioSource.value(), random, settings);
        return Stream.of(Arguments.of(args));
    }

    /*
     * Since the seed value is the same for all parameters, parameters of the same type
     * should be generated using a single context. This is to prevent the same value being
     * generated, e.g. @InstancioSource({String.class, String.class}) => "foo", "foo"
     */
    static Object[] createObjectsGroupingByType(final Class<?>[] types, final Random random, final Settings settings) {
        final Map<Class<?>, Long> counts = Arrays.stream(types)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        final Map<Class<?>, Queue<Object>> resultsByType = new LinkedHashMap<>();

        counts.forEach((type, count) -> {
            final InstancioOfClassApi<?> api = Instancio.of(type);
            if (settings != null) {
                api.withSettings(settings);
            }
            final Queue<Object> results = api
                    .withSeed(random.getSeed())
                    .stream()
                    .limit(count)
                    .collect(Collectors.toCollection(ArrayDeque::new));

            resultsByType.put(type, results);
        });

        final Object[] results = new Object[types.length];
        for (int i = 0; i < results.length; i++) {
            results[i] = resultsByType.get(types[i]).poll();
        }
        return results;
    }
}
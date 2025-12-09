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

import org.instancio.Random;
import org.instancio.documentation.Initializer;
import org.instancio.internal.util.Verify;
import org.instancio.junit.InstancioSource;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.support.Global;
import org.instancio.support.ThreadLocalRandom;
import org.instancio.support.ThreadLocalSettings;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.junit.jupiter.params.support.ParameterDeclaration;
import org.junit.jupiter.params.support.ParameterDeclarations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Stream;

import static org.instancio.junit.internal.Constants.INSTANCIO_NAMESPACE;
import static org.instancio.junit.internal.Constants.INSTANCIO_SOURCE_STATE;

/**
 * A provider that generates arguments for {@link InstancioSource}.
 */
public class InstancioSourceArgumentsProvider
        implements ArgumentsProvider, AnnotationConsumer<InstancioSource> {

    private InstancioSource instancioSource;

    @Initializer
    @Override
    public void accept(final InstancioSource instancioSource) {
        this.instancioSource = instancioSource;
    }

    @Override
    public Stream<? extends Arguments> provideArguments(final ParameterDeclarations parameters, final ExtensionContext context) {
        final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.getInstance();
        final ThreadLocalSettings threadLocalSettings = ThreadLocalSettings.getInstance();

        ExtensionSupport.processAnnotations(context, threadLocalRandom, threadLocalSettings);

        final Random random = Verify.notNull(threadLocalRandom.get(), "null random");
        final Settings settings = threadLocalSettings.get();
        final int samples = getNumberOfSamples(settings);

        final InstancioSourceState state = new InstancioSourceState(random.getSeed(), samples);
        context.getStore(INSTANCIO_NAMESPACE).put(INSTANCIO_SOURCE_STATE, state);

        return Stream
                .generate(() -> {
                    state.decrementSamplesRemaining();
                    return Arguments.of(createObjects(parameters, random, settings));
                })
                .limit(samples);
    }

    private int getNumberOfSamples(@Nullable Settings threadLocalSettings) {
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

    private static Object[] createObjects(final ParameterDeclarations parameters, final @Nullable Random random, final @Nullable Settings settings) {
        return parameters.getAll().stream()
                .map(ParameterDeclaration::getAnnotatedElement)
                .map(Parameter.class::cast)
                .map(param -> {
                    final Type targetType = param.getParameterizedType();
                    final List<Annotation> annotations = ReflectionUtils.collectionAnnotations(param);
                    final ElementAnnotations elementAnnotations = new ElementAnnotations(annotations);
                    return new ObjectCreator(settings, random)
                            .createObject(param, targetType, elementAnnotations);
                })
                .toArray();
    }
}
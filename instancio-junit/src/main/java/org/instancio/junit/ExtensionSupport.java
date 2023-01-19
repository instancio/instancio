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

import org.instancio.exception.InstancioApiException;
import org.instancio.internal.ThreadLocalRandom;
import org.instancio.internal.ThreadLocalSettings;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.internal.random.Seeds;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.Sonar;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

final class ExtensionSupport {

    static void processAnnotations(final ExtensionContext context,
                                   final ThreadLocalRandom threadLocalRandom,
                                   final ThreadLocalSettings threadLocalSettings) {
        try {
            ExtensionSupport.processWithSettingsAnnotation(context, threadLocalSettings);
            ExtensionSupport.processSeedAnnotation(context, threadLocalRandom);
        } catch (Exception ex) {
            threadLocalRandom.remove();
            threadLocalSettings.remove();
            throw ex;
        }
    }

    private static void processSeedAnnotation(final ExtensionContext context,
                                              final ThreadLocalRandom threadLocalRandom) {

        final Optional<Method> testMethod = context.getTestMethod();
        if (testMethod.isPresent()) {
            final Seed seedAnnotation = testMethod.get().getAnnotation(Seed.class);
            final long seed;

            if (seedAnnotation != null) {
                seed = seedAnnotation.value();
            } else if (ModelContext.getGlobalRandom() != null) {
                seed = ModelContext.getGlobalRandom().getSeed();
            } else {
                seed = Seeds.randomSeed();
            }

            // each test method gets a new instance of random to avoid
            // the state of the random leaking across tests
            threadLocalRandom.set(new DefaultRandom(seed));
        }
    }

    @SuppressWarnings({Sonar.ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED, "PMD.CyclomaticComplexity"})
    private static void processWithSettingsAnnotation(final ExtensionContext context,
                                                      final ThreadLocalSettings threadLocalSettings) {

        final Optional<Class<?>> testClass = context.getTestClass();
        if (!testClass.isPresent()) {
            return;
        }

        final List<Field> fields = ReflectionUtils.getAnnotatedFields(testClass.get(), WithSettings.class);

        if (fields.size() > 1) {
            throw new InstancioApiException(String.format(
                    "%nFound more than one field annotated '@WithSettings':%n%n%s",
                    fields.stream().map(Field::toString).collect(joining(System.lineSeparator()))));

        } else if (fields.size() == 1) {
            final Field field = fields.get(0);

            // Test instance is not present for parameterized tests, in which case
            // we expect the settings annotation to be on a static field
            final Optional<Object> testInstance = context.getTestInstance();
            final Object settings = ReflectionUtils.getFieldValue(field, testInstance.orElse(null));

            if (testInstance.isPresent() && settings == null) {
                throw new InstancioApiException(String.format(
                        "%n@WithSettings must be annotated on a non-null field."));
            }
            if (settings == null) {
                throw new InstancioApiException(String.format(
                        "%n@WithSettings must be annotated on a non-null field."
                                + "%nIf @WithSettings is used with a @ParameterizedTest, the Settings field must be static."));
            }
            if (!(settings instanceof Settings)) {
                throw new InstancioApiException(String.format(
                        "%n@WithSettings must be annotated on a Settings field."
                                + "%n%nFound annotation on: %s", field));
            }
            threadLocalSettings.set((Settings) settings);
        }
    }

    private ExtensionSupport() {
        // non-instantiable
    }
}

/*
 * Copyright 2022 the original author or authors.
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
import org.instancio.internal.ThreadLocalRandomProvider;
import org.instancio.internal.ThreadLocalSettings;
import org.instancio.internal.random.RandomProviderImpl;
import org.instancio.settings.Settings;
import org.instancio.util.ReflectionUtils;
import org.instancio.util.SeedUtil;
import org.instancio.util.Sonar;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

class ExtensionSupport {

    static void processSeedAnnotation(
            final ExtensionContext context,
            final ThreadLocalRandomProvider threadLocalRandomProvider) {

        if (threadLocalRandomProvider.get() != null) {
            return;
        }
        final Optional<Method> testMethod = context.getTestMethod();
        if (testMethod.isPresent()) {
            final Seed seedAnnotation = testMethod.get().getAnnotation(Seed.class);
            final int seed = seedAnnotation == null
                    ? SeedUtil.randomSeed()
                    : seedAnnotation.value();

            threadLocalRandomProvider.set(new RandomProviderImpl(seed));
        }
    }

    @SuppressWarnings(Sonar.ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED)
    static void processWithSettingsAnnotation(final ExtensionContext context,
                                              final ThreadLocalSettings threadLocalSettings) {
        if (threadLocalSettings.get() != null) {
            return;
        }

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
            final Optional<Object> settings = getFieldValue(field, testInstance.orElse(null));

            if (testInstance.isPresent() && !settings.isPresent()) {
                throw new InstancioApiException(String.format(
                        "%n@WithSettings must be annotated on a non-null field."));
            }
            if (!settings.isPresent()) {
                throw new InstancioApiException(String.format(
                        "%n@WithSettings must be annotated on a non-null field."
                                + "%nIf this error occurred with a @ParameterizedTest," +
                                " then the settings field should be static."));
            }
            if (!(settings.get() instanceof Settings)) {
                throw new InstancioApiException(String.format(
                        "%n@WithSettings must be annotated on a Settings field."
                                + "%n%nFound annotation on: %s", field));
            }
            threadLocalSettings.set((Settings) settings.get());
        }
    }

    private static Optional<Object> getFieldValue(final Field field, final Object target) {
        try {
            field.setAccessible(true);
            return Optional.ofNullable(field.get(target));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

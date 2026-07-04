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
import org.instancio.junit.Seed;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.instancio.support.Global;
import org.instancio.support.InternalTestContext;
import org.instancio.support.Seeds;
import org.instancio.support.ThreadLocalTestContext;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ExtensionSupport {

    public static void processAnnotations(
            final ExtensionContext context,
            final ThreadLocalTestContext threadLocalTestContext) {

        final Settings settings = processWithSettingsAnnotation(context);
        final DefaultRandom random = processSeedAnnotation(context, settings);
        threadLocalTestContext.set(new InternalTestContext(random, settings));
    }

    private static DefaultRandom processSeedAnnotation(
            final ExtensionContext context,
            @Nullable final Settings settings) {

        final Seed seedAnnotation = context.getTestMethod()
                .map(m -> m.getAnnotation(Seed.class))
                .orElse(null);

        final long seed;
        final Seeds.Source source;
        final Long settingsSeed = settings == null ? null : settings.get(Keys.SEED);
        final Random configuredRandom = Global.getConfiguredRandom();

        if (settingsSeed != null) {
            seed = settingsSeed;
            source = Seeds.Source.WITH_SETTINGS_ANNOTATION;
        } else if (seedAnnotation != null) {
            seed = seedAnnotation.value();
            source = Seeds.Source.SEED_ANNOTATION;
        } else if (configuredRandom != null) {
            seed = configuredRandom.getSeed();
            source = Seeds.Source.GLOBAL;
        } else {
            seed = Seeds.randomSeed();
            source = Seeds.Source.RANDOM;
        }

        // each test method gets a new instance of random to avoid
        // the state of the random leaking across tests
        return new DefaultRandom(seed, source);
    }

    @Nullable
    @SuppressWarnings("java:S3011")
    private static Settings processWithSettingsAnnotation(final ExtensionContext context) {
        final List<Class<?>> testClasses = new ArrayList<>(context.getEnclosingTestClasses());
        context.getTestClass().ifPresent(testClasses::add);

        return testClasses.stream()
                .map(t -> findSettings(t, context))
                .flatMap(Optional::stream)
                .reduce(Settings::merge)
                .orElse(null);
    }

    private static Optional<Settings> findSettings(Class<?> testClass, ExtensionContext context) {
        final List<Field> fields = ReflectionUtils.getAnnotatedFields(testClass, WithSettings.class);

        if (fields.size() > 1) {
            throw Fail.multipleAnnotatedFields(fields);
        } else if (fields.isEmpty()) {
            return Optional.empty();
        }

        final Field field = fields.get(0);

        // Test instance is not present for parameterized tests, in which case
        // we expect the settings annotation to be on a static field
        final Optional<Object> testInstance = context.getTestInstances().flatMap(testInstances -> testInstances.findInstance(testClass));
        final Object settings = ReflectionUtils.getFieldValue(field, testInstance.orElse(null));

        if (testInstance.isPresent() && settings == null) {
            throw Fail.withSettingsOnNullField();
        }
        if (settings == null) {
            throw Fail.withSettingsOnNullOrNonStaticField();
        }
        if (!(settings instanceof Settings s)) {
            throw Fail.withSettingsOnWrongFieldType(field);
        }
        return Optional.of(s);
    }

    private ExtensionSupport() {
        // non-instantiable
    }
}

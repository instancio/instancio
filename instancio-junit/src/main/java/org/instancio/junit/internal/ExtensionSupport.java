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

import org.instancio.junit.Seed;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.instancio.support.Global;
import org.instancio.support.Seeds;
import org.instancio.support.ThreadLocalRandom;
import org.instancio.support.ThreadLocalSettings;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public final class ExtensionSupport {

    public static void processAnnotations(
            final ExtensionContext context,
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

    private static void processSeedAnnotation(
            final ExtensionContext context,
            final ThreadLocalRandom threadLocalRandom) {

        final Seed seedAnnotation = context.getTestMethod()
                .map(m -> m.getAnnotation(Seed.class))
                .orElse(null);

        final long seed;
        final Seeds.Source source;
        final Settings tlSettings = ThreadLocalSettings.getInstance().get();
        final Long tlSeed = tlSettings == null ? null : tlSettings.get(Keys.SEED);

        if (tlSeed != null) {
            seed = tlSeed;
            source = Seeds.Source.WITH_SETTINGS_ANNOTATION;
        } else if (seedAnnotation != null) {
            seed = seedAnnotation.value();
            source = Seeds.Source.SEED_ANNOTATION;
        } else if (Global.getConfiguredRandom() != null) {
            seed = Global.getConfiguredRandom().getSeed();
            source = Seeds.Source.GLOBAL;
        } else {
            seed = Seeds.randomSeed();
            source = Seeds.Source.RANDOM;
        }

        // each test method gets a new instance of random to avoid
        // the state of the random leaking across tests
        threadLocalRandom.set(new DefaultRandom(seed, source));
    }

    @SuppressWarnings({"java:S3011", "PMD.CyclomaticComplexity"})
    private static void processWithSettingsAnnotation(
            final ExtensionContext context,
            final ThreadLocalSettings threadLocalSettings) {

        final Optional<Class<?>> testClass = context.getTestClass();
        if (!testClass.isPresent()) {
            return;
        }

        final List<Field> fields = ReflectionUtils.getAnnotatedFields(testClass.get(), WithSettings.class);

        if (fields.size() > 1) {
            throw Fail.multipleAnnotatedFields(fields);

        } else if (fields.size() == 1) {
            final Field field = fields.get(0);

            // Test instance is not present for parameterized tests, in which case
            // we expect the settings annotation to be on a static field
            final Optional<Object> testInstance = context.getTestInstance();
            final Object settings = ReflectionUtils.getFieldValue(field, testInstance.orElse(null));

            if (testInstance.isPresent() && settings == null) {
                throw Fail.withSettingsOnNullField();
            }
            if (settings == null) {
                throw Fail.withSettingsOnNullOrNonStaticField();
            }
            if (!(settings instanceof Settings)) {
                throw Fail.withSettingsOnWrongFieldType(field);
            }
            threadLocalSettings.set((Settings) settings);
        }
    }

    private ExtensionSupport() {
        // non-instantiable
    }
}

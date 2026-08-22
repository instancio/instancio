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
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private static Settings processWithSettingsAnnotation(final ExtensionContext context) {
        // instances are ordered from outermost to innermost, so settings
        // declared by inner classes are overlaid on those of their outer classes
        return context.getRequiredTestInstances().getAllInstances().stream()
                .map(ExtensionSupport::findSettings)
                .flatMap(Optional::stream)
                .reduce(Settings::merge)
                .orElse(null);
    }

    /**
     * Returns the settings declared by the given instance's class and its supertypes,
     * merged such that settings declared by subtypes take precedence.
     */
    private static Optional<Settings> findSettings(final Object testInstance) {
        final List<Field> fields = AnnotationSupport.findAnnotatedFields(
                testInstance.getClass(), WithSettings.class);

        if (fields.isEmpty()) {
            return Optional.empty();
        }

        checkAtMostOneFieldPerDeclaringClass(fields);

        return fields.stream()
                .map(field -> getSettingsValue(field, testInstance))
                .reduce(Settings::merge);
    }

    private static void checkAtMostOneFieldPerDeclaringClass(final List<Field> fields) {
        final Map<Class<?>, List<Field>> fieldsByDeclaringClass = fields.stream()
                .collect(Collectors.groupingBy(Field::getDeclaringClass, LinkedHashMap::new, Collectors.toList()));

        for (List<Field> declaredFields : fieldsByDeclaringClass.values()) {
            if (declaredFields.size() > 1) {
                throw Fail.multipleAnnotatedFields(declaredFields);
            }
        }
    }

    private static Settings getSettingsValue(final Field field, final Object testInstance) {
        final Object settings = ReflectionSupport.tryToReadFieldValue(field, testInstance)
                .toOptional()
                .orElse(null);

        if (settings == null) {
            throw Fail.withSettingsOnNullField();
        }
        if (!(settings instanceof Settings s)) {
            throw Fail.withSettingsOnWrongFieldType(field);
        }
        return s;
    }

    private ExtensionSupport() {
        // non-instantiable
    }
}

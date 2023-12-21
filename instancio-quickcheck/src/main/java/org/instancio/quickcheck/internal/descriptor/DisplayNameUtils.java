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
package org.instancio.quickcheck.internal.descriptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGenerator.Standard;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

/**
 * This class is from the
 * <a href="https://github.com/junit-team/junit5/">JUnit Jupiter</a> library.
 *
 * <p>This is a modified version of
 * {@code org.junit.jupiter.engine.descriptor.DisplayNameUtils}.
 */
final class DisplayNameUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DisplayNameUtils.class);

    private DisplayNameUtils() {
    }

    static Supplier<String> createDisplayNameSupplierForClass(Class<?> testClass) {
        return () -> getDisplayNameGenerator().generateDisplayNameForClass(testClass);
    }

    static String determineDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        final DisplayNameGenerator generator = getDisplayNameGenerator();
        return determineDisplayName(testMethod, () -> generator.generateDisplayNameForMethod(testClass, testMethod));
    }

    static DisplayNameGenerator getDisplayNameGenerator() {
        return DisplayNameGenerator.getDisplayNameGenerator(Standard.class);
    }

    static String determineDisplayName(AnnotatedElement element, Supplier<String> displayNameSupplier) {
        Preconditions.notNull(element, "Annotated element must not be null");
        Optional<DisplayName> displayNameAnnotation = findAnnotation(element, DisplayName.class);
        if (displayNameAnnotation.isPresent()) {
            String displayName = displayNameAnnotation.get().value().trim();

            // TODO [#242] Replace logging with precondition check once we have a proper mechanism for
            // handling validation exceptions during the TestEngine discovery phase.
            if (StringUtils.isBlank(displayName)) {
                LOGGER.warn(() -> String.format(
                        "Configuration error: @DisplayName on [%s] must be declared with a non-empty value.", element));
            } else {
                return displayName;
            }
        }

        // else let a 'DisplayNameGenerator' generate a display name
        return displayNameSupplier.get();
    }
}

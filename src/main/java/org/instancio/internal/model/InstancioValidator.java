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
package org.instancio.internal.model;

import org.instancio.exception.InstancioApiException;
import org.instancio.settings.SettingKey;
import org.instancio.util.ReflectionUtils;
import org.instancio.util.Verify;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InstancioValidator {

    private InstancioValidator() {
        // non-instantiable
    }

    public static void validateTypeParameters(Class<?> rootClass, List<Class<?>> rootTypeParameters) {
        final int typeVarsLength = rootClass.getTypeParameters().length;

        if (typeVarsLength == 0 && !rootTypeParameters.isEmpty()) {
            final String suppliedParams = rootTypeParameters.stream()
                    .map(Class::getSimpleName)
                    .collect(Collectors.joining(", "));

            throw new InstancioApiException(String.format(
                    "\nClass '%s' is not generic." +
                            "\nSpecifying type parameters 'withTypeParameters(%s)` is not valid for this class.",
                    rootClass.getName(), suppliedParams));
        }

        if (typeVarsLength != rootTypeParameters.size()) {
            throw new InstancioApiException(String.format(
                    "\nClass '%s' has %s type parameters: %s." +
                            "\nPlease specify the required type parameters using 'withTypeParameters(Class... types)`",
                    rootClass.getName(),
                    rootClass.getTypeParameters().length,
                    Arrays.toString(rootClass.getTypeParameters())));
        }
    }

    public static void validateSubtypeMapping(final Class<?> from, final Class<?> to) {
        Verify.notNull(from, "'from' class must not be null");
        Verify.notNull(to, "'to' class must not be null");
        if (from == to) {
            throw new InstancioApiException(String.format("Cannot map the class to itself: '%s'", to.getName()));
        }
        if (!from.isAssignableFrom(to)) {
            throw new InstancioApiException(String.format(
                    "Class '%s' is not a subtype of '%s'", to.getName(), from.getName()));
        }

        validateConcreteClass(to);
    }

    public static Class<?> validateConcreteClass(final Class<?> klass) {
        if (!ReflectionUtils.isConcrete(klass)) {
            throw new InstancioApiException(String.format(
                    "Class must not be an interface or abstract class: '%s'", klass.getName()));
        }
        return klass;
    }

    public static void validateSettingKey(final SettingKey key, final Object value) {
        Verify.notNull(key, "Setting key must not be null");
        Verify.notNull(value, "Setting value must not be null");
        Verify.isTrue(key.type() == value.getClass(),
                "The value '%s' is of unexpected type (%s) for key '%s'",
                value, value.getClass().getSimpleName(), key);
    }
}

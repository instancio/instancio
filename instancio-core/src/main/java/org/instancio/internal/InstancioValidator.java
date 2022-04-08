/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.internal;

import org.instancio.exception.InstancioApiException;
import org.instancio.settings.SettingKey;
import org.instancio.util.Format;
import org.instancio.util.ReflectionUtils;
import org.instancio.util.Verify;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class InstancioValidator {

    private InstancioValidator() {
        // non-instantiable
    }

    public static void validateTypeParameters(Class<?> rootClass, List<Class<?>> rootTypeParameters) {
        final int typeVarsLength = rootClass.isArray()
                ? rootClass.getComponentType().getTypeParameters().length
                : rootClass.getTypeParameters().length;

        if (typeVarsLength == 0 && !rootTypeParameters.isEmpty()) {
            final String suppliedParams = Format.paramsToCsv(rootTypeParameters);

            throw new InstancioApiException(String.format(
                    "%nClass '%s' is not generic." +
                            "%nSpecifying type parameters 'withTypeParameters(%s)` is not valid for this class.",
                    rootClass.getName(), suppliedParams));
        }

        if (typeVarsLength != rootTypeParameters.size()) {
            throw new InstancioApiException(String.format(
                    "%nClass '%s' has %s type parameters: %s." +
                            "%nSpecify the required type parameters using 'withTypeParameters(Class... types)`",
                    rootClass.getName(),
                    rootClass.getTypeParameters().length,
                    Arrays.toString(rootClass.getTypeParameters())));
        }

        for (Class<?> param : rootTypeParameters) {
            if (param.getTypeParameters().length > 0) {
                final String suppliedParams = Format.paramsToCsv(rootTypeParameters);
                final String classWithTypeParams = String.format("%s<%s>",
                        param.getSimpleName(), Format.getTypeVariablesCsv(param));

                throw new InstancioApiException(String.format(
                        "%n%n'withTypeParameters(%s)` contains a generic class: %s'" +
                                "%n%nFor nested generics use the type token API, for example:" +
                                "%nMap<String, List<Integer>> map = Instancio.create(new TypeToken<Map<String, List<Integer>>>(){});" +
                                "%n%nor the builder version:" +
                                "%nMap<String, List<Integer>> map = Instancio.of(new TypeToken<Map<String, List<Integer>>>(){}).create();",
                        suppliedParams, classWithTypeParams));
            }
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

    public static <T> T[] notEmpty(@Nullable final T[] array, final String message, final Object... values) {
        isTrue(array != null && array.length > 0, message, values);
        return array;
    }

    public static <T> Collection<T> notEmpty(@Nullable final Collection<T> collection, final String message, final Object... values) {
        isTrue(collection != null && !collection.isEmpty(), message, values);
        return collection;
    }

    public static void isTrue(final boolean condition, final String message, final Object... values) {
        if (!condition) {
            throw new InstancioApiException(String.format(message, values));
        }
    }

}

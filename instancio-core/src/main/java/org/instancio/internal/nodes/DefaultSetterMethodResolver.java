/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.internal.nodes;

import org.instancio.internal.util.Fail;
import org.instancio.settings.Keys;
import org.instancio.settings.SetterStyle;
import org.instancio.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

final class DefaultSetterMethodResolver {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSetterMethodResolver.class);

    private final MethodNameResolver setterNameResolver;

    DefaultSetterMethodResolver(final Settings settings) {
        this.setterNameResolver = getMethodNameResolver(settings.get(Keys.SETTER_STYLE));
    }

    Method getSetter(final Field field) {
        final String methodName = setterNameResolver.resolveFor(field);

        if (methodName != null) {
            try {
                final Class<?> klass = field.getDeclaringClass();
                return klass.getDeclaredMethod(methodName, field.getType());
            } catch (NoSuchMethodException ex) {
                LOG.trace("Could not find matching setter for field '{}'. Expected method '{}' does not exist",
                        field, methodName);
            }
        }
        return null;
    }

    @SuppressWarnings("PMD.ExhaustiveSwitchHasDefault")
    private static MethodNameResolver getMethodNameResolver(final SetterStyle style) {
        switch (style) {
            case SET:
                return new SetterMethodNameWithPrefixResolver("set");
            case WITH:
                return new SetterMethodNameWithPrefixResolver("with");
            case PROPERTY:
                return new SetterMethodNameNoPrefixResolver();
            default:
                throw Fail.withFataInternalError("Unhandled setter style: %s", style);
        }
    }
}

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
package org.instancio.internal.assigners;

import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.Format;
import org.instancio.settings.Keys;
import org.instancio.settings.SetterStyle;
import org.instancio.settings.Settings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.instancio.internal.util.ExceptionUtils.logException;

final class DefaultSetterMethodResolver {

    private final MethodNameResolver setterNameResolver;

    DefaultSetterMethodResolver(final Settings settings) {
        this.setterNameResolver = getMethodNameResolver(settings.get(Keys.SETTER_STYLE));
    }

    Method getSetter(final InternalNode node) {
        final String methodName = setterNameResolver.resolveFor(node.getField());
        final Field field = node.getField();

        if (methodName != null) {
            try {
                final Class<?> klass = field.getDeclaringClass();
                return klass.getDeclaredMethod(methodName, field.getType());
            } catch (NoSuchMethodException ex) {
                logException("Resolved setter method '{}' for field '{}' does not exist",
                        ex, methodName, Format.formatField(field));
            }
        }
        return null;
    }

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

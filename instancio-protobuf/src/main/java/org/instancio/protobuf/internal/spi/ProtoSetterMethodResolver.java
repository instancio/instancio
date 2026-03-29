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
package org.instancio.protobuf.internal.spi;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import org.instancio.Node;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.Verify;
import org.instancio.spi.InstancioServiceProvider;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static java.util.Objects.requireNonNull;

class ProtoSetterMethodResolver implements InstancioServiceProvider.SetterMethodResolver {

    @Nullable
    @Override
    public Method getSetter(final Node node) {
        final Field field = requireNonNull(node.getField(), "node must have a field");
        final Class<?> builderClass = findMessageBuilderClass(field.getDeclaringClass());

        if (builderClass == null) {
            return null;
        }

        final String fieldName = field.getName();
        Verify.isTrue(fieldName.endsWith("_"), "Expected proto field to end with '_'");

        final Class<?> targetClass = node.getTargetClass();
        final String capitalisedName = capitaliseAndRemoveTrailingUnderscore(fieldName);

        if (Map.class.isAssignableFrom(targetClass)) {
            final String methodName = "putAll" + capitalisedName;
            return ReflectionUtils.getSetterMethod(builderClass, methodName, Map.class);
        } else if (Iterable.class.isAssignableFrom(targetClass)
                   && !ByteString.class.isAssignableFrom(targetClass)) {
            // ByteString implements Iterable<Byte> but is a scalar proto type, not a repeated field
            final String methodName = "addAll" + capitalisedName;
            return ReflectionUtils.getSetterMethod(builderClass, methodName, Iterable.class);
        } else {
            final String methodName = "set" + capitalisedName;
            return ReflectionUtils.getSetterMethod(builderClass, methodName, targetClass);
        }
    }

    private static String capitaliseAndRemoveTrailingUnderscore(final String fieldName) {
        final char[] ch = fieldName.toCharArray();
        ch[0] = Character.toUpperCase(ch[0]);
        return new String(ch, 0, ch.length - 1);
    }

    @Nullable
    private static Class<?> findMessageBuilderClass(final Class<?> klass) {
        if (ProtoHelper.isProtoMessage(klass)) {
            for (Class<?> inner : klass.getDeclaredClasses()) {
                if (Message.Builder.class.isAssignableFrom(inner)) {
                    return inner;
                }
            }
        }
        return null;
    }
}

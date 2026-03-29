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
import com.google.protobuf.Descriptors;
import com.google.protobuf.StringValue;
import org.instancio.Node;
import org.instancio.internal.spi.InternalTypeResolver;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.StringUtils;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

final class ProtoTypeResolver implements InternalTypeResolver {

    /**
     * Disabled because proto remaps field types in ways that
     * are not subtype relationships, e.g. {@code int} to proto enum.
     */
    @Override
    public boolean isSubtypeValidationEnabled() {
        return false;
    }

    @Nullable
    @Override
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public Type getSubtype(final Node node) {
        final Field field = node.getField();
        if (field == null) {
            return null;
        }

        if (field.getDeclaringClass() == StringValue.class && "value_".equals(field.getName())) {
            return String.class;
        }
        if (!ProtoHelper.isProtoMessage(field.getDeclaringClass())) {
            return null;
        }
        final String name = field.getName();
        if (!name.endsWith("_")) {
            return null;
        }
        final Descriptors.Descriptor desc = ProtoHelper.getDescriptor(field.getDeclaringClass());
        final String protoName = StringUtils.camelCaseToSnakeCase(name.substring(0, name.length() - 1));
        final Descriptors.FieldDescriptor fieldDescriptor = desc.findFieldByName(protoName);
        if (fieldDescriptor == null) {
            return null;
        }

        if (fieldDescriptor.isMapField()) {
            // Use the generated getXxxMap() getter to get the exact `Map<K,V>` parameterised type.
            // (Looking up types via the inner entry's "value" descriptor doesn't work because
            // the map-entry value field is named "value", not the actual field name.)
            final String fieldCamel = StringUtils.snakeCaseToCamelCase(protoName);
            final String mapGetter = "get" + StringUtils.capitalise(fieldCamel) + "Map";
            final Method getter = ReflectionUtils.getRequiredZeroArgMethod(field.getDeclaringClass(), mapGetter);
            return getter.getGenericReturnType();
        }

        if (fieldDescriptor.isRepeated()) {
            // Use the generated getXxxList() getter to get the exact `List<E>` parameterised type.
            // Scalar repeated fields int32, int64, etc) use primitive-specialised
            // types like `IntList`, `LongList`, etc. that carry no generic type parameter at the
            // field declaration level, so we cannot infer the element type from the field itself.
            // Therefore, use the getter method to get the parameterised type.
            final String fieldCamel = StringUtils.snakeCaseToCamelCase(protoName);
            final String listGetter = "get" + StringUtils.capitalise(fieldCamel) + "List";
            final Method getter = ReflectionUtils.getRequiredZeroArgMethod(field.getDeclaringClass(), listGetter);
            return getter.getGenericReturnType();
        }

        final Descriptors.FieldDescriptor.JavaType javaType = fieldDescriptor.getJavaType();
        return switch (javaType) {
            case STRING -> String.class;
            case ENUM -> typeFromGetter(field.getDeclaringClass(), StringUtils.snakeCaseToCamelCase(protoName));
            case BYTE_STRING -> ByteString.class;
            default -> null;
        };
    }

    private static Class<?> typeFromGetter(final Class<?> klass, final String fieldCamel) {
        final String methodName = "get" + StringUtils.capitalise(fieldCamel);
        final Method getter = ReflectionUtils.getRequiredZeroArgMethod(klass, methodName);
        return getter.getReturnType();
    }
}

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
import org.instancio.Node;
import org.instancio.internal.spi.InternalExtension;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Substitutes {@code null} values with protobuf type-specific defaults
 * since proto fields do not allow null values.
 */
class ProtoNullSubstitutor implements InternalExtension.InternalNullSubstitutor {

    private static final Map<Class<?>, Object> PROTO_SCALAR_DEFAULTS = Map.of(
            String.class, "",
            Integer.class, 0,
            Long.class, 0L,
            Float.class, 0f,
            Double.class, 0d,
            Boolean.class, Boolean.FALSE,
            ByteString.class, ByteString.EMPTY);

    @Nullable
    @Override
    public Object substituteNull(final Node node) {
        final Node parent = node.getParent();

        if (parent == null) {
            return null; // root object can be null
        }

        final Class<?> target = node.getTargetClass();
        if (ProtoHelper.isProtoMessage(target)) {
            return ProtoHelper.getDefaultInstance(target);
        }

        // For map values and collection elements, node has no backing field.
        // Fall back to the parent's field to detect a proto context.
        final Field field = node.getField();
        final Field contextField = field != null ? field : parent.getField();

        return contextField != null && ProtoHelper.isProtoMessage(contextField.getDeclaringClass())
                ? PROTO_SCALAR_DEFAULTS.get(target)
                : null;
    }
}

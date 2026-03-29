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

import com.google.protobuf.BoolValue;
import com.google.protobuf.BytesValue;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.FloatValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.Message;
import com.google.protobuf.StringValue;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.UInt64Value;
import org.instancio.internal.util.ReflectionUtils;

import java.util.Set;

final class ProtoHelper {

    /**
     * Known protobuf wrapper types. These are treated as single-field POJOs
     * so that user selectors, e.g. {@code all(String.class)} can reach
     * the inner {@code value} field.
     */
    private static final Set<Class<?>> PROTO_WRAPPER_TYPES = Set.of(
            StringValue.class, BytesValue.class,
            BoolValue.class,
            Int32Value.class, Int64Value.class,
            UInt32Value.class, UInt64Value.class,
            FloatValue.class, DoubleValue.class);

    static boolean isProtoWrapperType(final Class<?> klass) {
        return PROTO_WRAPPER_TYPES.contains(klass);
    }

    static boolean isProtoMessage(final Class<?> klass) {
        return Message.class.isAssignableFrom(klass);
    }

    static Descriptors.Descriptor getDescriptor(final Class<?> messageClass) {
        return ReflectionUtils.invokeStaticMethod(messageClass, "getDescriptor");
    }

    static Message getDefaultInstance(final Class<?> messageClass) {
        return ReflectionUtils.invokeStaticMethod(messageClass, "getDefaultInstance");
    }

    private ProtoHelper() {
        // non-instantiable
    }
}

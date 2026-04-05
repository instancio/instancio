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

import com.google.protobuf.Message;
import org.instancio.internal.spi.InternalExtension;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.Verify;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * Resolves a getter reference on a proto {@link Message} class
 * (e.g. {@code Person::getName}) to the corresponding backing
 * field in that class (e.g. {@code name_}), enabling
 * {@code Select.field(Person::getName)} to work with protobuf messages.
 */
class ProtoGetterMethodFieldResolver implements InternalExtension.InternalGetterMethodFieldResolver {

    @Nullable
    @Override
    public Field resolveField(final Class<?> declaringClass, final String methodName) {

        if (!ProtoHelper.isProtoMessage(declaringClass)) {
            return null;
        }

        Verify.isTrue(methodName.startsWith("get"), "Expected getter method: %s", methodName);

        // methodName is e.g. "getName", "getPhoneNumbersList", "getAddressesMap"
        // strip leading "get" (offset 3), and trailing "List" or "Map" suffix if present
        int start = 3;
        int end = methodName.length();

        if (methodName.endsWith("List")) {
            end -= 4;
        } else if (methodName.endsWith("Map")) {
            end -= 3;
        }

        // build "fieldName_" directly: lower-case the first char, append the rest, append "_"
        final String fieldName = Character.toLowerCase(methodName.charAt(start)) +
                                 methodName.substring(start + 1, end) +
                                 '_';

        return ReflectionUtils.getFieldOrNull(declaringClass, fieldName);
    }
}

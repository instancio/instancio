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

import com.google.protobuf.Descriptors;
import org.instancio.Node;
import org.instancio.internal.spi.InternalServiceProvider;
import org.instancio.internal.util.StringUtils;

import java.lang.reflect.Field;

class ProtoNodeFilter implements InternalServiceProvider.InternalNodeFilter {

    private static final String PROTOBUF_PACKAGE_NAME = "com.google.protobuf";

    @Override
    public Decision resolve(final Node node) {
        final Field field = node.getField();

        if (field == null) {
            return Decision.KEEP;
        }

        final String fieldName = field.getName();

        // For well-known wrapper types: allow only the inner value_ field so that
        // user selectors (e.g. all(String.class)) can reach it; ignore everything else.
        if (ProtoHelper.isProtoWrapperType(field.getDeclaringClass())) {
            return "value_".equals(fieldName) ? Decision.KEEP : Decision.PRUNE;
        }

        if (field.getDeclaringClass().getPackageName().startsWith(PROTOBUF_PACKAGE_NAME)) {
            return Decision.PRUNE;
        }

        if (!ProtoHelper.isProtoMessage(field.getDeclaringClass())) {
            return Decision.KEEP;
        }

        // Proto data fields end with '_'; skip everything else
        if (!fieldName.endsWith("_")) {
            return Decision.PRUNE;
        }

        // Skip if no matching proto descriptor field
        final Descriptors.Descriptor desc = ProtoHelper.getDescriptor(field.getDeclaringClass());
        final String snakeCase = StringUtils.camelCaseToSnakeCase(fieldName.substring(0, fieldName.length() - 1));
        if (desc.findFieldByName(snakeCase) == null) {
            return Decision.PRUNE;
        }

        return Decision.KEEP;
    }
}

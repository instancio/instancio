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
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodError;
import org.instancio.settings.Settings;

public final class ProtoInternalExtension implements InternalExtension {

    private static final Settings PROTO_BUILDER_ASSIGNER_SETTINGS =
            Settings.create()
                    .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                    .set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.IGNORE)
                    .lock();

    private static final InternalAssignerSettingsProvider ASSIGNER_SETTINGS_PROVIDER =
            targetClass -> Message.Builder.class.isAssignableFrom(targetClass)
                    ? PROTO_BUILDER_ASSIGNER_SETTINGS
                    : null;

    private static final InternalNullSubstitutor NULL_SUBSTITUTOR =
            new ProtoNullSubstitutor();

    private static final InternalNodeFilter NODE_FILTER =
            new ProtoNodeFilter();

    private static final InternalGetterMethodFieldResolver GETTER_METHOD_FIELD_RESOLVER =
            new ProtoGetterMethodFieldResolver();

    @Override
    public InternalNullSubstitutor getNullSubstitutor() {
        return NULL_SUBSTITUTOR;
    }

    @Override
    public InternalAssignerSettingsProvider getAssignerSettingsProvider() {
        return ASSIGNER_SETTINGS_PROVIDER;
    }

    @Override
    public InternalNodeFilter getNodeFilter() {
        return NODE_FILTER;
    }

    @Override
    public InternalGetterMethodFieldResolver getGetterMethodFieldResolver() {
        return GETTER_METHOD_FIELD_RESOLVER;
    }
}

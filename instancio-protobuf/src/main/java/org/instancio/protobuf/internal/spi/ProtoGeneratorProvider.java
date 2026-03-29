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
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Duration;
import com.google.protobuf.Message;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.Timestamp;
import org.instancio.Node;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.internal.util.StringUtils;
import org.instancio.protobuf.internal.generator.ProtoByteStringGenerator;
import org.instancio.protobuf.internal.generator.ProtoDurationGenerator;
import org.instancio.protobuf.internal.generator.ProtoEnumGenerator;
import org.instancio.protobuf.internal.generator.ProtoMessageBuilderGenerator;
import org.instancio.protobuf.internal.generator.ProtoTimestampGenerator;
import org.instancio.protobuf.internal.generator.ProtoUnsignedIntGenerator;
import org.instancio.protobuf.internal.generator.ProtoUnsignedLongGenerator;
import org.instancio.settings.Settings;
import org.instancio.spi.InstancioServiceProvider;
import org.instancio.spi.ServiceProviderContext;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;

class ProtoGeneratorProvider implements InstancioServiceProvider.GeneratorProvider {

    private final GeneratorContext generatorContext;

    ProtoGeneratorProvider(final ServiceProviderContext spContext) {
        this.generatorContext = new GeneratorContext(spContext.getSettings(), spContext.random());
    }

    @Nullable
    @Override
    public GeneratorSpec<?> getGenerator(final Node node, final Generators generators) {
        final Field field = node.getField();

        if (field != null) {
            final GeneratorSpec<?> spec = getGeneratorSpecForField(field);
            if (spec != null) {
                return spec;
            }
        }
        return getGeneratorSpecForClass(node.getTargetClass());
    }

    @Nullable
    private Generator<?> getGeneratorSpecForClass(final Class<?> target) {
        if (target == Timestamp.class) {
            return new ProtoTimestampGenerator();
        }
        if (target == Duration.class) {
            return new ProtoDurationGenerator();
        }
        if (Message.class.isAssignableFrom(target)) {
            return new ProtoMessageBuilderGenerator(target);
        }
        if (ProtocolMessageEnum.class.isAssignableFrom(target)) {
            return new ProtoEnumGenerator(target);
        }
        if (ByteString.class.isAssignableFrom(target)) {
            return new ProtoByteStringGenerator(generatorContext);
        }
        return null;
    }

    @Nullable
    private GeneratorSpec<?> getGeneratorSpecForField(final Field field) {
        final Settings settings = generatorContext.getSettings();
        final FieldDescriptor.Type protoType = getProtoFieldType(field);

        if (protoType == FieldDescriptor.Type.UINT32
            || protoType == FieldDescriptor.Type.FIXED32) {

            return new ProtoUnsignedIntGenerator(settings);
        }

        if (protoType == FieldDescriptor.Type.UINT64
            || protoType == FieldDescriptor.Type.FIXED64) {

            return new ProtoUnsignedLongGenerator(settings);
        }

        return null;
    }

    /**
     * Returns the proto {@link FieldDescriptor.Type}
     * for the given Java backing field, or {@code null} if the field
     * is not a user proto message field.
     */
    private static FieldDescriptor.@Nullable Type getProtoFieldType(final Field field) {
        final Class<?> declaringClass = field.getDeclaringClass();
        if (!ProtoHelper.isProtoMessage(declaringClass)) {
            return null;
        }

        final String fieldName = field.getName();

        final String protoFieldName = StringUtils.camelCaseToSnakeCase(
                fieldName.substring(0, fieldName.length() - 1));

        final FieldDescriptor fd = ProtoHelper
                .getDescriptor(declaringClass)
                .findFieldByName(protoFieldName);

        return fd.getType();
    }
}

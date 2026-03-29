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
package org.instancio.protobuf.internal.generator;

import com.google.protobuf.Message;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.internal.generator.InternalContainerHint;
import org.instancio.internal.util.ReflectionUtils;

/**
 * Generator for proto {@link Message} types.
 *
 * <p>Calls {@code Message.newBuilder()} and returns the builder. The return type is
 * {@code Object} to avoid a cast error since {@code Builder} is not a subtype of
 * {@code Message}. The engine calls {@code builder.build()} via the {@code buildFunction}
 * hint after populating all child fields.
 */
public record ProtoMessageBuilderGenerator(Class<?> messageClass) implements Generator<Object> {

    private static final Hints HINTS = Hints.builder()
            .afterGenerate(AfterGenerate.POPULATE_ALL)
            .with(InternalContainerHint.builder()
                    .buildFunction(obj -> ((Message.Builder) obj).build())
                    .build())
            .build();

    @Override
    public Object generate(final Random random) {
        return ReflectionUtils.invokeStaticMethod(messageClass, "newBuilder");
    }

    @Override
    public Hints hints() {
        return HINTS;
    }
}

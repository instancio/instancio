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

import org.instancio.documentation.Initializer;
import org.instancio.spi.InstancioServiceProvider;
import org.instancio.spi.ServiceProviderContext;

public class ProtoServiceProvider implements InstancioServiceProvider {

    private static final SetterMethodResolver SETTER_METHOD_RESOLVER = new ProtoSetterMethodResolver();
    private static final TypeResolver TYPE_RESOLVER = new ProtoTypeResolver();

    private GeneratorProvider generatorProvider;

    @Initializer
    @Override
    public void init(final ServiceProviderContext context) {
        this.generatorProvider = new ProtoGeneratorProvider(context);
    }

    @Override
    public GeneratorProvider getGeneratorProvider() {
        return generatorProvider;
    }

    @Override
    public SetterMethodResolver getSetterMethodResolver() {
        return SETTER_METHOD_RESOLVER;
    }

    @Override
    public TypeResolver getTypeResolver() {
        return TYPE_RESOLVER;
    }
}

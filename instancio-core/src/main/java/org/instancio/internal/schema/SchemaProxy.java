/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.internal.schema;

import org.instancio.internal.schema.csv.CsvDataLoader;
import org.instancio.internal.schema.csv.CsvResourceHandler;
import org.instancio.schema.Schema;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public final class SchemaProxy {

    @SuppressWarnings("unchecked")
    public static <S extends Schema> S forClass(final SchemaContext<S> context) {
        final ResourceHandler resourceHandler = new CsvResourceHandler(new CsvDataLoader());
        final AbstractSchemaDataProvider generator = resourceHandler.createDataSpecGenerator(context);
        final InvocationHandler invocationHandler = new SchemaProxyInvocationHandler(generator);

        return (S) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{context.getSchemaClass(), InternalSchema.class},
                invocationHandler);
    }

    public static <S extends Schema> AbstractSchemaDataProvider getProvider(final S schema) {
        SchemaProxyInvocationHandler handler = (SchemaProxyInvocationHandler) Proxy.getInvocationHandler(schema);
        return handler.getProvider();
    }

    private SchemaProxy() {
        // non-instantiable
    }
}

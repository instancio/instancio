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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class SchemaProxyInvocationHandler implements InvocationHandler {
    private final AbstractSchemaDataProvider provider;

    public SchemaProxyInvocationHandler(final AbstractSchemaDataProvider provider) {
        this.provider = provider;
    }

    public AbstractSchemaDataProvider getProvider() {
        return provider;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final String invokedMethodName = method.getName();

        if ("equals".equals(invokedMethodName)) {
            return proxy == args[0]; //NOPMD
        }
        if ("hashCode".equals(invokedMethodName)) {
            return System.identityHashCode(proxy);
        }
        if ("toString".equals(invokedMethodName)) {
            return String.format("Proxy[%s]", proxy.getClass().getInterfaces()[0].getName());
        }
        if ("getDataProperties".equals(invokedMethodName)) {
            return provider.getDataProperties();
        }
        if ("resolveSpec".equals(invokedMethodName)) {
            return provider.resolveSpec((String) args[0]);
        }
        return provider.createSpec(new SpecMethod(method), args);
    }
}

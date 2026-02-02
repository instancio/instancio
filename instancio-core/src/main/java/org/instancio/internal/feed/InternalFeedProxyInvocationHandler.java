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
package org.instancio.internal.feed;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class InternalFeedProxyInvocationHandler implements InvocationHandler {
    private final InternalFeed internalFeed;

    public InternalFeedProxyInvocationHandler(final InternalFeed internalFeed) {
        this.internalFeed = internalFeed;
    }

    public InternalFeed getProvider() {
        return internalFeed;
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
        return internalFeed.createSpec(new SpecMethod(method), args);
    }
}

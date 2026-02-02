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

import org.instancio.feed.Feed;
import org.instancio.internal.feed.csv.CsvResourceHandler;
import org.instancio.internal.feed.json.JsonResourceHandler;
import org.instancio.settings.FeedFormatType;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public final class InternalFeedProxy {

    @SuppressWarnings("unchecked")
    public static <F extends Feed> F forClass(final InternalFeedContext<F> context) {
        final ResourceHandler resourceHandler = getResourceHandler(context);
        final InternalFeed feed = resourceHandler.createFeed(context);
        final InvocationHandler invocationHandler = new InternalFeedProxyInvocationHandler(feed);

        return (F) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{context.getFeedClass(), InternalFeed.class},
                invocationHandler);
    }

    private static ResourceHandler getResourceHandler(final InternalFeedContext<?> context) {
        if (context.getDataFormatType() == FeedFormatType.CSV) {
            return new CsvResourceHandler();
        }
        return new JsonResourceHandler();
    }

    public static InternalFeed asInternalFeed(final Feed feed) {
        InternalFeedProxyInvocationHandler handler = (InternalFeedProxyInvocationHandler)
                Proxy.getInvocationHandler(feed);

        return handler.getProvider();
    }

    private InternalFeedProxy() {
        // non-instantiable
    }
}

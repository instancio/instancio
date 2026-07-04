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
package org.instancio.support;

import org.instancio.documentation.InternalApi;
import org.jspecify.annotations.Nullable;

@InternalApi
public final class ThreadLocalTestContext {

    private static final ThreadLocalTestContext INSTANCE = new ThreadLocalTestContext();

    private static final ThreadLocal<@Nullable InternalTestContext> CONTEXT = new InheritableThreadLocal<>();

    private ThreadLocalTestContext() {
        // non-instantiable
    }

    @Nullable
    public InternalTestContext get() {
        return CONTEXT.get();
    }

    public void remove() {
        CONTEXT.remove();
    }

    public void set(final InternalTestContext internalTestContext) {
        CONTEXT.set(internalTestContext);
    }

    public static ThreadLocalTestContext getInstance() {
        return INSTANCE;
    }
}

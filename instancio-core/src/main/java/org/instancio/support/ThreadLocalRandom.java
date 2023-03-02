/*
 * Copyright 2022-2023 the original author or authors.
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

import org.instancio.Random;
import org.instancio.documentation.InternalApi;

@InternalApi
public final class ThreadLocalRandom {

    private static final ThreadLocalRandom INSTANCE = new ThreadLocalRandom();

    private static final ThreadLocal<Random> RANDOM = new ThreadLocal<>();

    private ThreadLocalRandom() {
        // non-instantiable
    }

    public Random get() {
        return RANDOM.get();
    }

    public void remove() {
        RANDOM.remove();
    }

    public void set(final Random provider) {
        RANDOM.set(provider);
    }

    public static ThreadLocalRandom getInstance() {
        return INSTANCE;
    }
}

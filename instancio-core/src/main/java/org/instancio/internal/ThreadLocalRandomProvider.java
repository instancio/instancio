/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.internal;

import org.instancio.internal.random.RandomProvider;

@SuppressWarnings("java:S5164")
public class ThreadLocalRandomProvider {

    private static final ThreadLocalRandomProvider INSTANCE = new ThreadLocalRandomProvider();

    private static final ThreadLocal<RandomProvider> randomProvider = new ThreadLocal<>();

    private ThreadLocalRandomProvider() {
        // non-instantiable
    }

    public RandomProvider get() {
        return randomProvider.get();
    }

    public void remove() {
        randomProvider.remove();
    }

    public void set(final RandomProvider provider) {
        randomProvider.set(provider);
    }

    public static ThreadLocalRandomProvider getInstance() {
        return INSTANCE;
    }
}

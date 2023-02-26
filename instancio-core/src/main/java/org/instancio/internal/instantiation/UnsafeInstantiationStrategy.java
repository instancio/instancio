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
package org.instancio.internal.instantiation;

import org.instancio.internal.util.ReflectionUtils;
import org.jetbrains.annotations.VisibleForTesting;

/**
 * Uses {@code sun.misc.Unsafe} to instantiate objects.
 */
public class UnsafeInstantiationStrategy implements InstantiationStrategy {

    private final boolean isUnsafeAvailable;

    public UnsafeInstantiationStrategy() {
        isUnsafeAvailable = ReflectionUtils.loadClass("sun.misc.Unsafe") != null;
    }

    @Override
    public <T> T createInstance(final Class<T> klass) {
        return isUnsafeAvailable()
                ? UnsafeHelper.getInstance().allocateInstance(klass)
                : null;
    }

    @VisibleForTesting
    boolean isUnsafeAvailable() {
        return isUnsafeAvailable;
    }
}

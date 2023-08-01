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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses {@code sun.misc.Unsafe} to instantiate objects.
 */
final class UnsafeInstantiationStrategy implements InstantiationStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(UnsafeInstantiationStrategy.class);

    private final boolean isUnsafeAvailable;

    private UnsafeInstantiationStrategy() {
        isUnsafeAvailable = ReflectionUtils.loadClass("sun.misc.Unsafe") != null;

        if (!isUnsafeAvailable) {
            // Verbose message, but should be logged only once since this class is a singleton
            // Note: this issue can also occur within an OSGi container
            LOG.debug(String.format(
                    "sun.misc.Unsafe is unavailable. This may result in nulls being generated%n" +
                            "for POJO classes that do not provide a default (no-argument) constructor.%n" +
                            "If using JPMS, consider adding 'requires jdk.unsupported' to module-info.java"));
        }
    }

    static InstantiationStrategy getInstance() {
        return Holder.INSTANCE;
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

    private static final class Holder {
        private static final InstantiationStrategy INSTANCE = new UnsafeInstantiationStrategy();
    }
}

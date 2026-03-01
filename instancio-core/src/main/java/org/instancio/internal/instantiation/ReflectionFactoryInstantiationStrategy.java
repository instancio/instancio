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
package org.instancio.internal.instantiation;

import org.instancio.documentation.VisibleForTesting;
import org.instancio.internal.util.ReflectionUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses {@code sun.reflect.ReflectionFactory} to instantiate objects.
 */
final class ReflectionFactoryInstantiationStrategy implements InstantiationStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(ReflectionFactoryInstantiationStrategy.class);

    private final boolean isReflectionFactoryAvailable;

    private ReflectionFactoryInstantiationStrategy() {
        isReflectionFactoryAvailable = ReflectionUtils.loadClass(
                "sun.reflect.ReflectionFactory") != null;

        if (!isReflectionFactoryAvailable) {
            LOG.debug("sun.reflect.ReflectionFactory is unavailable");
        }
    }

    static InstantiationStrategy getInstance() {
        return Holder.INSTANCE;
    }

    @Nullable
    @Override
    public <T> T createInstance(final Class<T> klass) {
        return isReflectionFactoryAvailable()
                ? ReflectionFactoryHelper.createInstance(klass)
                : null;
    }

    @VisibleForTesting
    boolean isReflectionFactoryAvailable() {
        return isReflectionFactoryAvailable;
    }

    private static final class Holder {
        private static final InstantiationStrategy INSTANCE =
                new ReflectionFactoryInstantiationStrategy();
    }
}

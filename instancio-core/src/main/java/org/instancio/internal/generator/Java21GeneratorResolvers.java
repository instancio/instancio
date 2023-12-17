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
package org.instancio.internal.generator;

import java.util.Map;

/**
 * No-op for Java versions below 21 (see {@code src/main/java21}).
 */
@SuppressWarnings("all")
public final class Java21GeneratorResolvers {

    private Java21GeneratorResolvers() {
        // non-instantiable
    }

    public static void putSubtypes(final Map<Class<?>, Class<?>> map) {
        // no-op
    }

    public static void putGenerators(final Map<Class<?>, Class<?>> map) {
        // no-op
    }
}

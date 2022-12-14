/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.internal.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class ObjectUtils {
    private ObjectUtils() {
        //non-instantiable
    }

    @NotNull
    public static <T> T defaultIfNull(@Nullable final T value, final T defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static <T> T defaultIfNull(@Nullable final T value, final Supplier<T> defaultValue) {
        return value == null ? defaultValue.get() : value;
    }
}

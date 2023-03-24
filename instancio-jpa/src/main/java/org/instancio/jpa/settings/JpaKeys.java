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
package org.instancio.jpa.settings;

import jakarta.persistence.metamodel.Metamodel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.instancio.documentation.ExperimentalApi;
import org.instancio.internal.settings.InternalKey;
import org.instancio.settings.SettingKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JpaKeys {

    private static final List<SettingKey<Object>> ALL_KEYS = new ArrayList<>();

    @ExperimentalApi
    public static final SettingKey<Metamodel> METAMODEL = register(
        "jpaMetamodel", Metamodel.class, null, true);

    public static List<SettingKey<Object>> all() {
        return Collections.unmodifiableList(ALL_KEYS);
    }

    private static <T> SettingKey<T> register(
        @NotNull final String propertyKey,
        @NotNull final Class<T> type,
        @Nullable final Object defaultValue,
        final boolean allowsNullValue) {

        final SettingKey<T> settingKey = new InternalKey<>(
            propertyKey, type, defaultValue, null, allowsNullValue);

        ALL_KEYS.add((SettingKey<Object>) settingKey);
        return settingKey;
    }

    private JpaKeys() {
        // non-instantiable
    }
}

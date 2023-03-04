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
package org.instancio.internal.settings;

import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class InternalKey<T> implements SettingKey<T>, AutoAdjustable, Comparable<SettingKey<T>> {

    private final String propertyKey;
    private final Class<?> type;
    private final Object defaultValue;
    private final RangeAdjuster rangeAdjuster;
    private final boolean allowsNullValue;

    public InternalKey(final String propertyKey,
                       final Class<?> type,
                       @Nullable final Object defaultValue,
                       @Nullable final RangeAdjuster rangeAdjuster,
                       boolean allowsNullValue) {

        this.propertyKey = propertyKey;
        this.type = type;
        this.defaultValue = defaultValue;
        this.rangeAdjuster = rangeAdjuster;
        this.allowsNullValue = allowsNullValue;
    }

    @Override
    public String propertyKey() {
        return propertyKey;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> type() {
        return (Class<T>) type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T defaultValue() {
        return (T) defaultValue;
    }

    @Override
    public boolean allowsNullValue() {
        return allowsNullValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <N extends Number & Comparable<N>> void autoAdjust(
            @NotNull final Settings settings,
            @NotNull final N otherValue) {

        if (rangeAdjuster != null) {
            final SettingKey<N> key = (SettingKey<N>) this;
            rangeAdjuster.adjustRange(settings, key, otherValue);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof InternalKey)) return false;
        final InternalKey<?> key = (InternalKey<?>) o;
        return Objects.equals(propertyKey, key.propertyKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyKey);
    }

    @Override
    public int compareTo(final SettingKey<T> o) {
        return propertyKey.compareTo(o.propertyKey());
    }

    @Override
    public String toString() {
        return propertyKey;
    }
}

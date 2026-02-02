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
package org.instancio.internal.settings;

import org.instancio.internal.ApiValidator;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public final class InternalKey<T>
        implements SettingKey<T>, AutoAdjustable, Comparable<InternalKey<T>> {

    private final String propertyKey;
    private final Class<?> type;
    private final Object defaultValue;
    private final RangeAdjuster rangeAdjuster;
    private final boolean allowsNullValue;
    private final boolean allowsNegative;

    /**
     * Constructor.
     *
     * @param propertyKey     used for {@code instancio.properties}
     * @param type            the type of the key
     * @param defaultValue    optional default devalue
     * @param rangeAdjuster   an adjuster for range settings, such as min/max
     * @param allowsNullValue whether this setting can be {@code null}
     * @param allowsNegative  whether range can be negative;
     *                        only needed if {@code rangeAdjuster} is specified
     */
    public InternalKey(final String propertyKey,
                       final Class<?> type,
                       @Nullable final Object defaultValue,
                       @Nullable final RangeAdjuster rangeAdjuster,
                       final boolean allowsNullValue,
                       final boolean allowsNegative) {

        this.propertyKey = propertyKey;
        this.type = type;
        this.defaultValue = defaultValue;
        this.rangeAdjuster = rangeAdjuster;
        this.allowsNullValue = allowsNullValue;
        this.allowsNegative = allowsNegative;
    }

    @Override
    public String propertyKey() {
        return propertyKey;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<T> type() {
        return (Class<T>) type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T defaultValue() {
        return (T) defaultValue;
    }

    @Override
    public boolean allowsNullValue() {
        return allowsNullValue;
    }

    public boolean allowsNegative() {
        return allowsNegative;
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
    @SuppressWarnings("PMD.SimplifyBooleanReturns")
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof SettingKey<?> key)) return false;
        return Objects.equals(propertyKey, key.propertyKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyKey);
    }

    @Override
    public int compareTo(final InternalKey<T> o) {
        return propertyKey.compareTo(o.propertyKey());
    }

    @Override
    public String toString() {
        return String.format("SettingKey[propertyKey=%s, type=%s]",
                propertyKey, type.getSimpleName());
    }

    public static <T> SettingKeyBuilder<T> builder(final Class<T> type) {
        return new InternalKeyBuilder<>(type);
    }

    public static final class InternalKeyBuilder<T> implements SettingKeyBuilder<T> {
        private final Class<T> type;
        private String propertyKey;

        private InternalKeyBuilder(final Class<T> type) {
            this.type = ApiValidator.notNull(type, "type must not be null");
        }

        @Override
        public SettingKeyBuilder<T> ofType(final Class<T> type) {
            return new InternalKeyBuilder<>(type);
        }

        @Override
        public SettingKeyBuilder<T> withPropertyKey(final String propertyKey) {
            this.propertyKey = ApiValidator.notNull(propertyKey, "property key must not be null");
            return this;
        }

        @Override
        public SettingKey<T> create() {
            final String key;
            if (propertyKey == null) {
                key = "custom.key." + UUID.randomUUID().toString()
                        .replace("-", "")
                        .substring(0, 20);
            } else {
                key = propertyKey;
            }
            return new InternalKey<>(key, type, null, null, true, false);
        }
    }
}

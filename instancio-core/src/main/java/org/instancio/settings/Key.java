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
package org.instancio.settings;

import org.instancio.util.Verify;

import javax.annotation.Nullable;
import java.util.Objects;

public final class Key implements SettingKey {

    private final String propertyKey;
    private final Class<?> type;
    private final Object defaultValue;
    private final RangeAdjuster rangeAdjuster;
    private final boolean allowsNullValue;

    Key(final String propertyKey,
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

    Key(final String propertyKey, final Class<?> type, @Nullable final Object defaultValue, @Nullable final RangeAdjuster rangeAdjuster) {
        this(propertyKey, type, Verify.notNull(defaultValue, "Null default value"), rangeAdjuster, false);
    }

    @Override
    public String propertyKey() {
        return propertyKey;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Class<T> type() {
        return (Class<T>) type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T defaultValue() {
        return (T) defaultValue;
    }

    @Override
    public boolean allowsNullValue() {
        return allowsNullValue;
    }

    @Override
    public <T extends Number & Comparable<T>> void autoAdjust(final Settings settings, final T otherValue) {
        if (rangeAdjuster != null) {
            rangeAdjuster.adjustRange(settings, this, otherValue);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Key)) return false;
        final Key key = (Key) o;
        return Objects.equals(propertyKey, key.propertyKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyKey);
    }

    @Override
    public int compareTo(final SettingKey o) {
        return propertyKey.compareTo(o.propertyKey());
    }

    @Override
    public String toString() {
        return propertyKey;
    }
}

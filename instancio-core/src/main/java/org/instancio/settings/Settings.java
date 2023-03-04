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
package org.instancio.settings;

import org.instancio.internal.settings.InternalSettings;
import org.instancio.internal.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * This class provides an API for updating settings programmatically.
 * An instance of this class can be created using one of the static methods
 * below. Instances of this class can be shared when creating different objects.
 *
 * <ul>
 *   <li>{@link #create()} - returns a new instance of blank settings</li>
 *   <li>{@link #defaults()} - returns a new instance containing default settings</li>
 * </ul>
 * <p>
 * Out of the box, Instancio uses default settings as returned by {@link #defaults()}.
 * Defaults can be overridden either globally using a configuration file, or per-object
 * using the API, for example:
 *
 * <pre>{@code
 *     // Create a blank instance of settings and set the overrides
 *     Settings settings = Settings.create()
 *         .set(Keys.COLLECTION_MIN_SIZE, 50)
 *         .set(Keys.COLLECTION_MAX_SIZE, 100);
 *
 *     // Pass the overrides when creating an object
 *     Person person = Instancio.of(Person.class)
 *         .withSettings(settings)
 *         .create();
 * }</pre>
 * <p>
 * For information on how to override settings globally using a configuration file, please refer
 * to the <a href="https://www.instancio.org/user-guide/">user guide</a>.
 *
 * @see Keys
 * @see SettingKey
 * @since 1.0.1
 */
public interface Settings {

    /**
     * Creates a new instance of empty settings.
     *
     * @return a new instance of empty settings
     */
    static Settings create() {
        return InternalSettings.create();
    }

    /**
     * Creates a new instance containing default settings.
     *
     * @return a new instance of settings containing the defaults
     */
    static Settings defaults() {
        return InternalSettings.defaults();
    }

    /**
     * Create settings from the given map.
     *
     * @param map to create settings from
     * @return a new instance of settings created from the given map
     */
    static Settings from(@NotNull Map<Object, Object> map) {
        return InternalSettings.from(map);
    }

    /**
     * Create settings from the given settings.
     *
     * @param other settings to create settings from
     * @return a new instance of settings
     */
    static Settings from(@NotNull Settings other) {
        return InternalSettings.from(other);
    }

    /**
     * Creates a new instance of settings by merging given settings with these settings.
     *
     * @param other settings to merge
     * @return new instance of merged settings
     */
    Settings merge(@NotNull Settings other);

    /**
     * Get setting value for given key.
     *
     * @param key setting key
     * @param <T> setting value type
     * @return value for given key, or {@code null} if none.
     */
    <T> T get(@NotNull SettingKey<T> key);

    /**
     * Set the setting with the given key to the specified value.
     * <p>
     * Note: when updating range settings (such as {@link Keys#COLLECTION_MIN_SIZE}
     * and {@link Keys#COLLECTION_MAX_SIZE}), range bounds are auto-adjusted by
     * {@link Constants#RANGE_ADJUSTMENT_PERCENTAGE} if the new minimum
     * is higher than the current maximum, and vice versa.
     *
     * @param key   to set
     * @param value to set
     * @return this instance of settings
     */
    <T> Settings set(@NotNull SettingKey<T> key, @Nullable T value);

    /**
     * Maps the supertype {@code from} supertype to 'to' subtype.
     *
     * <p>Example:
     *
     * <pre>{@code
     *   Settings settings = Settings.create()
     *       .mapType(Animal.class, Dog.class);
     *
     *   Animal animal = Instancio.of(Animal.class)
     *       .withSettings(settings)
     *       .create();
     *
     *   assertThat(animal).isExactlyInstanceOf(Dog.class);
     * }</pre>
     *
     * @param type    the type to map to a subtype
     * @param subtype the subtype class
     * @return this instance of settings
     */
    Settings mapType(@NotNull Class<?> type, @NotNull Class<?> subtype);

    /**
     * Returns a read-only view of the subtype map.
     *
     * @return subtype map
     */
    Map<Class<?>, Class<?>> getSubtypeMap();

    /**
     * Locks these settings for further modifications,
     * making this instance immutable.
     *
     * @return this instance of settings that can no longer be modified
     */
    Settings lock();

}

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

import org.instancio.documentation.InternalApi;
import org.instancio.settings.Settings;
import org.jspecify.annotations.NonNull;

/**
 * Represents a setting that can be auto-adjusted based on the value
 * of another related setting. An example would be auto-adjusting
 * the {@code max} value of a range, when the new {@code min} value
 * is greater than the current {@code max} value.
 *
 * @since 2.11.0
 */
@InternalApi
public interface AutoAdjustable {

    /**
     * Auto-adjusts the {@link Settings} value for this key
     * based on the value of another setting key.
     *
     * @param settings   to adjust
     * @param otherValue value of the other setting to base the adjustment off
     * @param <N>        a comparable number
     * @since 2.11.0
     */
    <N extends Number & Comparable<N>> void autoAdjust(
            @NonNull Settings settings,
            @NonNull N otherValue);
}

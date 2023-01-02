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
package org.instancio.generator.specs;

import org.instancio.generator.AsStringGeneratorSpec;

import java.time.MonthDay;

/**
 * Generator spec for {@link MonthDay} types.
 *
 * @since 2.3.0
 */
public interface MonthDayGeneratorSpec extends AsStringGeneratorSpec<MonthDay> {

    /**
     * Generate a {@code MonthDay} value on or after the specified day.
     *
     * @param min lower bound
     * @return spec builder
     * @since 2.3.0
     */
    MonthDayGeneratorSpec min(MonthDay min);

    /**
     * Generate a {@code MonthDay} value on or before the specified day.
     *
     * @param max upper bound
     * @return spec builder
     * @since 2.3.0
     */
    MonthDayGeneratorSpec max(MonthDay max);

    /**
     * Generate a {@code MonthDay} value between the given range.
     *
     * @param min lower bound (inclusive)
     * @param max upper bound (inclusive)
     * @return spec builder
     * @since 2.3.0
     */
    MonthDayGeneratorSpec range(MonthDay min, MonthDay max);
}

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
package org.instancio.generator.specs;

import org.instancio.generator.GeneratorSpec;

import java.time.Period;

/**
 * Generator spec for {@link Period}.
 *
 * @since 1.5.4
 */
public interface PeriodGeneratorSpec extends GeneratorSpec<Period> {

    /**
     * Generate number of {@link Period} days in the given range.
     *
     * @param min minimum number of days (inclusive)
     * @param max maximum number of days (inclusive)
     * @return spec builder
     * @since 1.5.4
     */
    PeriodGeneratorSpec days(int min, int max);

    /**
     * Generate number of {@link Period} months in the given range.
     *
     * @param min minimum number of months (inclusive)
     * @param max maximum number of months (inclusive)
     * @return spec builder
     * @since 1.5.4
     */
    PeriodGeneratorSpec months(int min, int max);

    /**
     * Generate number of {@link Period} years in the given range.
     *
     * @param min minimum number of years (inclusive)
     * @param max maximum number of years (inclusive)
     * @return spec builder
     * @since 1.5.4
     */
    PeriodGeneratorSpec years(int min, int max);
}

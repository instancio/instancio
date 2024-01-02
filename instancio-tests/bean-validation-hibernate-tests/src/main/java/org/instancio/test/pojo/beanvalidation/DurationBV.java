/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.test.pojo.beanvalidation;

import lombok.Data;
import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;

import java.time.Duration;

@Data
public class DurationBV {

    @DurationMin(seconds = 1)
    private Duration min;

    @DurationMin(days = 100000)
    private Duration largeMin;

    @DurationMax(seconds = 100)
    private Duration max;

    @DurationMax(seconds = -100)
    private Duration maxNegative;

    @DurationMin(nanos = 1)
    @DurationMax(nanos = 1)
    private Duration minMax;

    @DurationMin(nanos = 1)
    @DurationMax(nanos = 1)
    private Duration minMaxSame;

    @DurationMin(nanos = 1)
    @DurationMax(nanos = 3)
    private Duration minMaxSmall;

    @DurationMin(days = -1)
    @DurationMax(hours = -1)
    private Duration minMaxNegative;

    @DurationMin(minutes = 60, seconds = 90)
    @DurationMax(days = 5, hours = 12)
    private Duration minMaxDifferentUnits;
}

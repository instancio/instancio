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
package org.instancio.internal.generator.time;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;

import java.time.Year;

public class YearGenerator extends JavaTimeTemporalGenerator<Year> {

    public YearGenerator(final GeneratorContext context) {
        super(context,
                Year.of(1970),
                Year.now().plusYears(50));
    }

    @Override
    public String apiMethod() {
        return "year()";
    }

    @Override
    Year getLatestPast() {
        return Year.now().minusYears(1);
    }

    @Override
    Year getEarliestFuture() {
        return Year.now().plusYears(1);
    }

    @Override
    void validateRange() {
        ApiValidator.isTrue(min.compareTo(max) <= 0, "Start must not exceed end: %s, %s", min, max);
    }

    @Override
    public Year generate(final Random random) {
        return Year.of(random.intRange(min.getValue(), max.getValue()));
    }
}

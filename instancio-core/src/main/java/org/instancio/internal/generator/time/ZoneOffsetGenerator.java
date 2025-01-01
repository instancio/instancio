/*
 * Copyright 2022-2025 the original author or authors.
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
import org.instancio.internal.generator.AbstractGenerator;

import java.time.ZoneOffset;

public class ZoneOffsetGenerator extends AbstractGenerator<ZoneOffset> {

    private static final int MIN_HOURS = -18;
    private static final int MAX_HOURS = 18;

    public ZoneOffsetGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return null;
    }

    @Override
    protected ZoneOffset tryGenerateNonNull(final Random random) {
        final int hours = random.intRange(MIN_HOURS, MAX_HOURS);

        if (Math.abs(hours) == MAX_HOURS) {
            return ZoneOffset.ofHoursMinutes(hours, 0);
        }

        final int minutes = hours < 0
                ? random.intRange(-59, 0)
                : random.intRange(0, 59);

        return ZoneOffset.ofHoursMinutes(hours, minutes);
    }
}

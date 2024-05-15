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
package org.instancio.internal.util;

import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Hints;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class Constants {

    public static final String NL = System.lineSeparator();

    /**
     * Percentage by which to adjust a min/max range if min is set higher than max, or vice versa.
     */
    public static final int RANGE_ADJUSTMENT_PERCENTAGE = 50;

    /**
     * The number of times generating an object for a given node
     * can be retried until it is aborted.
     */
    public static final int MAX_RETRIES = 1000;

    /**
     * Default min array/collection size.
     */
    public static final int MIN_SIZE = 2;

    /**
     * Default max array/collection size.
     */
    public static final int MAX_SIZE = 6;

    /**
     * Default min for numeric types.
     */
    public static final int NUMERIC_MIN = 1;

    /**
     * Default max for numeric types.
     */
    public static final int NUMERIC_MAX = 10_000;

    /**
     * Maximum code point for generating Unicode strings (planes 0-3, inclusive).
     *
     * <p>See: <a href="https://en.wikipedia.org/wiki/Plane_(Unicode)">Plane (Unicode)</a>
     */
    public static final int MAX_CODE_POINT = 0x3FFFF;

    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;

    public static final LocalDateTime DEFAULT_MIN = LocalDateTime.of(1970, 1, 1, 0, 0);
    public static final LocalDateTime DEFAULT_MAX = LocalDateTime.of(2090, 1, 1, 0, 0).minusNanos(1);

    public static final long DURATION_MIN_NANOS = 1;
    public static final long DURATION_MAX_NANOS = Duration.ofDays(1000).toNanos();

    public static final Hints DO_NOT_MODIFY_HINT = Hints.afterGenerate(AfterGenerate.DO_NOT_MODIFY);

    private Constants() {
        // non-instantiable
    }
}

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
package org.instancio.support;

import org.instancio.documentation.InternalApi;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Internal class for generating random initial seed values.
 * <p>
 * This class is not part of the public API.
 */
@InternalApi
public final class Seeds {

    /**
     * Maximum bit length of the seed.
     *
     * <p>Since {@link Long#MAX_VALUE} is {@code 2^63 - 1},
     * the bit length is set to 62 to avoid overflow,
     * which may cause negative seeds to be generated.</p>
     */
    private static final int NUM_BITS_62 = 62;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private Seeds() {
        // non-instantiable
    }

    public static long randomSeed() {
        // For user convenience, generate only positive seeds.
        return new BigInteger(NUM_BITS_62, SECURE_RANDOM).longValue();
    }
}

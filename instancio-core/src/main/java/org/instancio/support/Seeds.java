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
package org.instancio.support;

import org.instancio.Random;
import org.instancio.documentation.InternalApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Internal class for generating random initial seed values.
 * <p>
 * This class is not part of the public API.
 */
@InternalApi
public final class Seeds {

    private static final Logger LOG = LoggerFactory.getLogger(Seeds.class);

    public enum Source {
        MANUAL(".withSeed()"),
        WITH_SETTINGS_BUILDER(".withSettings()"),
        WITH_SETTINGS_ANNOTATION("@WithSettings"),
        SEED_ANNOTATION("@Seed"),
        GLOBAL("instancio.properties"),
        RANDOM("random seed");

        private final String description;

        Source(final String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

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

    public static void logSeed(final Random random, final Type rootType) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Generating {} with seed {} (seed source: {})",
                    rootType.getTypeName(), random.getSeed(), ((DefaultRandom) random).getSource());
        }
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.internal.random;

import org.instancio.util.Verify;

import java.util.Random;

/**
 * Copied from the <a href="https://commons.apache.org/proper/commons-math">Apache Commons Math</a> library.
 * <p>
 * This is a modified version of {@code org.apache.commons.math3.random.RandomDataGenerator},
 * with most of the code from the original class omitted.
 */
@SuppressWarnings("PMD")
public class RandomDataGenerator {

    public static long nextLong(final Random random, final long lower, final long upper) {
        Verify.isTrue(lower <= upper, "Lower must be less than upper: %s, %s", lower, upper);

        final long max = (upper - lower) + 1;

        if (max <= 0) {
            // the range is too wide to fit in a positive long (larger than 2^63); as it covers
            // more than half the long range, we use directly a simple rejection method
            while (true) {
                final long r = random.nextLong();
                if (r >= lower && r <= upper) {
                    return r;
                }
            }
        } else if (max < Integer.MAX_VALUE) {
            // we can shift the range and generate directly a positive int
            return lower + random.nextInt((int) max);
        } else {
            // we can shift the range and generate directly a positive long
            return lower + nextLong(random, max);
        }
    }

    private static long nextLong(final Random random, final long n) throws IllegalArgumentException {
        if (n > 0) {
            final byte[] byteArray = new byte[8];
            long bits;
            long val;
            do {
                random.nextBytes(byteArray);
                bits = 0;
                for (final byte b : byteArray) {
                    bits = (bits << 8) | ((b) & 0xffL);
                }
                bits &= 0x7fffffffffffffffL;
                val = bits % n;
            } while (bits - val + (n - 1) < 0);
            return val;
        }
        throw new IllegalStateException("Not Strictly positive: " + n);
    }

    static double nextDouble(final Random random, double lower, double upper) {
        Verify.isTrue(lower <= upper, "Lower must be less than or equal to upper: %s, %s", lower, upper);
        Verify.isFalse(Double.isInfinite(lower), "Lower bound must not be infinite");
        Verify.isFalse(Double.isInfinite(upper), "Upper bound must not be infinite");

        final double u = random.nextDouble();
        return u * upper + (1.0 - u) * lower;
    }

    private RandomDataGenerator() {
        // non-instantiable
    }
}

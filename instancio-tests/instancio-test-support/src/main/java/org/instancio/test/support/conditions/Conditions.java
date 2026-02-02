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
package org.instancio.test.support.conditions;

import org.assertj.core.api.Condition;
import org.instancio.test.support.util.Constants;

public final class Conditions {

    public static final Condition<Integer> ODD_NUMBER = new Condition<>(i -> i % 2 != 0, "odd number");
    public static final Condition<Integer> EVEN_NUMBER = new Condition<>(i -> i % 2 == 0, "even number");

    /**
     * By default, random strings are generated in uppercase and have length between
     * {@code Keys.STRING_MIN_LENGTH} and {@code Keys.STRING_MIN_LENGTH}, inclusive.
     */
    public static final Condition<String> RANDOM_STRING = new Condition<>(
            s -> s != null && s.matches("[A-Z]{3,10}"), "random string");

    /**
     * By default, random integers are positive.
     */
    public static final Condition<Integer> RANDOM_INTEGER = new Condition<>(
            i -> i != null && i >= Constants.NUMERIC_MIN && i <= Constants.NUMERIC_MAX, "random integer");

    private Conditions() {
        // non-instantiable
    }
}

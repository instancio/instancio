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
package org.example.generator;

import org.instancio.Random;
import org.instancio.generator.Generator;

public class CustomIntegerGenerator implements Generator<Integer> {

    public static final int MIN = 0;
    public static final int MAX = 10;

    private boolean evenNumbers;

    public CustomIntegerGenerator evenNumbers() {
        this.evenNumbers = true;
        return this;
    }

    @Override
    public Integer generate(final Random random) {
        int result = nextInt(random);

        if (evenNumbers) {
            while (result % 2 != 0) {
                result = nextInt(random);
            }
        }

        return result;
    }

    private static int nextInt(final Random random) {
        return random.intRange(0, 10);
    }
}

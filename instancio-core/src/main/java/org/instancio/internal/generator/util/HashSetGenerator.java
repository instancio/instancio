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
package org.instancio.internal.generator.util;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;

import java.util.Collection;
import java.util.HashSet;

public class HashSetGenerator<T> extends CollectionGenerator<T> {

    public HashSetGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public Collection<T> generate(final Random random) {
        return random.diceRoll(nullable) ? null : new HashSet<>();
    }
}

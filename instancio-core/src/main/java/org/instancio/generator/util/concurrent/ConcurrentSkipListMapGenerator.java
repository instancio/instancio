/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.generator.util.concurrent;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.util.MapGenerator;
import org.instancio.internal.random.RandomProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConcurrentSkipListMapGenerator<K, V> extends MapGenerator<K, V> {

    public ConcurrentSkipListMapGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    @SuppressWarnings("SortedCollectionWithNonComparableKeys")
    public Map<K, V> generate(final RandomProvider random) {
        return random.diceRoll(nullable) ? null : new ConcurrentSkipListMap<>();
    }
}

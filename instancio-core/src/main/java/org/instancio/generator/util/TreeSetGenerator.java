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
package org.instancio.generator.util;

import org.instancio.generator.GeneratorContext;

import java.util.Collection;
import java.util.TreeSet;

public class TreeSetGenerator<T> extends CollectionGenerator<T> {

    public TreeSetGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    @SuppressWarnings("SortedCollectionWithNonComparableKeys")
    public Collection<T> generate() {
        return random().diceRoll(nullable) ? null : new TreeSet<>();
    }
}

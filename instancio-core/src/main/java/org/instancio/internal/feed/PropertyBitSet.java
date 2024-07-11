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
package org.instancio.internal.feed;

import org.instancio.internal.util.Sonar;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class PropertyBitSet {

    private final Map<String, Integer> propertyIndexMap = new HashMap<>();
    private final BitSet bitSet = new BitSet(128);
    private int last;

    @SuppressWarnings(Sonar.MAP_COMPUTE_IF_ABSENT)
    public boolean get(final String property) {
        Integer index = propertyIndexMap.get(property);
        if (index == null) {
            index = last;
            propertyIndexMap.put(property, index);
            last++;
        }
        return bitSet.get(index);
    }

    public void set(final String property) {
        bitSet.set(propertyIndexMap.get(property));
    }

    public void clear() {
        bitSet.clear();
    }
}

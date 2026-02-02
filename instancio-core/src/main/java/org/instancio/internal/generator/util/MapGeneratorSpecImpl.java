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
package org.instancio.internal.generator.util;

import org.instancio.Random;
import org.instancio.exception.InstancioException;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.util.ReflectionUtils;

import java.util.Map;

public class MapGeneratorSpecImpl<K, V> extends MapGenerator<K, V> {

    public MapGeneratorSpecImpl(final GeneratorContext context) {
        super(context);
        setDelegating(true);
        // Type is either resolved from the field or specified explicitly
        // by the user via generator.subtype() method
        super.mapType = null; // NOPMD
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<K, V> tryGenerateNonNull(final Random random) {
        if (mapType != null) {
            return (Map<K, V>) ReflectionUtils.newInstance(mapType);
        }
        throw new InstancioException(getClass() + " should delegate to another generator");
    }
}

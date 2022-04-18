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
package org.instancio.generator.lang;

import org.instancio.Generator;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.ReflectionUtils;
import org.instancio.util.Verify;

public class EnumGenerator<E extends Enum<E>> implements Generator<E> {
    private final Class<E> enumClass;

    public EnumGenerator(final Class<E> enumClass) {
        this.enumClass = Verify.notNull(enumClass, "Enum class must not be null");
    }

    @Override
    public E generate(final RandomProvider random) {
        final E[] enumValues = ReflectionUtils.getEnumValues(enumClass);
        return random.oneOf(enumValues);
    }
}

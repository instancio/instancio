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

import org.instancio.Generator;
import org.instancio.Random;

import java.util.EnumSet;

public class EnumSetGenerator<E extends Enum<E>> implements Generator<EnumSet<E>> {

    private final Class<E> enumClass;

    public EnumSetGenerator(final Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public EnumSet<E> generate(final Random random) {
        return EnumSet.noneOf(enumClass);
    }

}

/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.internal.generator.lang;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.AbstractGenerator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class ClassGenerator extends AbstractGenerator<Class<?>> {

    private static final Class<?>[] CLASSES = {
            byte.class, short.class, int.class, long.class, float.class, double.class, boolean.class, char.class,
            Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Boolean.class, Character.class,
            CharSequence.class, Number.class, String.class, Object.class, Object[].class, Optional.class, UUID.class,
            Collection.class, List.class, Map.class, Set.class
    };

    public ClassGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return null;
    }

    @Override
    protected Class<?> tryGenerateNonNull(final Random random) {
        return random.oneOf(CLASSES);
    }
}

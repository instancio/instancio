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

import org.instancio.exception.InstancioException;
import org.instancio.generator.AbstractGenerator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.random.RandomProvider;
import org.instancio.util.Verify;

import java.lang.reflect.Method;

public class EnumGenerator extends AbstractGenerator<Enum<?>> {
    private final Class<?> enumClass;

    public EnumGenerator(final GeneratorContext context, final Class<?> enumClass) {
        super(context);
        this.enumClass = Verify.notNull(enumClass, "Enum class must not be null");
    }

    @Override
    public Enum<?> generate(final RandomProvider random) {
        try {
            Method m = enumClass.getDeclaredMethod("values");
            Enum<?>[] res = (Enum<?>[]) m.invoke(null);
            return random.from(res);
        } catch (Exception ex) {
            throw new InstancioException("Error generating enum value for: " + enumClass.getName(), ex);
        }
    }
}

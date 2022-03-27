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
package org.instancio;

import org.instancio.internal.GeneratedHints;
import org.instancio.util.TypeUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Generic interface for generating values.
 *
 * @param <T> type to generate
 * @see org.instancio.Generators
 */
@FunctionalInterface
public interface Generator<T> extends GeneratorSpec<T> {

    /**
     * Depending on implementation, repeated invocations may return different values.
     * Some implementations may return random values on each invocation, while others
     * may return predefined values or random values from a given set.
     * <p>
     * Generators can be passed in as a lambda function. The following example
     * shows how to override generation strategy for certain fields.
     *
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *         .supply(field("age"), oneOf(20, 30, 40, 50))
     *         .supply(field("location"), () -> "Canada")
     *         .supply(all(LocalDateTime.class), () -> LocalDateTime.now())
     *         .create();
     * }</pre>
     *
     * @return generated value
     */
    T generate();

    default boolean isDelegating() {
        return false;
    }

    default void setDelegate(Generator<?> delegate) {
    }

    default Class<?> targetType() {
        final ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        Type genericType = genericSuperclass.getActualTypeArguments()[0];
        return TypeUtils.getRawType(genericType);
    }

    default GeneratedHints getHints() {
        // ignore children by default to ensure values created
        // from user-supplied generators are not modified
        return GeneratedHints.builder().ignoreChildren(true).build();
    }
}

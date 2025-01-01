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
package org.instancio.internal.generator.sequence;

import org.instancio.exception.InstancioApiException;
import org.instancio.generator.specs.NumericSequenceGeneratorSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.instancio.internal.util.NumberUtils;
import org.instancio.internal.util.TypeUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies {@link NumericSequenceGeneratorSpec} implementations.
 *
 * @param <T> number type
 */
public abstract class AbstractNumericSequenceGeneratorTestTemplate<T extends Number & Comparable<T>>
        extends AbstractGeneratorTestTemplate<T, AbstractNumericSequenceGenerator<T>> {

    @Test
    void shouldStartWithOneAndIncrementByOne() {
        final AbstractGenerator<T> generator = generator();

        final List<Long> result = Stream.generate(() -> generator.generate(random))
                .map(Number::longValue)
                .limit(5)
                .collect(Collectors.toList());

        assertThat(result).containsExactly(1L, 2L, 3L, 4L, 5L);
    }

    @Test
    void withNextFunction() {
        final AbstractNumericSequenceGenerator<T> generator = generator();

        generator
                .start(asT(2))
                .next(num -> asT(num.longValue() * num.longValue()));

        final List<Long> result = Stream.generate(() -> generator.generate(random))
                .map(Number::longValue)
                .limit(5)
                .collect(Collectors.toList());

        assertThat(result).containsExactly(2L, 4L, 16L, 256L, 65536L);
    }

    @Test
    void validation() {
        final AbstractNumericSequenceGenerator<T> generator = generator();

        assertThatThrownBy(() -> generator.start(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("sequence 'start' value must not be null");

        assertThatThrownBy(() -> generator.next(null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("sequence 'next' function must not be null");
    }

    @SuppressWarnings("unchecked")
    private T asT(final long value) {
        final Class<?> numberType = TypeUtils.getGenericSuperclassTypeArgument(getClass());
        assertThat(numberType).isNotNull();
        return (T) NumberUtils.longConverter(numberType).apply(value);
    }
}

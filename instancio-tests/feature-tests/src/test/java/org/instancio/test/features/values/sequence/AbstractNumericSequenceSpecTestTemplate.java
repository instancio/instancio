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
package org.instancio.test.features.values.sequence;

import org.instancio.generator.specs.NumericSequenceSpec;
import org.instancio.internal.util.NumberUtils;
import org.instancio.internal.util.TypeUtils;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractNumericSequenceSpecTestTemplate<T extends Number & Comparable<T>>
        extends AbstractValueSpecTestTemplate<T> {

    protected abstract NumericSequenceSpec<T> spec();

    @Override
    protected void assertDefaultSpecValue(final T actual) {
        assertThat(actual.longValue()).isPositive();
    }

    @Test
    void sequenceShouldStartFromOneAndIncrementByOne() {
        final List<Long> actual = spec().stream()
                .limit(5)
                .map(Number::longValue)
                .collect(Collectors.toList());

        assertThat(actual).containsExactly(1L, 2L, 3L, 4L, 5L);
    }

    @Test
    void sequenceWithNextFunction() {
        final List<Long> actual = spec()
                .start(asT(5))
                .next(n -> asT(n.longValue() * 2))
                .stream()
                .limit(5)
                .map(Number::longValue)
                .collect(Collectors.toList());

        assertThat(actual).containsExactly(5L, 10L, 20L, 40L, 80L);
    }

    @SuppressWarnings("unchecked")
    private T asT(final long value) {
        final Class<?> numberType = TypeUtils.getGenericSuperclassTypeArgument(getClass());
        assertThat(numberType).isNotNull();
        return (T) NumberUtils.longConverter(numberType).apply(value);
    }
}

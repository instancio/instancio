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
package org.instancio.generators.coretypes;

import org.instancio.GeneratorContext;
import org.instancio.util.ReflectionUtils;
import org.instancio.util.TypeUtils;
import org.instancio.util.Verify;

import java.util.Optional;

public abstract class AbstractRandomComparableNumberGeneratorSpec<T extends Number & Comparable<T>>
        extends AbstractRandomNumberGeneratorSpec<T> {

    private static final String MAX_VALUE_FIELD = "MAX_VALUE";
    private static final String MIN_VALUE_FIELD = "MIN_VALUE";

    protected AbstractRandomComparableNumberGeneratorSpec(
            final GeneratorContext context, final T min, final T max, final boolean nullable) {

        super(context, min, max, nullable);
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the specified {@code min} value is greater than or equal to the current {@code max} value,
     * the {@code max} value will be updated to a value higher than the given {@code min}.
     */
    @Override
    public NumberGeneratorSpec<T> min(final T min) {
        this.min = Verify.notNull(min);
        if (min.compareTo(max) >= 0) {
            max = getNumberClassConstant(MAX_VALUE_FIELD).orElse(max);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * If the specified {@code max} value is less than or equal to the current {@code min} value,
     * the {@code min} value will be updated to a value lower than the given {@code max}.
     */
    @Override
    public NumberGeneratorSpec<T> max(final T max) {
        this.max = Verify.notNull(max);
        if (max.compareTo(min) <= 0) {
            min = getNumberClassConstant(MIN_VALUE_FIELD).orElse(min);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    private Optional<T> getNumberClassConstant(final String fieldName) {
        final Class<?> numberClass = TypeUtils.getGenericSuperclassRawTypeArgument(getClass());
        return Optional.ofNullable((T) ReflectionUtils.safeStaticFieldValue(numberClass, fieldName));
    }
}

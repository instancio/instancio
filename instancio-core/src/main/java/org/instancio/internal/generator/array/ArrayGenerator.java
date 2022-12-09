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
package org.instancio.internal.generator.array;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.generator.PopulateAction;
import org.instancio.generator.hints.ArrayHint;
import org.instancio.generator.specs.ArrayGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.InternalHint;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.internal.util.Constants;
import org.instancio.internal.util.NumberUtils;
import org.instancio.settings.Keys;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrayGenerator<T> extends AbstractGenerator<T> implements ArrayGeneratorSpec<T> {

    protected int minLength;
    protected int maxLength;
    private boolean nullable;
    private boolean nullableElements;
    private Class<?> arrayType;
    private List<Object> withElements;

    public ArrayGenerator(final GeneratorContext context) {
        super(context);
        this.minLength = context.getSettings().get(Keys.ARRAY_MIN_LENGTH);
        this.maxLength = context.getSettings().get(Keys.ARRAY_MAX_LENGTH);
        this.nullable = context.getSettings().get(Keys.ARRAY_NULLABLE);
        this.nullableElements = context.getSettings().get(Keys.ARRAY_ELEMENTS_NULLABLE);
    }

    public ArrayGenerator(final GeneratorContext context, final Class<?> arrayType) {
        this(context);
        this.arrayType = ApiValidator.notNull(arrayType, "Type must not be null");
    }

    @Override
    public ArrayGeneratorSpec<T> minLength(final int length) {
        this.minLength = ApiValidator.validateLength(length);
        this.maxLength = NumberUtils.calculateNewMax(maxLength, minLength, Constants.RANGE_ADJUSTMENT_PERCENTAGE);
        return this;
    }

    @Override
    public ArrayGeneratorSpec<T> maxLength(final int length) {
        this.maxLength = ApiValidator.validateLength(length);
        this.minLength = NumberUtils.calculateNewMin(minLength, maxLength, Constants.RANGE_ADJUSTMENT_PERCENTAGE);
        return this;
    }

    @Override
    public ArrayGeneratorSpec<T> length(final int length) {
        this.maxLength = ApiValidator.validateLength(length);
        this.minLength = length;
        return this;
    }

    @Override
    public ArrayGeneratorSpec<T> nullable() {
        this.nullable = true;
        return this;
    }

    @Override
    public ArrayGeneratorSpec<T> nullableElements() {
        this.nullableElements = true;
        return this;
    }

    @Override
    public ArrayGenerator<T> subtype(final Class<?> type) {
        ApiValidator.isTrue(type != null && type.isArray(), "Type must be an array: %s", type);
        this.arrayType = type;
        return this;
    }

    @Override
    @SafeVarargs
    public final ArrayGeneratorSpec<T> with(final T... elements) {
        ApiValidator.notEmpty(elements, "'array().with(...)' must contain at least one element");

        if (withElements == null) {
            withElements = new ArrayList<>();
        }
        Collections.addAll(withElements, elements);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T generate(final Random random) {
        ApiValidator.isTrue(arrayType.isArray(), "Expected an array type: %s", arrayType);

        if (random.diceRoll(nullable)) {
            return null;
        }

        final int length = random.intRange(minLength, maxLength)
                + (withElements == null ? 0 : withElements.size());

        return (T) Array.newInstance(arrayType.getComponentType(), length);
    }

    @Override
    public Hints hints() {
        return Hints.builder()
                .populateAction(PopulateAction.ALL)
                .with(ArrayHint.builder()
                        .nullableElements(nullableElements)
                        .withElements(withElements)
                        .shuffle(!CollectionUtils.isNullOrEmpty(withElements))
                        .build())
                .with(InternalHint.builder()
                        .targetClass(arrayType)
                        .build())
                .build();
    }
}

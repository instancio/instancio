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
package org.instancio.generator.array;

import org.instancio.generator.AbstractGenerator;
import org.instancio.generator.GeneratedHints;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.random.RandomProvider;
import org.instancio.settings.Keys;
import org.instancio.util.Verify;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ArrayGenerator<T> extends AbstractGenerator<T> implements ArrayGeneratorSpec<T> {

    private static final String NEGATIVE_LENGTH = "Length must not be negative: %s";
    private int minLength;
    private int maxLength;
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
        this.arrayType = Verify.notNull(arrayType, "Type must not be null");
    }

    @Override
    public ArrayGeneratorSpec<T> minLength(final int length) {
        Verify.isTrue(length >= 0, NEGATIVE_LENGTH, length);
        this.minLength = length;
        this.maxLength = Math.max(maxLength, minLength);
        return this;
    }

    @Override
    public ArrayGeneratorSpec<T> maxLength(final int length) {
        Verify.isTrue(length >= 0, NEGATIVE_LENGTH, length);
        this.maxLength = length;
        this.minLength = Math.min(minLength, maxLength);
        return this;
    }

    @Override
    public ArrayGeneratorSpec<T> length(final int length) {
        Verify.isTrue(length >= 0, NEGATIVE_LENGTH, length);
        this.maxLength = length;
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

    public ArrayGenerator<T> type(final Class<?> type) {
        Verify.isTrue(type != null && type.isArray(), "Type must be an array: %s", type);
        this.arrayType = type;
        return this;
    }

    @Override
    public ArrayGeneratorSpec<T> with(final T... elements) {
        ApiValidator.notEmpty(elements, "'array().with(...)' must contain at least one element");

        if (withElements == null) {
            withElements = new ArrayList<>();
        }
        Collections.addAll(withElements, elements);
        return this;
    }

    @Override
    public Optional<Class<?>> targetClass() {
        return Optional.ofNullable(arrayType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T generate(final RandomProvider random) {
        Verify.state(arrayType.isArray(), "Expected an array type: %s", arrayType);

        if (random.diceRoll(nullable)) {
            return null;
        }

        final int length = random.intRange(minLength, maxLength + 1)
                + (withElements == null ? 0 : withElements.size());

        return (T) Array.newInstance(arrayType.getComponentType(), length);
    }

    @Override
    public GeneratedHints getHints() {
        return GeneratedHints.builder()
                .ignoreChildren(false)
                .nullableResult(nullable)
                .nullableElements(nullableElements)
                .withElements(withElements)
                .build();
    }
}

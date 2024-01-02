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
package org.instancio.internal.generator.array;

import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.ArrayHint;
import org.instancio.generator.specs.ArrayGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.internal.util.NumberUtils;
import org.instancio.settings.Keys;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrayGenerator<T> extends AbstractGenerator<T> implements ArrayGeneratorSpec<T> {

    protected int minLength;
    protected int maxLength;
    private boolean nullableElements;
    private Class<?> arrayType;
    private List<Object> withElements;

    public ArrayGenerator(final GeneratorContext context) {
        super(context);
        this.minLength = context.getSettings().get(Keys.ARRAY_MIN_LENGTH);
        this.maxLength = context.getSettings().get(Keys.ARRAY_MAX_LENGTH);
        super.nullable(context.getSettings().get(Keys.ARRAY_NULLABLE));
        this.nullableElements = context.getSettings().get(Keys.ARRAY_ELEMENTS_NULLABLE);
    }

    public ArrayGenerator(final GeneratorContext context, final Class<?> arrayType) {
        this(context);
        this.arrayType = arrayType;
    }

    @Override
    public String apiMethod() {
        return "array()";
    }

    @Override
    public ArrayGeneratorSpec<T> minLength(final int length) {
        this.minLength = ApiValidator.validateLength(length);
        this.maxLength = NumberUtils.calculateNewMaxSize(maxLength, minLength);
        return this;
    }

    @Override
    public ArrayGeneratorSpec<T> maxLength(final int length) {
        this.maxLength = ApiValidator.validateLength(length);
        this.minLength = NumberUtils.calculateNewMinSize(minLength, maxLength);
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
        super.nullable();
        return this;
    }

    @Override
    public ArrayGeneratorSpec<T> nullable(final boolean isNullable) {
        super.nullable(isNullable);
        return this;
    }

    @Override
    public ArrayGeneratorSpec<T> nullableElements() {
        this.nullableElements = true;
        return this;
    }

    @Override
    public ArrayGenerator<T> subtype(final Class<?> type) {
        ApiValidator.isTrue(type != null && type.isArray(), "type must be an array: %s", type);
        this.arrayType = type;
        return this;
    }

    @Override
    @SafeVarargs
    public final ArrayGeneratorSpec<T> with(final T... elements) {
        ApiValidator.notEmpty(elements, "'array().with(...)' must contain at least one element");

        if (withElements == null) {
            withElements = new ArrayList<>(elements.length);
        }
        Collections.addAll(withElements, elements);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T tryGenerateNonNull(final Random random) {
        final int length = random.intRange(minLength, maxLength)
                + (withElements == null ? 0 : withElements.size());

        return (T) Array.newInstance(arrayType.getComponentType(), length);
    }

    @Override
    public Hints hints() {
        return Hints.builder()
                .afterGenerate(AfterGenerate.POPULATE_ALL)
                .with(ArrayHint.builder()
                        .nullableElements(nullableElements)
                        .withElements(withElements)
                        .shuffle(!CollectionUtils.isNullOrEmpty(withElements))
                        .build())
                .with(InternalGeneratorHint.builder()
                        .targetClass(arrayType)
                        .build())
                .build();
    }
}

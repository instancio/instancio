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
package org.instancio.generators;

import org.instancio.internal.GeneratedHints;
import org.instancio.internal.model.ModelContext;
import org.instancio.settings.Setting;
import org.instancio.util.Verify;

import java.lang.reflect.Array;

public class ArrayGenerator<T> extends AbstractRandomGenerator<T> implements ArrayGeneratorSpec<T> {

    private int minLength;
    private int maxLength;
    private boolean nullable;
    private boolean nullableElements;
    private Class<?> componentType;

    public ArrayGenerator(final ModelContext<?> context) {
        super(context);
    }

    public ArrayGenerator(final ModelContext<?> context, final Class<?> componentType) {
        super(context);
        this.componentType = Verify.notNull(componentType, "Type must not be null");
        this.minLength = context.getSettings().get(Setting.ARRAY_MIN_LENGTH);
        this.maxLength = context.getSettings().get(Setting.ARRAY_MAX_LENGTH);
        this.nullable = context.getSettings().get(Setting.ARRAY_NULLABLE);
        this.nullableElements = context.getSettings().get(Setting.ARRAY_ELEMENTS_NULLABLE);
    }

    @Override
    public ArrayGeneratorSpec<T> minLength(final int length) {
        Verify.isTrue(length >= 0, "Length cannot be negative: %s", length);
        this.minLength = length;
        this.maxLength = Math.max(maxLength, minLength);
        return this;
    }

    @Override
    public ArrayGeneratorSpec<T> maxLength(final int length) {
        Verify.isTrue(length >= 0, "Length cannot be negative: %s", length);
        this.maxLength = length;
        this.minLength = Math.min(minLength, maxLength);
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
        this.componentType = Verify.notNull(type, "Type must not be null");
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T generate() {
        Verify.notNull(componentType, "Array component type is null");
        if (random().diceRoll(nullable)) {
            return null;
        }
        return (T) Array.newInstance(componentType, random().intBetween(minLength, maxLength + 1));
    }

    @Override
    public GeneratedHints getHints() {
        return GeneratedHints.builder()
                .ignoreChildren(false)
                .nullableResult(nullableElements)
                .build();
    }
}

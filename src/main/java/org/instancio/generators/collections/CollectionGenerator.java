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
package org.instancio.generators.collections;

import org.instancio.exception.InstancioException;
import org.instancio.generators.AbstractRandomGenerator;
import org.instancio.internal.GeneratedHints;
import org.instancio.internal.model.ModelContext;
import org.instancio.settings.Setting;
import org.instancio.util.Verify;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionGenerator<T> extends AbstractRandomGenerator<Collection<T>> implements CollectionGeneratorSpec<T> {

    protected int minSize;
    protected int maxSize;
    protected boolean nullable;
    protected boolean nullableElements;
    protected Class<?> type;

    public CollectionGenerator(final ModelContext<?> context) {
        super(context);
        this.minSize = context.getSettings().get(Setting.COLLECTION_MIN_SIZE);
        this.maxSize = context.getSettings().get(Setting.COLLECTION_MAX_SIZE);
        this.nullable = context.getSettings().get(Setting.COLLECTION_NULLABLE);
        this.nullableElements = context.getSettings().get(Setting.COLLECTION_ELEMENTS_NULLABLE);
        this.type = ArrayList.class; // default collection type
    }

    @Override
    public CollectionGeneratorSpec<T> minSize(final int size) {
        Verify.isTrue(size >= 0, "Size cannot be negative: %s", size);
        this.minSize = size;
        this.maxSize = Math.max(minSize, maxSize);
        return this;
    }

    @Override
    public CollectionGeneratorSpec<T> maxSize(final int size) {
        Verify.isTrue(size >= 0, "Size cannot be negative: %s", size);
        this.maxSize = size;
        this.minSize = Math.min(minSize, maxSize);
        return this;
    }

    @Override
    public CollectionGeneratorSpec<T> nullable() {
        this.nullable = true;
        return this;
    }

    @Override
    public CollectionGeneratorSpec<T> nullableElements() {
        this.nullableElements = true;
        return this;
    }

    @Override
    public CollectionGeneratorSpec<T> type(final Class<?> type) {
        this.type = Verify.notNull(type, "Type must not be null");
        return this;
    }

    @Override
    public Collection<T> generate() {
        try {
            return random().diceRoll(nullable) ? null : (Collection<T>) type.newInstance();
        } catch (Exception ex) {
            throw new InstancioException(String.format("Error creating instance of: %s", type), ex);
        }
    }

    @Override
    public GeneratedHints getHints() {
        return GeneratedHints.builder()
                .dataStructureSize(random().intBetween(minSize, maxSize + 1))
                .ignoreChildren(false)
                .nullableResult(nullable)
                .nullableElements(nullableElements)
                .build();
    }
}

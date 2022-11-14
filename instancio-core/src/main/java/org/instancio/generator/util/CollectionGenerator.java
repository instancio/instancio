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
package org.instancio.generator.util;

import org.instancio.Random;
import org.instancio.generator.AbstractGenerator;
import org.instancio.generator.GeneratedHints;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.CollectionGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.settings.Keys;
import org.instancio.util.Constants;
import org.instancio.util.NumberUtils;
import org.instancio.util.Sonar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CollectionGenerator<T> extends AbstractGenerator<Collection<T>> implements CollectionGeneratorSpec<T> {
    private static final Logger LOG = LoggerFactory.getLogger(CollectionGenerator.class);

    protected int minSize;
    protected int maxSize;
    protected boolean nullable;
    protected boolean nullableElements;
    @SuppressWarnings("PMD.LooseCoupling")
    protected Class<?> type = ArrayList.class; // default collection type
    protected List<Object> withElements;

    public CollectionGenerator(final GeneratorContext context) {
        super(context);
        this.minSize = context.getSettings().get(Keys.COLLECTION_MIN_SIZE);
        this.maxSize = context.getSettings().get(Keys.COLLECTION_MAX_SIZE);
        this.nullable = context.getSettings().get(Keys.COLLECTION_NULLABLE);
        this.nullableElements = context.getSettings().get(Keys.COLLECTION_ELEMENTS_NULLABLE);
    }

    @Override
    public CollectionGeneratorSpec<T> size(final int size) {
        this.minSize = ApiValidator.validateSize(size);
        this.maxSize = size;
        return this;
    }

    @Override
    public CollectionGeneratorSpec<T> minSize(final int size) {
        this.minSize = ApiValidator.validateSize(size);
        this.maxSize = NumberUtils.calculateNewMax(maxSize, minSize, Constants.RANGE_ADJUSTMENT_PERCENTAGE);
        return this;
    }

    @Override
    public CollectionGeneratorSpec<T> maxSize(final int size) {
        this.maxSize = ApiValidator.validateSize(size);
        this.minSize = NumberUtils.calculateNewMin(minSize, maxSize, Constants.RANGE_ADJUSTMENT_PERCENTAGE);
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
    public CollectionGeneratorSpec<T> subtype(final Class<?> type) {
        this.type = ApiValidator.notNull(type, "Type must not be null");
        return this;
    }

    @SafeVarargs
    @Override
    public final CollectionGeneratorSpec<T> with(final T... elements) {
        ApiValidator.notEmpty(elements, "'collection().with(...)' must contain at least one element");
        if (withElements == null) {
            withElements = new ArrayList<>();
        }
        Collections.addAll(withElements, elements);
        return this;
    }

    @Override
    @SuppressWarnings({"unchecked", Sonar.RETURN_EMPTY_COLLECTION})
    public Collection<T> generate(final Random random) {
        try {
            return random.diceRoll(nullable) ? null : (Collection<T>) type.getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            LOG.debug("Error creating instance of: {}", type, ex);
            return null; // NOPMD
        }
    }

    @Override
    public GeneratedHints getHints() {
        return GeneratedHints.builder()
                .dataStructureSize(getContext().random().intRange(minSize, maxSize))
                .ignoreChildren(false)
                .nullableResult(nullable)
                .nullableElements(nullableElements)
                .withElements(withElements)
                .build();
    }
}

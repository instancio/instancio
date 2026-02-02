/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.internal.generator.util;

import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.CollectionHint;
import org.instancio.generator.specs.CollectionGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.ErrorHandler;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.internal.util.ExceptionUtils;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.NumberUtils;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.Sonar;
import org.instancio.settings.Keys;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CollectionGenerator<T> extends AbstractGenerator<Collection<T>> implements CollectionGeneratorSpec<T> {
    private static final Class<?> DEFAULT_COLLECTION_TYPE = ArrayList.class; // NOPMD

    protected int minSize;
    protected int maxSize;
    private boolean nullableElements;
    private boolean unique;
    private List<Object> withElements;
    protected Class<?> collectionType;

    public CollectionGenerator(final GeneratorContext context) {
        super(context);
        this.minSize = context.getSettings().get(Keys.COLLECTION_MIN_SIZE);
        this.maxSize = context.getSettings().get(Keys.COLLECTION_MAX_SIZE);
        super.nullable(context.getSettings().get(Keys.COLLECTION_NULLABLE));
        this.nullableElements = context.getSettings().get(Keys.COLLECTION_ELEMENTS_NULLABLE);
        this.collectionType = DEFAULT_COLLECTION_TYPE;
    }

    @Override
    public String apiMethod() {
        return "collection()";
    }

    @Override
    public CollectionGenerator<T> size(final int size) {
        this.minSize = ApiValidator.validateSize(size);
        this.maxSize = size;
        return this;
    }

    @Override
    public CollectionGenerator<T> minSize(final int size) {
        this.minSize = ApiValidator.validateSize(size);
        this.maxSize = NumberUtils.calculateNewMaxSize(maxSize, minSize);
        return this;
    }

    @Override
    public CollectionGenerator<T> maxSize(final int size) {
        this.maxSize = ApiValidator.validateSize(size);
        this.minSize = NumberUtils.calculateNewMinSize(minSize, maxSize);
        return this;
    }

    @Override
    public CollectionGenerator<T> nullable() {
        super.nullable();
        return this;
    }

    @Override
    public CollectionGenerator<T> nullable(final boolean isNullable) {
        super.nullable(isNullable);
        return this;
    }

    @Override
    public CollectionGenerator<T> nullableElements() {
        this.nullableElements = true;
        return this;
    }

    @Override
    public CollectionGenerator<T> subtype(final Class<?> type) {
        this.collectionType = ApiValidator.notNull(type, "type must not be null");
        return this;
    }

    @Override
    public CollectionGenerator<T> unique() {
        this.unique = true;
        return this;
    }

    @SafeVarargs
    @Override
    public final CollectionGenerator<T> with(final T... elements) {
        ApiValidator.notEmpty(elements, "'collection().with(...)' must contain at least one element");
        if (withElements == null) {
            withElements = new ArrayList<>();
        }
        Collections.addAll(withElements, elements);
        return this;
    }

    @Override
    @SuppressWarnings({"unchecked", Sonar.RETURN_EMPTY_COLLECTION})
    protected Collection<T> tryGenerateNonNull(final Random random) {
        try {
            Constructor<?> ctor = ReflectionUtils.setAccessible(collectionType.getDeclaredConstructor());
            return (Collection<T>) ctor.newInstance();
        } catch (Exception ex) {
            final String msg = String.format("Error creating instance of: %s", collectionType);

            if (ErrorHandler.shouldFailOnError(getContext().getSettings())) {
                throw Fail.withFataInternalError(msg, ex);
            }
            ExceptionUtils.logException(msg, ex);
            return null; //NOPMD
        }
    }

    @Override
    public Hints hints() {
        return Hints.builder()
                .afterGenerate(AfterGenerate.POPULATE_ALL)
                .with(CollectionHint.builder()
                        .generateElements(getContext().random().intRange(minSize, maxSize))
                        .nullableElements(nullableElements)
                        .withElements(withElements)
                        .unique(unique)
                        .shuffle(!CollectionUtils.isNullOrEmpty(withElements))
                        .build())
                .with(InternalGeneratorHint.builder()
                        .targetClass(collectionType)
                        .nullableResult(isNullable())
                        .build())
                .build();
    }
}

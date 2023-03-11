/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.internal.generator.misc;

import org.instancio.Random;
import org.instancio.exception.InstancioException;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.generator.specs.EmitGeneratorSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class EmitGenerator<T> extends AbstractGenerator<T>
        implements EmitGeneratorSpec<T> {

    private final Queue<T> items = new LinkedList<>();
    private boolean shuffle;
    private boolean ignoreUnused;
    private WhenEmptyAction whenEmptyAction = WhenEmptyAction.EMIT_RANDOM;

    public enum WhenEmptyAction {
        EMIT_NULL, EMIT_RANDOM, FAIL
    }

    public EmitGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        // return null because there's no validation of the target class
        // for this spec (vararg can potentially have different types)
        return null;
    }

    @Override
    public EmitGenerator<T> items(final T... items) {
        ApiValidator.notNull(items, "Items must not be null");
        Collections.addAll(this.items, items);
        return this;
    }

    @Override
    public EmitGenerator<T> item(final T item, final int n) {
        ApiValidator.isTrue(n >= 0, "Number of items must not be negative: " + n);
        for (int i = 0; i < n; i++) {
            items.add(item);
        }
        return this;
    }

    @Override
    public EmitGenerator<T> shuffle() {
        shuffle = true;
        return this;
    }

    @Override
    public EmitGenerator<T> ignoreUnused() {
        ignoreUnused = true;
        return this;
    }

    @Override
    public EmitGenerator<T> whenEmptyEmitNull() {
        whenEmptyAction = WhenEmptyAction.EMIT_NULL;
        return this;
    }

    @Override
    public EmitGenerator<T> whenEmptyEmitRandom() {
        whenEmptyAction = WhenEmptyAction.EMIT_RANDOM;
        return this;
    }

    @Override
    public EmitGenerator<T> whenEmptyThrowException() {
        whenEmptyAction = WhenEmptyAction.FAIL;
        return this;
    }

    public WhenEmptyAction getWhenEmptyAction() {
        return whenEmptyAction;
    }

    public boolean hasMore() {
        return !items.isEmpty();
    }

    public Collection<T> getItems() {
        return items;
    }

    public boolean isIgnoreUnused() {
        return ignoreUnused;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T tryGenerateNonNull(final Random random) {
        if (items.isEmpty()) {
            // isEmpty() check should be done by the caller,
            // therefore, this branch should not be reachable
            throw new InstancioException("Invalid call to emit() - no items available");
        }

        if (shuffle) {
            CollectionUtils.shuffle((Collection<Object>) items, random);
            shuffle = false;
        }

        return items.poll();
    }

    @Override
    public Hints hints() {
        return Hints.builder()
                .afterGenerate(AfterGenerate.DO_NOT_MODIFY)
                .with(InternalGeneratorHint.builder()
                        .emitNull(whenEmptyAction == WhenEmptyAction.EMIT_NULL)
                        .build())
                .build();
    }
}

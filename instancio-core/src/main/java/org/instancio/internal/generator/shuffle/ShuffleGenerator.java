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
package org.instancio.internal.generator.shuffle;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.ShuffleSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ShuffleGenerator<T> extends AbstractGenerator<Collection<T>>
        implements ShuffleSpec<T> {

    private List<T> items;

    public ShuffleGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        // no validation is needed because this spec
        // is only available via Instancio.gen()
        return null;
    }

    @Override
    @SafeVarargs
    public final ShuffleGenerator<T> shuffle(final T... array) {
        ApiValidator.notNull(array, "array must not be null");
        items = CollectionUtils.asUnmodifiableList(array);
        return this;
    }

    @Override
    public ShuffleGenerator<T> shuffle(final Collection<T> collection) {
        ApiValidator.notNull(collection, "collection must not be null");
        items = Collections.unmodifiableList(new ArrayList<>(collection));
        return this;
    }

    @Override
    public ShuffleGenerator<T> nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected Collection<T> tryGenerateNonNull(final Random random) {
        final List<T> shuffledCopy = new ArrayList<>(items);
        CollectionUtils.shuffle(shuffledCopy, random);
        return shuffledCopy;
    }

}

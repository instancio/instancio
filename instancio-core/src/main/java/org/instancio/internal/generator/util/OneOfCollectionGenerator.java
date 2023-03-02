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
package org.instancio.internal.generator.util;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.OneOfCollectionSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.support.Global;

import java.util.Collection;

public class OneOfCollectionGenerator<T> extends AbstractGenerator<T> implements OneOfCollectionSpec<T> {

    private Collection<T> values;

    public OneOfCollectionGenerator() {
        this(Global.generatorContext());
    }

    public OneOfCollectionGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return null;
    }

    @Override
    public OneOfCollectionGenerator<T> oneOf(final Collection<T> values) {
        this.values = ApiValidator.notEmpty(values, "Collection must have at least one element");
        return this;
    }

    @Override
    public OneOfCollectionGenerator<T> nullable() {
        super.nullable();
        return this;
    }

    @Override
    public T tryGenerateNonNull(final Random random) {
        return random.oneOf(values);
    }
}

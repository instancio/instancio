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
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.random.RandomProvider;

public class OneOfArrayGenerator<T> extends AbstractGenerator<T> implements OneOfArrayGeneratorSpec<T> {

    private T[] values;

    public OneOfArrayGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public OneOfArrayGeneratorSpec<T> oneOf(final T[] values) {
        this.values = ApiValidator.notEmpty(values, "Array must have at least one element");
        return this;
    }

    @Override
    public T generate(final RandomProvider random) {
        return random.oneOf(values);
    }
}

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
package org.instancio.internal.generator.domain.id;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.IsbnSpec;
import org.instancio.internal.context.Global;
import org.instancio.internal.generator.AbstractGenerator;

public class IsbnGenerator extends AbstractGenerator<String> implements IsbnSpec {

    private final EanGenerator delegate;

    public IsbnGenerator() {
        this(Global.generatorContext());
    }

    public IsbnGenerator(final GeneratorContext context) {
        super(context);
        delegate = new EanGenerator(context);
    }

    @Override
    public String apiMethod() {
        return "isbn()";
    }

    @Override
    public IsbnGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected String tryGenerateNonNull(final Random random) {
        return delegate.tryGenerateNonNull(random);
    }
}


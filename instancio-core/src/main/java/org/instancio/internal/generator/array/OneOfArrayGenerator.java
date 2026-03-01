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
package org.instancio.internal.generator.array;

import org.instancio.Random;
import org.instancio.documentation.Initializer;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.generator.specs.OneOfArrayGeneratorSpec;
import org.instancio.generator.specs.OneOfArraySpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.util.Sonar;
import org.jspecify.annotations.Nullable;

@SuppressWarnings({"NotNullFieldNotInitialized", Sonar.NULL_MARKED_NULL_VALUE})
public class OneOfArrayGenerator<T> extends AbstractGenerator<T>
        implements OneOfArrayGeneratorSpec<T>, OneOfArraySpec<T> {

    // Delegates generating a value (not from one of the choices) to the engine
    private static final Hints ALLOW_EMPTY_HINT = Hints.builder()
            .with(InternalGeneratorHint.builder()
                    .emptyResult(true)
                    .build())
            .build();

    private @Nullable T[] values;
    private boolean allowRandomValues;

    public OneOfArrayGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "oneOf()";
    }

    @Initializer
    @SafeVarargs
    @Override
    public final OneOfArrayGenerator<T> oneOf(@Nullable final T... values) {
        this.values = ApiValidator.notEmpty(values, "Array must have at least one element");
        return this;
    }

    @Override
    public OneOfArrayGenerator<T> orRandom() {
        this.allowRandomValues = true;
        return this;
    }

    @Override
    public OneOfArrayGenerator<T> nullable() {
        super.nullable();
        return this;
    }

    @Nullable
    @Override
    public T tryGenerateNonNull(final Random random) {
        return random.oneOf(values);
    }

    @Override
    public Hints hints() {
        return getContext().random().diceRoll(allowRandomValues)
                ? ALLOW_EMPTY_HINT
                : super.hints();
    }
}

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
package org.instancio.internal.generator.misc;

import org.instancio.Random;
import org.instancio.exception.InstancioException;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.generator.PopulateAction;
import org.instancio.internal.util.Sonar;

import static org.instancio.internal.util.ExceptionHandler.conditionalFailOnError;

public final class GeneratorDecorator implements Generator<Object> {

    private final Generator<?> delegate;
    private final Hints hints;

    public GeneratorDecorator(final Generator<?> delegate, final Hints hints) {
        this.delegate = delegate;
        this.hints = hints;
    }

    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    public static Generator<?> decorateActionless(
            final Generator<?> generator,
            final PopulateAction populateAction,
            final GeneratorContext context) {

        // must init() first because decorate method copies Hints,
        // and Hints may depend on setup done in init()
        generator.init(context);

        final Hints originalHints = generator.hints();
        if (originalHints != null && originalHints.populateAction() != null) {
            return generator;
        }

        final Hints newHints = originalHints == null
                ? Hints.withPopulateAction(populateAction)
                : Hints.builder(originalHints).populateAction(populateAction).build();

        return new GeneratorDecorator(generator, newHints);
    }

    @Override
    public void init(final GeneratorContext context) {
        conditionalFailOnError(() -> {
            throw new InstancioException("Unexpected call to Generator.init().");
        });
    }

    @Override
    public Object generate(final Random random) {
        return delegate.generate(random);
    }

    @Override
    public Hints hints() {
        return hints;
    }

}

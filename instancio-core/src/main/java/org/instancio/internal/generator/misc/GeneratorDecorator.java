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
package org.instancio.internal.generator.misc;

import org.instancio.InstancioApi;
import org.instancio.Random;
import org.instancio.TargetSelector;
import org.instancio.documentation.InternalApi;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.internal.generator.AbstractGenerator;

import java.util.function.Supplier;

/**
 * Decorator for generators that might be missing hints.
 *
 * @param <T> generated type
 * @since 2.14.0
 */
@InternalApi
public abstract class GeneratorDecorator<T> implements Generator<T> {

    private final Generator<T> delegate;

    protected GeneratorDecorator(final Generator<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public final void init(final GeneratorContext context) {
        delegate.init(context);
    }

    @Override
    public final T generate(final Random random) {
        return delegate.generate(random);
    }

    public final Generator<T> getDelegate() {
        return delegate;
    }

    /**
     * Decorates suppliers from {@link InstancioApi#supply(TargetSelector, Generator)}.
     *
     * @param supplier to decorate with hints
     * @param <T>      generated type
     * @return generator backed by the given supplier
     */
    public static <T> Generator<T> decorate(final Supplier<T> supplier) {
        return new SupplierAdapter<>(supplier);
    }

    /**
     * Decorates the {@code generator} with the specified {@code hints}.
     * The original hints will be ignored.
     *
     * @param generator to decorate with new hints
     * @param hints     replacement hints
     * @param <T>       generated type
     * @return generator with substituted hints
     */
    public static <T> Generator<T> replaceHints(final Generator<T> generator, final Hints hints) {
        return new GeneratorHintsDecorator<>(generator, hints);
    }

    /**
     * Decorates the {@code generator} with the {@code defaultAfterGenerate} action
     * if the generator does not specify the action in its hints. Retains all
     * the other hints defined by the {@code generator}.
     *
     * @param generator            to decorate with the action
     * @param defaultAfterGenerate the default action
     * @param <T>                  generated type
     * @return generator with updated hints if no action was specified in the original hints
     */
    public static <T> Generator<T> decorateIfNullAfterGenerate(
            final Generator<T> generator,
            final AfterGenerate defaultAfterGenerate) {

        // These generators don't need to be decorated
        // since they are expected to have a non-null AfterGenerate action
        if (generator instanceof AbstractGenerator<?> || generator instanceof GeneratorDecorator) {
            return generator;
        }

        return new GeneratorActionDecorator<>(generator, defaultAfterGenerate);
    }
}

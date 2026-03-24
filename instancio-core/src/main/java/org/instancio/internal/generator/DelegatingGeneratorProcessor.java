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
package org.instancio.internal.generator;

import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.internal.generator.array.ArrayGenerator;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Sonar;
import org.instancio.internal.util.Verify;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

/**
 * Shared logic for processing delegating generators.
 */
@SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
public final class DelegatingGeneratorProcessor {

    /**
     * Processes a generator that may delegate to a built-in generator.
     *
     * @param generator        the generator to process
     * @param node             the target node
     * @param delegateResolver resolves the delegate from the {@link InternalGeneratorHint};
     *                         may return {@code null} if no delegate is available
     * @return the (possibly replaced) generator
     */
    public static Generator<?> process(
            final Generator<?> generator,
            final InternalNode node,
            final Function<InternalGeneratorHint, @Nullable Generator<?>> delegateResolver) {

        if (generator instanceof ArrayGenerator<?> arrayGenerator) {
            arrayGenerator.subtype(node.getTargetClass());
        } else if (generator instanceof final AbstractGenerator<?> g) {
            if (!g.isDelegating()) {
                return generator;
            }

            final Hints hints = Verify.notNull(generator.hints(), "Generator hints are null");
            final InternalGeneratorHint internalHint = Verify.notNull(
                    hints.get(InternalGeneratorHint.class), "InternalGeneratorHint is null");

            final Generator<?> delegate = delegateResolver.apply(internalHint);

            if (delegate == null) {
                return generator;
            }

            if (delegate instanceof AbstractGenerator<?> delegateGenerator) {
                delegateGenerator.nullable(g.isNullable());
            }

            return GeneratorDecorator.replaceHints(delegate, hints);
        }

        return generator;
    }

    private DelegatingGeneratorProcessor() {
        // non-instantiable
    }
}

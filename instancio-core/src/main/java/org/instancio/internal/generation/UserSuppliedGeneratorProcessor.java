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
package org.instancio.internal.generation;

import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.GeneratorResolver;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.generator.SpiGeneratorResolver;
import org.instancio.internal.generator.array.ArrayGenerator;
import org.instancio.internal.generator.misc.EmitGenerator;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Sonar;
import org.jspecify.annotations.NonNull;

import static org.instancio.internal.util.ObjectUtils.defaultIfNull;

/**
 * User-supplied generators may not have all the required information.
 * This class processes generators to ensure they can generate values correctly.
 */
public final class UserSuppliedGeneratorProcessor {

    private final ModelContext context;
    private final GeneratorResolver generatorResolver;
    private final SpiGeneratorResolver spiGeneratorResolver;
    private final EmitGeneratorHelper emitGeneratorHelper;

    public UserSuppliedGeneratorProcessor(
            final ModelContext context,
            final GeneratorResolver generatorResolver,
            final SpiGeneratorResolver spiGeneratorResolver) {

        this.context = context;
        this.generatorResolver = generatorResolver;
        this.spiGeneratorResolver = spiGeneratorResolver;
        this.emitGeneratorHelper = new EmitGeneratorHelper(context);
    }

    @NonNull
    GeneratorResult getGeneratorResult(final @NonNull InternalNode node, final Generator<?> g) {
        final Generator<?> generator = processGenerator(g, node);

        if (generator instanceof EmitGenerator<?> emitGenerator) {
            return emitGeneratorHelper.getResult(emitGenerator, node);
        }

        final Hints hints = generator.hints();
        final InternalGeneratorHint internalHint = hints.get(InternalGeneratorHint.class);
        final boolean nullable = internalHint != null && internalHint.nullableResult();

        if (context.getRandom().diceRoll(nullable)) {
            return GeneratorResult.nullResult();
        }

        if (internalHint != null && internalHint.emptyResult()) {
            return GeneratorResult.emptyResult();
        }

        final Object value = generator.generate(context.getRandom());
        return GeneratorResult.create(value, hints);
    }

    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    private Generator<?> processGenerator(final Generator<?> generator, final InternalNode node) {
        ApiValidator.validateGeneratorUsage(node, generator);

        if (generator instanceof ArrayGenerator<?> arrayGenerator) {
            // If gen.array().subtype() was specified, then node targetClass
            // is expected to have the specified subtype.
            // If no subtype() was specified, then it will be null.
            // Therefore, it's safe to overwrite the generator subtype
            // with node's targetClass
            arrayGenerator.subtype(node.getTargetClass());
        } else if (generator instanceof final AbstractGenerator<?> g) {

            if (!g.isDelegating()) {
                return generator;
            }

            final Hints hints = generator.hints();
            final InternalGeneratorHint internalHint = hints.get(InternalGeneratorHint.class);
            final Generator<?> delegate = resolveDelegate(node, internalHint);

            if (delegate instanceof AbstractGenerator<?> delegateGenerator) {
                final boolean nullable = g.isNullable();
                delegateGenerator.nullable(nullable);
            }

            return GeneratorDecorator.replaceHints(delegate, hints);
        }

        return generator;
    }

    @NonNull
    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    private Generator<?> resolveDelegate(final InternalNode node, final InternalGeneratorHint internalHint) {
        final Class<?> targetClass = defaultIfNull(internalHint.targetClass(), node.getTargetClass());

        final InternalNode actualNode = targetClass == node.getTargetClass()
                ? node : node.toBuilder().targetClass(targetClass).build();

        Generator<?> generator = spiGeneratorResolver.getSpiGenerator(node);

        if (generator == null) {
            generator = generatorResolver.getCached(actualNode);
        }
        return generator;
    }
}
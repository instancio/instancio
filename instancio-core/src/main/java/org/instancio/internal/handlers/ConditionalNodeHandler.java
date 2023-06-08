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
package org.instancio.internal.handlers;

import org.instancio.generator.Generator;
import org.instancio.internal.ConditionalObjectStore;
import org.instancio.internal.conditional.InternalConditional;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResolver;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.generator.misc.EmitGenerator;
import org.instancio.internal.instantiation.Instantiator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.internal.util.Fail;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Predicate;

public class ConditionalNodeHandler implements NodeHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ConditionalNodeHandler.class);

    private final ModelContext<?> context;
    private final ConditionalObjectStore conditionalObjectStore;
    private final UserSuppliedGeneratorProcessor userSuppliedGeneratorProcessor;

    public ConditionalNodeHandler(
            final ModelContext<?> context,
            final ConditionalObjectStore conditionalObjectStore,
            final GeneratorResolver generatorResolver,
            final Instantiator instantiator) {

        this.context = context;
        this.conditionalObjectStore = conditionalObjectStore;
        this.userSuppliedGeneratorProcessor = new UserSuppliedGeneratorProcessor(
                generatorResolver, instantiator);
    }

    @NotNull
    @Override
    public GeneratorResult getResult(@NotNull final InternalNode node) {
        final List<InternalConditional> conditionals = context.getConditionals(node);

        // loop from the end so that the last matching conditional wins
        for (int i = conditionals.size() - 1; i >= 0; i--) {
            final InternalConditional conditional = conditionals.get(i);
            final List<Object> candidateValues = conditionalObjectStore.getValues(conditional.getDestination());

            LOG.trace("Values for destination {}: {}", conditional.getDestination(), candidateValues);

            if (candidateValues.isEmpty()) {
                // Values are not available yet
                LOG.trace("Delayed result for {}", conditional.getDestination());
                return GeneratorResult.delayed();
            }

            if (isSatisfied(candidateValues, conditional.getOriginPredicate())) {
                Generator<?> conditionalGenerator = conditional.getGenerator();

                // TODO refactor to remove this check
                if (conditionalGenerator instanceof EmitGenerator) {
                    final EmitGeneratorHelper helper = new EmitGeneratorHelper(context);
                    return helper.getResult((EmitGenerator<?>) conditionalGenerator, node);
                }

                final Generator<?> generator = userSuppliedGeneratorProcessor.processGenerator(
                        conditionalGenerator, node);

                final Object destinationResult = generator.generate(context.getRandom());
                return GeneratorResult.create(destinationResult, generator.hints());
            }
        }

        // If no conditional, or the condition was not satisfied,
        // return an empty result to process the node as usual
        return GeneratorResult.emptyResult();
    }

    /**
     * When multiple values are associated with the same origin selector,
     * a convention is needed to define a "satisfied" condition.
     * The options are: all, any, first, or last value.
     * The current implementation uses the latter as it seems
     * to make the most sense from user's perspective.
     */
    private static boolean isSatisfied(final List<?> values, final Predicate<Object> predicate) {
        final Object object = CollectionUtils.lastElement(values);
        try {
            return predicate.test(object);
        } catch (ClassCastException ex) {
            throw Fail.withUsageError("error invoking the conditional predicate against generated object of type %s",
                    object.getClass().getTypeName(), ex);
        } catch (Exception ex) {
            throw Fail.withUsageError("error invoking the conditional predicate",
                    object.getClass().getTypeName(), ex);
        }
    }

}

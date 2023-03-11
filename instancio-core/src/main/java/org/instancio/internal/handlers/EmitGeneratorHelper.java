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

import org.instancio.exception.InstancioApiException;
import org.instancio.generator.Hints;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.generator.misc.EmitGenerator;
import org.instancio.internal.generator.misc.EmitGenerator.WhenEmptyAction;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Format;

import java.lang.reflect.Field;

class EmitGeneratorHelper {

    private static final Hints EMIT_NULL_HINT = Hints.builder()
            .with(InternalGeneratorHint.builder().emitNull(true).build())
            .build();

    private static final GeneratorResult NULL_RESULT = GeneratorResult.create(null, EMIT_NULL_HINT);

    private final ModelContext<?> modelContext;

    EmitGeneratorHelper(final ModelContext<?> modelContext) {
        this.modelContext = modelContext;
    }

    GeneratorResult getResult(final EmitGenerator<?> generator, final InternalNode node) {
        if (generator.hasMore()) {
            final Object result = generator.generate(modelContext.getRandom());

            return result == null
                    ? NULL_RESULT
                    : GeneratorResult.create(result, generator.hints());
        }

        final WhenEmptyAction whenEmptyAction = generator.getWhenEmptyAction();

        if (whenEmptyAction == WhenEmptyAction.EMIT_NULL) {
            return NULL_RESULT;
        } else if (whenEmptyAction == WhenEmptyAction.EMIT_RANDOM) {
            return GeneratorResult.emptyResult();
        }

        // The error message isn't great. Ideally it should also report
        // the selector, but it's not readily available here
        throw new InstancioApiException("No items left to emit() for " +
                getTargetDescription(node));
    }

    private String getTargetDescription(final InternalNode node) {
        final Field field = node.getField();
        if (field != null) {
            return "field " + field.getDeclaringClass().getSimpleName() + "." + field.getName();
        }
        return "class " + Format.withoutPackage(node.getTargetClass());
    }
}

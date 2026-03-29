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
package org.instancio.internal;

import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.spi.InternalServiceProvider;
import org.instancio.internal.spi.InternalServiceProvider.InternalNullSubstitutor;

import static org.instancio.internal.util.Constants.DO_NOT_MODIFY_HINT;

public class NullSubstitutorFacade {

    private final InternalNullSubstitutor nullSubstitutor;

    public NullSubstitutorFacade(final ModelContext context) {
        this.nullSubstitutor = resolveNullSubstitutor(context);
    }

    public GeneratorResult substituteNull(final InternalNode node) {
        final Object substitute = nullSubstitutor.substituteNull(node);
        return substitute == null
                ? GeneratorResult.nullResult()
                : GeneratorResult.create(substitute, DO_NOT_MODIFY_HINT);
    }

    private static InternalNullSubstitutor resolveNullSubstitutor(final ModelContext context) {
        for (InternalServiceProvider provider : context.getInternalServiceProviders()) {
            final InternalNullSubstitutor ns = provider.getNullSubstitutor();
            if (ns != null) {
                return ns;
            }
        }
        return node -> null; // no-op substitutor - always returns null
    }
}

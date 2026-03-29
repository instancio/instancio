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
package org.instancio.internal.assigners;

import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;

/**
 * Resolves an {@link Assigner} based on a {@link GeneratorResult}.
 *
 * @since 6.0.0
 */
public interface AssignerResolver {

    /**
     * Resolves an assigner for child nodes of the given {@code generatorResult}.
     *
     * @param generatorResult the parent result, e.g. a POJO, for which to resolve the assigner
     * @return assigner for the given result
     */
    Assigner resolve(GeneratorResult generatorResult);

    static AssignerResolver create(ModelContext context) {
        return new AssignerResolverImpl(context);
    }
}

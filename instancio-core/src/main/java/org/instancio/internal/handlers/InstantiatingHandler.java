/*
 *  Copyright 2022-2023 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.internal.handlers;

import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Hints;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.reflection.instantiation.Instantiator;
import org.instancio.internal.util.ReflectionUtils;
import org.jetbrains.annotations.NotNull;

public class InstantiatingHandler implements NodeHandler {

    private final Instantiator instantiator;

    public InstantiatingHandler(final Instantiator instantiator) {
        this.instantiator = instantiator;
    }

    @NotNull
    @Override
    public GeneratorResult getResult(@NotNull final Node node) {
        final Class<?> targetClass = node.getTargetClass();

        if (ReflectionUtils.isArrayOrConcrete(targetClass)) {
            final Hints hints = Hints.builder()
                    .afterGenerate(AfterGenerate.POPULATE_ALL)
                    .build();

            final Object object = instantiator.instantiate(targetClass);
            return GeneratorResult.create(object, hints);
        }
        return GeneratorResult.emptyResult();
    }


}

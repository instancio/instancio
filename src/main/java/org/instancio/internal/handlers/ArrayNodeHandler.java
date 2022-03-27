/*
 *  Copyright 2022 the original author or authors.
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

import org.instancio.Generator;
import org.instancio.internal.GeneratorMap;
import org.instancio.internal.GeneratorResult;
import org.instancio.internal.model.ArrayNode;
import org.instancio.internal.model.Node;

import java.util.Optional;

public class ArrayNodeHandler implements NodeHandler {

    private final GeneratorMap generatorMap;

    public ArrayNodeHandler(final GeneratorMap generatorMap) {
        this.generatorMap = generatorMap;
    }

    @Override
    public Optional<GeneratorResult> getResult(final Node node) {
        if (node instanceof ArrayNode) {
            final Class<?> componentType = ((ArrayNode) node).getElementNode().getKlass();
            final Generator<?> generator = generatorMap.getArrayGenerator(componentType);
            final Object arrayObject = generator.generate();
            final GeneratorResult result = GeneratorResult.create(arrayObject, generator.getHints());
            return Optional.of(result);
        }
        return Optional.empty();
    }


}

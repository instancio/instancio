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

import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.MapHint;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.reflection.instantiation.Instantiator;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MapNodeHandler implements NodeHandler {

    private final ModelContext<?> context;
    private final Instantiator instantiator;

    public MapNodeHandler(final ModelContext<?> context, final Instantiator instantiator) {
        this.context = context;
        this.instantiator = instantiator;
    }

    @NotNull
    @Override
    public GeneratorResult getResult(@NotNull final Node node) {
        if (Map.class.isAssignableFrom(node.getTargetClass())) {
            final Hints hints = Hints.builder()
                    .afterGenerate(AfterGenerate.POPULATE_ALL)
                    .with(MapHint.builder()
                            .generateEntries(randomSize())
                            .build())
                    .build();

            return GeneratorResult.create(instantiator.instantiate(node.getTargetClass()), hints);
        }
        return GeneratorResult.emptyResult();
    }

    private int randomSize() {
        final Random random = context.getRandom();
        final Settings settings = context.getSettings();
        return random.intRange(settings.get(Keys.MAP_MIN_SIZE), settings.get(Keys.MAP_MAX_SIZE));
    }
}

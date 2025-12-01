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
package org.instancio.internal.generator.util;

import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.InternalContainerHint;
import org.instancio.internal.util.Sonar;
import org.jspecify.annotations.Nullable;

import java.util.OptionalInt;

public final class OptionalIntGenerator extends AbstractGenerator<OptionalInt> {

    public OptionalIntGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public @Nullable String apiMethod() {
        return null;
    }

    @Override
    @SuppressWarnings({"OptionalAssignedToNull", Sonar.NULL_OPTIONAL})
    public @Nullable OptionalInt tryGenerateNonNull(final Random random) {
        return null; // must be null to delegate creation to the engine
    }

    @Override
    public Hints hints() {
        return Hints.builder()
                .afterGenerate(AfterGenerate.DO_NOT_MODIFY)
                .with(InternalContainerHint.builder()
                        .generateEntries(1)
                        .createFunction(args -> OptionalInt.of((int) args[0]))
                        .build())
                .build();
    }
}

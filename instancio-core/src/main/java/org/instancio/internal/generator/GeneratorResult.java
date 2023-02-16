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
package org.instancio.internal.generator;

import org.instancio.TargetSelector;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Hints;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GeneratorResult {
    private static final Hints EMPTY_HINTS = Hints.afterGenerate(AfterGenerate.DO_NOT_MODIFY);
    private static final GeneratorResult NULL_RESULT = new GeneratorResult(null, EMPTY_HINTS);
    private static final GeneratorResult EMPTY_RESULT = new GeneratorResult(null, EMPTY_HINTS);
    private static final GeneratorResult IGNORED_RESULT = new GeneratorResult(null, EMPTY_HINTS);

    private final Object value;
    private final Hints hints;

    private GeneratorResult(@Nullable final Object value, @NotNull final Hints hints) {
        this.value = value;
        this.hints = hints;
    }

    public static GeneratorResult create(@Nullable final Object value, final Hints hints) {
        return new GeneratorResult(value, hints);
    }

    public Object getValue() {
        return value;
    }

    public Hints getHints() {
        return hints;
    }

    /**
     * A null was generated, therefore the target's value should be set to null.
     * If the target is a field with a pre-initialised default value, it will be
     * overwritten with null.
     *
     * @return null result
     */
    public static GeneratorResult nullResult() {
        return NULL_RESULT;
    }

    /**
     * An empty result implies that a value could not be generated.
     * This could be due to an error.
     *
     * @return empty result
     */
    public static GeneratorResult emptyResult() {
        return EMPTY_RESULT;
    }

    /**
     * An ignored result indicates that the target was marked with
     * {@link org.instancio.InstancioApi#ignore(TargetSelector)}.
     *
     * @return ignored result
     */
    public static GeneratorResult ignoredResult() {
        return IGNORED_RESULT;
    }

    public boolean containsNull() {
        return value == null;
    }

    public boolean isEmpty() {
        return this == EMPTY_RESULT;
    }

    public boolean isIgnored() {
        return this == IGNORED_RESULT;
    }

    @Override
    public String toString() {
        return String.format("GeneratorResult[value=%s, hints=%s]", value, hints);
    }
}

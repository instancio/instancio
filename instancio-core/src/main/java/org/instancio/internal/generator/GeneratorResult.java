/*
 * Copyright 2022 the original author or authors.
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

import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Hints;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.StringJoiner;

public final class GeneratorResult {
    private static final GeneratorResult NULL_RESULT = new GeneratorResult(
            null, Hints.afterGenerate(AfterGenerate.DO_NOT_MODIFY));

    private final Object value;
    private final Hints hints;

    private GeneratorResult(@Nullable final Object value, @NotNull final Hints hints) {
        this.value = value;
        this.hints = hints;
    }

    /**
     * This result indicates that a null was generated, therefore the target's value should
     * be set to null. If the target is a field with a pre-initialised default value, it will be
     * overwritten with a null value.
     * <p>
     * An actual {@code null} {@code GeneratorResult} means that the target field will be ignored
     * (it would retain its default value, if any).
     *
     * @return null result
     */
    public static GeneratorResult nullResult() {
        return NULL_RESULT;
    }

    public static GeneratorResult create(@Nullable final Object value, final Hints hints) {
        return new GeneratorResult(value, hints);
    }

    public Object getValue() {
        return value;
    }

    public boolean isNullResult() {
        return value == null;
    }

    public Hints getHints() {
        return hints;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GeneratorResult.class.getSimpleName() + "[", "]")
                .add("value=" + value)
                .add("hints=" + hints)
                .toString();
    }
}

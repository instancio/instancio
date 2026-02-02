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
package org.instancio.internal.generator;

import org.instancio.TargetSelector;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Hints;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class GeneratorResult {
    private static final Hints EMPTY_HINTS = Hints.afterGenerate(AfterGenerate.DO_NOT_MODIFY);
    private static final GeneratorResult NULL_RESULT = new GeneratorResult(null, EMPTY_HINTS, Type.NULL);
    private static final GeneratorResult EMPTY_RESULT = new GeneratorResult(null, EMPTY_HINTS, Type.EMPTY);
    private static final GeneratorResult IGNORED_RESULT = new GeneratorResult(null, EMPTY_HINTS, Type.IGNORED);
    private static final GeneratorResult DELAYED_RESULT = new GeneratorResult(null, EMPTY_HINTS, Type.DELAYED);

    private enum Type {
        NULL, EMPTY, IGNORED, DELAYED, NORMAL
    }

    private final Object value;
    private final Hints hints;
    private final Type type;

    private GeneratorResult(@Nullable final Object value, @NotNull final Hints hints, final Type type) {
        this.value = value;
        this.hints = Objects.requireNonNull(hints, "null hints");
        this.type = type;
    }

    public static GeneratorResult create(@Nullable final Object value, final Hints hints) {
        return new GeneratorResult(value, hints, Type.NORMAL);
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

    /**
     * A delayed result indicates that a value could not be generated
     * due to an unsatisfied dependency.
     *
     * @return delayed result
     */
    public static GeneratorResult delayed() {
        return DELAYED_RESULT;
    }

    // Note: another option is to introduce a dedicated EmitGeneratorHint,
    // however for a single boolean flag it doesn't seem to be worth it
    public boolean hasEmitNullHint() {
        final InternalGeneratorHint hint = hints.get(InternalGeneratorHint.class);
        return hint != null && hint.emitNull();
    }

    public boolean containsNull() {
        return value == null;
    }

    public boolean isNormal() {
        return type == Type.NORMAL;
    }

    public boolean isNull() {
        return type == Type.NULL;
    }

    public boolean isEmpty() {
        return type == Type.EMPTY;
    }

    public boolean isIgnored() {
        return type == Type.IGNORED;
    }

    public boolean isDelayed() {
        return type == Type.DELAYED;
    }

    @Override
    public String toString() {
        if (type == Type.NORMAL) {
            return String.format("Result[%s, %s]", value, hints);
        }
        return String.format("Result[%s]", type);
    }
}

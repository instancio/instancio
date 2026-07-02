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
import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

public final class GeneratorResult {
    private static final Hints EMPTY_HINTS = Hints.afterGenerate(AfterGenerate.DO_NOT_MODIFY);
    private static final GeneratorResult NULL_RESULT = new GeneratorResult(null, EMPTY_HINTS, Type.NULL, false, false);
    private static final GeneratorResult UNRESOLVED_RESULT = new GeneratorResult(null, EMPTY_HINTS, Type.UNRESOLVED, false, false);
    private static final GeneratorResult IGNORED_RESULT = new GeneratorResult(null, EMPTY_HINTS, Type.IGNORED, false, false);
    private static final GeneratorResult DELAYED_RESULT = new GeneratorResult(null, EMPTY_HINTS, Type.DELAYED, false, false);

    private enum Type {
        NULL, UNRESOLVED, IGNORED, DELAYED, RESOLVED
    }

    private final @Nullable Object value;
    private final Hints hints;
    private final Type type;
    // TODO refactor/simplify flags
    private final boolean fromAssignment;
    private final boolean explicitNull;  // explicit null by the user

    private GeneratorResult(
            @Nullable final Object value,
            final Hints hints,
            final Type type,
            final boolean fromAssignment,
            final boolean explicitNull) {
        this.value = value;
        this.hints = requireNonNull(hints, "null hints");
        this.type = type;
        this.fromAssignment = fromAssignment;
        this.explicitNull = explicitNull;
    }

    public static GeneratorResult resolved(@Nullable final Object value, final Hints hints) {
        return new GeneratorResult(value, hints, Type.RESOLVED, false, false);
    }

    public static GeneratorResult assignedResult(@Nullable final Object value) {
        return new GeneratorResult(value, EMPTY_HINTS, Type.RESOLVED, true, false);
    }

    public boolean isFromAssignment() {
        return fromAssignment;
    }

    /**
     * Returns {@code true} if this is an {@link #explicitNull(Hints) explicit null},
     * a deterministic {@code null} from the user as opposed to a random one.
     */
    public boolean isExplicitNull() {
        return explicitNull;
    }

    @Nullable
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
     * A deterministic {@code null} that the user requested explicitly,
     * e.g. via {@code set(selector, null)} or {@code supply(selector, () -> null)},
     * as opposed to a random null produced by {@code nullable()} or {@code withNullable()}.
     *
     * @param hints the generator hints (preserved, as for a resolved result)
     * @return explicit null result
     */
    public static GeneratorResult explicitNull(final Hints hints) {
        return new GeneratorResult(null, hints, Type.RESOLVED, false, true);
    }

    /**
     * An unresolved result implies that a value could not be generated.
     * This could be due to an error.
     *
     * @return unresolved result
     */
    public static GeneratorResult unresolvedResult() {
        return UNRESOLVED_RESULT;
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
    public static GeneratorResult delayedResult() {
        return DELAYED_RESULT;
    }

    public GeneratorResult applyBuildFunctionIfPresent() {
        if (value == null) {
            return this;
        }
        final InternalContainerHint hint = hints.get(InternalContainerHint.class);
        if (hint != null) {
            final ContainerBuildFunction<Object, Object> buildFn = hint.buildFunction();
            if (buildFn != null) {
                final Object built = buildFn.build(value);
                return resolved(built, hints);
            }
        }
        return this;
    }

    // Note: another option is to introduce a dedicated EmitGeneratorHint,
    // however for a single boolean flag it doesn't seem to be worth it
    private boolean hasEmitNullHint() {
        final InternalGeneratorHint hint = hints.get(InternalGeneratorHint.class);
        return hint != null && hint.emitNull();
    }

    public boolean isIntentionalNull() {
        return value == null && (explicitNull || fromAssignment || hasEmitNullHint());
    }

    public boolean isResolved() {
        return type == Type.RESOLVED;
    }

    public boolean isNull() {
        return type == Type.NULL;
    }

    public boolean isUnresolved() {
        return type == Type.UNRESOLVED;
    }

    public boolean isIgnored() {
        return type == Type.IGNORED;
    }

    public boolean isDelayed() {
        return type == Type.DELAYED;
    }

    @Override
    public String toString() {
        if (type == Type.RESOLVED) {
            return String.format("Result[%s, %s]", value, hints);
        }
        return String.format("Result[%s]", type);
    }
}

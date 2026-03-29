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
package org.instancio.internal.nodes;

import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;

final class SubtypeResult {

    private static final SubtypeResult EMPTY_RESULT = new SubtypeResult(null, false);

    private final @Nullable Type subtype;
    private final boolean validationEnabled;

    private SubtypeResult(final @Nullable Type subtype, final boolean validationEnabled) {
        this.subtype = subtype;
        this.validationEnabled = validationEnabled;
    }

    static SubtypeResult empty() {
        return EMPTY_RESULT;
    }

    static SubtypeResult of(@Nullable final Type subtype, final boolean validationEnabled) {
        return new SubtypeResult(subtype, validationEnabled);
    }

    static SubtypeResult withValidationEnabled(final Type subtype) {
        return of(subtype, true);
    }

    static SubtypeResult withValidationDisabled(@Nullable final Type subtype) {
        return of(subtype, false);
    }

    @Nullable
    Type getSubtype() {
        return subtype;
    }

    boolean isValidationEnabled() {
        return validationEnabled;
    }

    boolean isPresent() {
        return subtype != null;
    }
}

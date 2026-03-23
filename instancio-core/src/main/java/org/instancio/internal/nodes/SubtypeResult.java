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

record SubtypeResult(@Nullable Type subtype, boolean validationEnabled) {

    private static final SubtypeResult EMPTY_RESULT = new SubtypeResult(null, false);

    static SubtypeResult empty() {
        return EMPTY_RESULT;
    }

    static SubtypeResult subtypeWithValidation(Type subtype) {
        return new SubtypeResult(subtype, true);
    }

    static SubtypeResult subtypeWithoutValidation(@Nullable Type subtype) {
        return new SubtypeResult(subtype, false);
    }

    boolean isPresent() {
        return subtype != null;
    }
}

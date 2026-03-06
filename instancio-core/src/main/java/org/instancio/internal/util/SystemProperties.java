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
package org.instancio.internal.util;

import org.instancio.documentation.InternalApi;
import org.instancio.settings.AssignmentType;
import org.jspecify.annotations.Nullable;

@InternalApi
public final class SystemProperties {

    public static final String FAIL_ON_ERROR = "instancio.failOnError";
    public static final String ASSIGNMENT_TYPE = "instancio.assignmentType";

    private SystemProperties() {
        // non-instantiable
    }

    public static boolean isFailOnErrorEnabled() {
        return isEnabled(FAIL_ON_ERROR);
    }

    private static boolean isEnabled(final String key) {
        return Boolean.TRUE.toString().equals(System.getProperty(key));
    }

    @Nullable
    public static AssignmentType getAssignmentType() {
        final String type = System.getProperty(ASSIGNMENT_TYPE);
        return type == null ? null : AssignmentType.valueOf(type);
    }
}

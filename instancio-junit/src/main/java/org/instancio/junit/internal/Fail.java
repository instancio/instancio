/*
 * Copyright 2022-2025 the original author or authors.
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

package org.instancio.junit.internal;

import org.instancio.exception.InstancioApiException;

import java.lang.reflect.Field;
import java.util.List;

import static org.instancio.internal.util.Constants.NL;

final class Fail {

    private static final int SB_SIZE = 512;

    private Fail() {
        // non-instantiable
    }

    static InstancioApiException withSettingsOnNullField() {
        //noinspection StringBufferReplaceableByString
        final String msg = new StringBuilder(SB_SIZE)
                .append("Cause:").append(NL)
                .append(" -> @WithSettings must be annotated on a non-null field.").append(NL)
                .toString();

        return apiException(msg);
    }

    static InstancioApiException withSettingsOnNullOrNonStaticField() {
        //noinspection StringBufferReplaceableByString
        final String msg = new StringBuilder(SB_SIZE)
                .append("Possible causes:").append(NL)
                .append(" -> @WithSettings must be annotated on a non-null field.").append(NL)
                .append(" -> If @WithSettings is used in a test class that contains a @ParameterizedTest,").append(NL)
                .append("    the annotated Settings field must be static.")
                .toString();

        return apiException(msg);
    }

    static InstancioApiException withSettingsOnWrongFieldType(final Field field) {
        //noinspection StringBufferReplaceableByString
        final String msg = new StringBuilder(SB_SIZE)
                .append("Cause:").append(NL)
                .append(" -> @WithSettings must be annotated on a Settings field.").append(NL)
                .append(NL)
                .append("Found annotation on:").append(NL)
                .append(" -> ").append(field)
                .toString();

        return apiException(msg);
    }

    static InstancioApiException multipleAnnotatedFields(final List<Field> fields) {
        final StringBuilder sb = new StringBuilder(SB_SIZE)
                .append("Cause:").append(NL)
                .append(" -> Found more than one field annotated '@WithSettings'").append(NL)
                .append(NL);

        for (int i = 0; i < fields.size(); i++) {
            final Field field = fields.get(i);
            sb.append("    (").append(i + 1).append(") ").append(field).append(NL);
        }

        final String msg = sb.append(NL)
                .append("Only one annotated Settings field is expected")
                .toString();

        return apiException(msg);
    }

    private static InstancioApiException apiException(final String msg) {
        return new InstancioApiException(String.format("Error running test%n%n%s%n", msg));
    }
}

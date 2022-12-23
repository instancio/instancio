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
package org.instancio.internal.assigners;

import org.instancio.assignment.AssignmentType;
import org.instancio.assignment.OnSetFieldError;
import org.instancio.assignment.OnSetMethodError;
import org.instancio.assignment.OnSetMethodNotFound;
import org.instancio.assignment.SetterStyle;
import org.instancio.internal.util.Format;
import org.instancio.internal.util.Sonar;

@SuppressWarnings(Sonar.STRING_LITERALS_DUPLICATED)
final class AssignerErrorUtil {
    private static final int INITIAL_SB_SIZE = 1024;
    private static final String NL = System.getProperty("line.separator");

    private AssignerErrorUtil() {
        // non-instantiable
    }

    static String getFieldAssignmentErrorMessage(
            final Object value,
            final String fieldName,
            final Throwable cause) {

        //noinspection StringBufferReplaceableByString
        return new StringBuilder(INITIAL_SB_SIZE)
                .append(NL)
                .append("Throwing exception because:").append(NL)
                .append(" -> Keys.ON_SET_FIELD_ERROR = ").append(OnSetFieldError.FAIL).append(NL)
                .append(NL)
                .append("Error assigning value to field:").append(NL)
                .append(" -> Field: ").append(fieldName).append(NL)
                .append(" -> Argument type:  ").append(Format.withoutPackage(value.getClass())).append(NL)
                .append(" -> Argument value: ").append(value).append(NL)
                .append(NL)
                .append("Root cause: ").append(NL)
                .append(" -> ").append(getRootCause(cause)).append(NL)
                .append(NL)
                .append("To resolve the error, consider one of the following:").append(NL)
                .append(" -> Update Keys.ON_SET_FIELD_ERROR setting to").append(NL)
                .append("    -> ").append(OnSetFieldError.IGNORE).append(" to leave value uninitialised").append(NL)
                .toString();
    }

    static String getSetterNotFoundMessage(
            final String fieldName,
            final String expectedMethodName,
            final SetterStyle setterStyle) {

        //noinspection StringBufferReplaceableByString
        return new StringBuilder(INITIAL_SB_SIZE)
                .append(NL)
                .append("Throwing exception because:").append(NL)
                .append(" -> Keys.ASSIGNMENT_TYPE = ").append(AssignmentType.METHOD).append(NL)
                .append(" -> Keys.ON_SET_METHOD_NOT_FOUND = ").append(OnSetMethodNotFound.FAIL).append(NL)
                .append(NL)
                .append("Setter method could not be resolved for field:").append(NL)
                .append(" -> ").append(fieldName).append(NL)
                .append(NL)
                .append("Using:").append(NL)
                .append(" -> Keys.SETTER_STYLE = ").append(setterStyle).append(NL)
                .append(" -> Expected method name: '").append(expectedMethodName).append('\'').append(NL)
                .append(NL)
                .append("To resolve the error, consider one of the following:").append(NL)
                .append(" -> Add the expected setter method").append(NL)
                .append(" -> Update Keys.ON_SET_METHOD_NOT_FOUND setting to").append(NL)
                .append("    -> ").append(OnSetMethodNotFound.ASSIGN_FIELD).append(" to assign value via field").append(NL)
                .append("    -> ").append(OnSetMethodNotFound.IGNORE).append(" to leave value uninitialised").append(NL)
                .toString();
    }

    static String getSetterInvocationErrorMessage(
            final Object value,
            final OnSetMethodError onSetMethodError,
            final String method,
            final Throwable cause) {

        //noinspection StringBufferReplaceableByString
        return new StringBuilder(INITIAL_SB_SIZE)
                .append(NL)
                .append("Throwing exception because:").append(NL)
                .append(" -> Keys.ASSIGNMENT_TYPE = ").append(AssignmentType.METHOD).append(NL)
                .append(" -> Keys.ON_SET_METHOD_ERROR = ").append(onSetMethodError).append(NL)
                .append(NL)
                .append("Method invocation failed:").append(NL)
                .append(" -> Method: ").append(method).append(NL)
                .append(" -> Argument type:  ").append(Format.withoutPackage(value.getClass())).append(NL)
                .append(" -> Argument value: ").append(value).append(NL)
                .append(NL)
                .append("Root cause: ").append(NL)
                .append(" -> ").append(getRootCause(cause)).append(NL)
                .append(NL)
                .append("To resolve the error, consider one of the following:").append(NL)
                .append(" -> Address the root cause that triggered the exception").append(NL)
                .append(" -> Update Keys.ON_SET_METHOD_ERROR setting to").append(NL)
                .append("    -> ").append(OnSetMethodError.ASSIGN_FIELD).append(" to assign value via field").append(NL)
                .append("    -> ").append(OnSetMethodError.IGNORE).append(" to leave value uninitialised").append(NL)
                .toString();
    }

    private static Throwable getRootCause(final Throwable t) {
        Throwable cause = t;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }
}

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
package org.instancio.internal.util;

import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetFieldError;
import org.instancio.settings.OnSetMethodError;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.SetterStyle;
import org.instancio.settings.Settings;

import java.lang.reflect.Field;

import static org.instancio.internal.util.Constants.NL;

@SuppressWarnings(Sonar.STRING_LITERALS_DUPLICATED)
public final class ErrorMessageUtils {
    private static final int INITIAL_SB_SIZE = 1024;

    private ErrorMessageUtils() {
        // non-instantiable
    }

    public static String getTypeMismatchErrorMessage(final Object value, final InternalNode node) {
        return getTypeMismatchErrorMessage(value, node, null);
    }

    public static String getTypeMismatchErrorMessage(final Object value, final InternalNode node, final Throwable cause) {
        final StringBuilder sb = new StringBuilder(INITIAL_SB_SIZE)
                .append("error assigning value to: ").append(Format.nodePathToRootBlock(node)).append(NL)
                .append(NL).append(NL);

        appendTypeMismatchDetails(value, node, sb);

        if (cause != null) {
            sb.append(NL).append(NL)
                    .append("Root cause:").append(NL)
                    .append(" -> ").append(getRootCause(cause));
        }

        return sb.toString();
    }

    public static String getContainerElementMismatchMessage(
            final String errorMsg,
            final Object value,
            final InternalNode containerNode,
            final InternalNode elementNode) {

        final StringBuilder sb = new StringBuilder(INITIAL_SB_SIZE)
                .append(errorMsg).append(": ").append(Format.nodePathToRootBlock(containerNode)).append(NL)
                .append(NL).append(NL);

        appendTypeMismatchDetails(value, elementNode, sb);
        return sb.toString();
    }

    /**
     * @see #mapCouldNotBePopulated(ModelContext, InternalNode, int)
     */
    public static String collectionCouldNotBePopulated(
            final ModelContext<?> context,
            final InternalNode node,
            final int targetSize) {

        final Settings settings = context.getSettings();

        //noinspection StringBufferReplaceableByString
        return new StringBuilder(INITIAL_SB_SIZE)
                .append("unable to populate Collection of size ").append(targetSize).append(": ")
                .append(Format.nodePathToRootBlock(node)).append(NL)
                .append(NL).append(NL)
                .append("Could not generate enough elements to populate the collection.").append(NL)
                .append("This typically occurs with Sets when the number of potential values is").append(NL)
                .append("limited and the target set cannot be generated due to duplicate element").append(NL)
                .append("values, for example:").append(NL)
                .append(NL)
                .append(" -> The element type is an enum").append(NL)
                .append(NL)
                .append(" -> The element type is a POJO, but blank POJOs are being generated").append(NL)
                .append("    because the configured maximum depth has been reached").append(NL)
                .append(NL)
                .append(" -> The element is an abstract type and no subtype was specified.").append(NL)
                .append("    See https://www.instancio.org/user-guide/#subtype-mapping for details").append(NL)
                .append(NL)
                .append("Model properties:").append(NL)
                .append(NL)
                .append(" -> Collection target size: ").append(targetSize).append(NL)
                .append(NL)
                .append("    The size was either chosen randomly based on current settings,").append(NL)
                .append("    or may have been specified explicitly via the API.").append(NL)
                .append(NL)
                .append(" -> Current size settings are").append(NL)
                .append(NL)
                .append("    Keys.COLLECTION_MIN_SIZE: ").append(settings.get(Keys.COLLECTION_MIN_SIZE)).append(NL)
                .append("    Keys.COLLECTION_MAX_SIZE: ").append(settings.get(Keys.COLLECTION_MAX_SIZE)).append(NL)
                .append(NL)
                .append(" -> Keys.MAX_DEPTH: ").append(settings.get(Keys.MAX_DEPTH)).append(NL)
                .append(NL)
                .append(" -> Model max depth: ").append(context.getMaxDepth()).append(NL)
                .append(NL)
                .append("    Unless overridden using withMaxDepth() method,").append(NL)
                .append("    this value should be the same as Keys.MAX_DEPTH").append(NL)
                .append(NL)
                .append("For more information see: https://www.instancio.org/user-guide/#error-handling")
                .toString();
    }

    /**
     * @see #collectionCouldNotBePopulated(ModelContext, InternalNode, int)
     */
    public static String mapCouldNotBePopulated(
            final ModelContext<?> context,
            final InternalNode node,
            final int targetSize) {

        final Settings settings = context.getSettings();

        //noinspection StringBufferReplaceableByString
        return new StringBuilder(INITIAL_SB_SIZE)
                .append("unable to populate Map of size ").append(targetSize).append(": ")
                .append(Format.nodePathToRootBlock(node)).append(NL)
                .append(NL).append(NL)
                .append("Could not generate enough entries to populate the map.").append(NL)
                .append("This occurs when the number of potential map keys is limited").append(NL)
                .append("and the target map cannot be generated due to duplicate keys,").append(NL)
                .append("for example:").append(NL)
                .append(NL)
                .append(" -> The key type is an enum").append(NL)
                .append(NL)
                .append(" -> The key type is a POJO, but blank POJOs are being generated").append(NL)
                .append("    because the configured maximum depth has been reached").append(NL)
                .append(NL)
                .append(" -> The key or value is an abstract type and no subtype was specified.").append(NL)
                .append("    See https://www.instancio.org/user-guide/#subtype-mapping for details").append(NL)
                .append(NL)
                .append("Model properties:").append(NL)
                .append(NL)
                .append(" -> Map target size: ").append(targetSize).append(NL)
                .append(NL)
                .append("    The size was either chosen randomly based on current settings,").append(NL)
                .append("    or may have been specified explicitly via the API.").append(NL)
                .append(NL)
                .append(" -> Current size settings are").append(NL)
                .append(NL)
                .append("    Keys.MAP_MIN_SIZE: ").append(settings.get(Keys.MAP_MIN_SIZE)).append(NL)
                .append("    Keys.MAP_MAX_SIZE: ").append(settings.get(Keys.MAP_MAX_SIZE)).append(NL)
                .append(NL)
                .append(" -> Keys.MAX_DEPTH: ").append(settings.get(Keys.MAX_DEPTH)).append(NL)
                .append(NL)
                .append(" -> Model max depth: ").append(context.getMaxDepth()).append(NL)
                .append(NL)
                .append("    Unless overridden using withMaxDepth() method,").append(NL)
                .append("    this value should be the same as Keys.MAX_DEPTH").append(NL)
                .append(NL)
                .append("For more information see: https://www.instancio.org/user-guide/#error-handling")
                .toString();
    }


    private static void appendTypeMismatchDetails(
            final Object value, final InternalNode node, final StringBuilder sb) {

        final String nodeDescription = Format.formatNode(node);
        final String argType = Format.withoutPackage(value.getClass());
        final String argValue = StringUtils.quoteToString(value);

        sb.append("Type mismatch:").append(NL).append(NL);
        if (node.getField() == null) {
            sb.append(" -> Target type ..............: ");
        } else {
            sb.append(" -> Target field .............: ");
        }

        sb.append(nodeDescription).append(NL)
                .append(" -> Provided argument type ...: ").append(argType).append(NL)
                .append(" -> Provided argument value ..: ").append(argValue);
    }

    public static String incompatibleField(
            final Object value,
            final Field field,
            final Throwable cause,
            final Settings settings) {

        final OnSetFieldError onSetFieldError = settings.get(Keys.ON_SET_FIELD_ERROR);
        final String fieldName = Format.formatField(field);
        final String argType = Format.withoutPackage(value.getClass());
        final String argValue = StringUtils.quoteToString(value);

        //noinspection StringBufferReplaceableByString
        return new StringBuilder(INITIAL_SB_SIZE)
                .append(NL)
                .append("Throwing exception because:").append(NL)
                .append(" -> Keys.ON_SET_FIELD_ERROR = ").append(onSetFieldError).append(NL)
                .append(NL)
                .append("Error assigning value to field:").append(NL)
                .append(NL)
                .append(" -> Target field: ............: ").append(fieldName).append(NL)
                .append(" -> Provided argument type ...: ").append(argType).append(NL)
                .append(" -> Provided argument value ..: ").append(argValue).append(NL)
                .append(NL)
                .append("Root cause:").append(NL)
                .append(" -> ").append(getRootCause(cause)).append(NL)
                .append(NL)
                .append("To ignore the error and leave the field uninitialised").append(NL)
                .append(" -> Update Keys.ON_SET_FIELD_ERROR setting to: ").append(OnSetFieldError.IGNORE).append(NL)
                .toString();
    }

    public static String setterNotFound(
            final Field field,
            final String expectedMethodName,
            final Settings settings) {

        final String fieldName = Format.formatField(field);
        final SetterStyle setterStyle = settings.get(Keys.SETTER_STYLE);
        final OnSetMethodNotFound onSetMethodNotFound = settings.get(Keys.ON_SET_METHOD_NOT_FOUND);

        //noinspection StringBufferReplaceableByString
        return new StringBuilder(INITIAL_SB_SIZE)
                .append(NL)
                .append("Throwing exception because:").append(NL)
                .append(" -> Keys.ASSIGNMENT_TYPE = ").append(AssignmentType.METHOD).append(NL)
                .append(" -> Keys.ON_SET_METHOD_NOT_FOUND = ").append(onSetMethodNotFound).append(NL)
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
                .append(" -> Update Keys.ON_SET_METHOD_NOT_FOUND setting to:").append(NL)
                .append("    -> ").append(OnSetMethodNotFound.ASSIGN_FIELD).append(" to assign value via field").append(NL)
                .append("    -> ").append(OnSetMethodNotFound.IGNORE).append(" to leave value uninitialised").append(NL)
                .toString();
    }

    public static String getSetterInvocationErrorMessage(
            final Object value,
            final String method,
            final Throwable cause,
            final Settings settings) {

        final OnSetMethodError onSetMethodError = settings.get(Keys.ON_SET_METHOD_ERROR);
        final String argType = Format.withoutPackage(value.getClass());
        final String argValue = StringUtils.quoteToString(value);

        //noinspection StringBufferReplaceableByString
        return new StringBuilder(INITIAL_SB_SIZE)
                .append(NL)
                .append("Throwing exception because:").append(NL)
                .append(" -> Keys.ASSIGNMENT_TYPE = ").append(AssignmentType.METHOD).append(NL)
                .append(" -> Keys.ON_SET_METHOD_ERROR = ").append(onSetMethodError).append(NL)
                .append(NL)
                .append("Method invocation failed:").append(NL)
                .append(NL)
                .append(" -> Method ...................: ").append(method).append(NL)
                .append(" -> Provided argument type ...: ").append(argType).append(NL)
                .append(" -> Provided argument value ..: ").append(argValue).append(NL)
                .append(NL)
                .append("Root cause:").append(NL)
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

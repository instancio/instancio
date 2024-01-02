/*
 * Copyright 2022-2024 the original author or authors.
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

    @SuppressWarnings("StringBufferReplaceableByString")
    public static String unableToResolveFieldFromMethodRef(final Class<?> targetClass, final String lambdaImplMethodName) {
        final String at = Format.firstNonInstancioStackTraceLine(new Throwable());
        return new StringBuilder(1024).append(NL).append(NL)
                .append("Unable to resolve the field from method reference:").append(NL)
                .append("-> ").append(Format.withoutPackage(targetClass)).append("::").append(lambdaImplMethodName).append(NL)
                .append("   at ").append(at).append(NL)
                .append(NL)
                .append("Potential causes:").append(NL)
                .append("-> The method and the corresponding field do not follow expected naming conventions").append(NL)
                .append("   See: https://www.instancio.org/user-guide/#method-reference-selector").append(NL)
                .append("-> The method is abstract, declared in a superclass, and the field is declared in a subclass").append(NL)
                .append("-> The method reference is expressed as a lambda function").append(NL)
                .append("   Example:     field((SamplePojo pojo) -> pojo.getValue())").append(NL)
                .append("   Instead of:  field(SamplePojo::getValue)").append(NL)
                .append("-> You are using Kotlin and passing a method reference of a Kotlin class").append(NL)
                .append(NL)
                .append("Possible solutions:").append(NL)
                .append("-> Resolve the above issues, if applicable").append(NL)
                .append("-> Specify the field name explicitly, e.g.").append(NL)
                .append("   field(Example.class, \"someField\")").append(NL)
                .append("-> If using Kotlin, consider creating a 'KSelect' utility class, for example:").append(NL)
                .append(NL)
                .append("   class KSelect {").append(NL)
                .append("       companion object {").append(NL)
                .append("           fun <T, V> field(property: KProperty1<T, V>): TargetSelector {").append(NL)
                .append("               val field = property.javaField!!").append(NL)
                .append("               return Select.field(field.declaringClass, field.name)").append(NL)
                .append("           }").append(NL)
                .append("       }").append(NL)
                .append("   }").append(NL)
                .append(NL)
                .append("   Usage: KSelect.field(SamplePojo::value)").append(NL)
                .toString();
    }

    public static String createSetterSelectorWithFieldAssignmentErrorMessage(final String selectorLocation) {
        //noinspection StringBufferReplaceableByString
        return new StringBuilder(512)
                .append("setter() selector cannot be used with AssignmentType.FIELD:").append(NL)
                .append(" -> ").append(selectorLocation).append(NL)
                .append(NL)
                .append("Root cause:").append(NL)
                .append(NL)
                .append("Instancio provides the 'Keys.ASSIGNMENT_TYPE' setting that").append(NL)
                .append("determines how objects are populated. The setting supports two options:").append(NL)
                .append(NL)
                .append(" -> AssignmentType.FIELD (default behaviour)").append(NL)
                .append("    Objects are populated via fields only. Setter methods are ignored.").append(NL)
                .append("    This is the recommended setting for most uses cases.").append(NL)
                .append(NL)
                .append(" -> AssignmentType.METHOD").append(NL)
                .append("    Objects are populated via methods and (optionally) fields.").append(NL)
                .append(NL)
                .append("Using setter() selectors is only supported with METHOD assignment.").append(NL)
                .append(NL)
                .append("    // Example").append(NL)
                .append("    Settings settings = Settings.create()").append(NL)
                .append("        .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD);").append(NL)
                .append(NL)
                .append("    Pojo pojo = Instancio.of(Pojo.class)").append(NL)
                .append("        .withSettings(settings)").append(NL)
                .append("        .set(setter(Pojo::setValue), \"foo\")").append(NL)
                .append("        .create();").append(NL)
                .append(NL)
                .append("See https://www.instancio.org/user-guide/#assignment-settings for details")
                .toString();
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

    public static String abstractRootWithoutSubtype(final Class<?> klass) {
        //noinspection StringBufferReplaceableByString
        return new StringBuilder(1024)
                .append("could not create an instance of ").append(klass).append(NL)
                .append(NL)
                .append("Cause:").append(NL)
                .append(NL)
                .append(" -> It is an abstract class and no subtype was provided").append(NL)
                .append(NL)
                .append("To resolve this error:").append(NL)
                .append(NL)
                .append(" -> Specify the subtype using the builder API:").append(NL)
                .append(NL)
                .append("    AbstractPojo pojo = Instancio.of(AbstractPojo.class)").append(NL)
                .append("        .subtype(all(AbstractPojo.class), ConcretePojo.class)").append(NL)
                .append("        .create();").append(NL)
                .append(NL)
                .append(" -> Or alternatively, specify the subtype using Settings:").append(NL)
                .append(NL)
                .append("    Settings settings = Settings.create()").append(NL)
                .append("        .mapType(AbstractPojo.class, ConcretePojo.class);").append(NL)
                .append(NL)
                .append("    AbstractPojo pojo = Instancio.of(AbstractPojo.class)").append(NL)
                .append("        .withSettings(settings)").append(NL)
                .append("        .create();").append(NL)
                .append(NL)
                .append("For more information see: https://www.instancio.org/user-guide/#subtype-mapping")
                .toString();
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

    public static String setterNotFound(final InternalNode node, final Settings settings) {
        final String fieldName = Format.formatField(node.getField());
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
        final String argType = value == null ? "n/a" : Format.withoutPackage(value.getClass());
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

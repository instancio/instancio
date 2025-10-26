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
package org.instancio.internal.util;

import org.instancio.feed.DataSource;
import org.instancio.feed.Feed;
import org.instancio.generator.Generator;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.feed.SpecMethod;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.FeedDataEndAction;
import org.instancio.settings.Keys;
import org.instancio.settings.OnFeedPropertyUnmatched;
import org.instancio.settings.OnSetFieldError;
import org.instancio.settings.OnSetMethodError;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.SetterStyle;
import org.instancio.settings.SettingKey;
import org.instancio.settings.Settings;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.instancio.internal.util.Constants.NL;
import static org.instancio.internal.util.Format.formatField;
import static org.instancio.internal.util.Format.formatMethodWithFence;
import static org.instancio.internal.util.Format.formatNode;
import static org.instancio.internal.util.Format.methodNameWithParams;
import static org.instancio.internal.util.Format.nodePathToRootBlock;
import static org.instancio.internal.util.Format.withoutPackage;

@SuppressWarnings({
        Sonar.STRING_LITERALS_DUPLICATED,
        "PMD.AvoidDuplicateLiterals",
        "PMD.ExcessiveImports",
        "StringBufferReplaceableByString"
})
public final class ErrorMessageUtils {
    private static final int INITIAL_SB_SIZE = 1024;

    private ErrorMessageUtils() {
        // non-instantiable
    }

    public static String maxDepthReached(final InternalNode node, final Integer maxDepth) {
        return new StringBuilder(INITIAL_SB_SIZE)
                .append("max depth reached while generating object graph").append(NL)
                .append(NL)
                .append(nodePathToRootBlock(node)).append(NL)
                .append(NL)
                .append(" -> Generation stopped at depth ").append(maxDepth).append(NL)
                .append("    (configured using the ").append(keyDesc(Keys.MAX_DEPTH)).append(" setting)").append(NL)
                .append(NL)
                .append("This error was thrown because:").append(NL)
                .append(NL)
                .append(" -> ").append(keyDesc(Keys.FAIL_ON_MAX_DEPTH_REACHED)).append(" = true").append(NL)
                .append(NL)
                .append("To resolve this error:").append(NL)
                .append(NL)
                .append(" -> Increase the value of the ").append(keyDesc(Keys.MAX_DEPTH)).append(" setting").append(NL)
                .append(" -> Use withMaxDepth() to override the depth for this model").append(NL)
                .append(" -> Set ").append(keyDesc(Keys.FAIL_ON_MAX_DEPTH_REACHED))
                .append(" to false to skip creating deeper objects")
                .toString();
    }


    public static String maxGenerationAttemptsExceeded(final InternalNode node, final int maxGenerationAttempts) {
        return new StringBuilder(INITIAL_SB_SIZE)
                .append("failed generating a value for node:").append(NL)
                .append(NL)
                .append(nodePathToRootBlock(node)).append(NL)
                .append(NL)
                .append(" -> Generation was abandoned after ").append(maxGenerationAttempts)
                .append(" attempts to avoid an infinite loop").append(NL)
                .append("    (configured using the ").append(keyDesc(Keys.MAX_GENERATION_ATTEMPTS))
                .append(" settings)").append(NL)
                .append(NL)
                .append("Possible causes:").append(NL)
                .append(NL)
                .append(" -> filter() predicate rejected too many generated values, exceeding the maximum number of attempts")
                .append(NL)
                .append(" -> withUnique() method unable to generate a sufficient number of unique values").append(NL)
                .append(" -> a hash-based collection (a Set or Map) of a given size could not be populated").append(NL)
                .append(NL)
                .append("To resolve this error:").append(NL)
                .append(NL)
                .append(" -> update the generation parameters to address the root cause").append(NL)
                .append(" -> increase the value of the ").append(keyDesc(Keys.MAX_GENERATION_ATTEMPTS)).append(" setting")
                .toString();
    }

    public static String filterPredicateErrorMessage(final Object value, final InternalNode node, final Throwable ex) {
        final String argValue = StringUtils.quoteStringValue(value);
        return new StringBuilder(INITIAL_SB_SIZE)
                .append("filter() predicate threw an exception").append(NL)
                .append(" -> Value provided to predicate: ").append(argValue).append(NL)
                .append(" -> Target node: ").append(nodePathToRootBlock(node)).append(NL)
                .append(NL)
                .append("Root cause:").append(NL)
                .append(" -> ").append(getRootCause(ex))
                .toString();
    }

    public static String unableToGetValueFromField(final Field field, final Object target) {
        return new StringBuilder(300)
                .append("unable to get value from field.").append(NL)
                .append(NL)
                .append(" -> Field........: ").append(field).append(NL)
                .append(" -> Target type..: ").append(target.getClass()).append(NL)
                .append(NL)
                .append("If you think this is a bug, please submit a bug report including the stacktrace:").append(NL)
                .append("https://github.com/instancio/instancio/issues")
                .toString();
    }

    public static String unableToResolveFieldFromMethodRef(final Class<?> targetClass, final String lambdaImplMethodName) {
        final String at = Format.firstNonInstancioStackTraceLine(new Throwable());
        return new StringBuilder(INITIAL_SB_SIZE).append(NL).append(NL)
                .append("Unable to resolve the field from method reference:").append(NL)
                .append("-> ").append(withoutPackage(targetClass)).append("::").append(lambdaImplMethodName).append(NL)
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
                .append("error assigning value to: ").append(nodePathToRootBlock(node)).append(NL)
                .append(NL).append(NL);

        appendTypeMismatchDetails(value, node, sb);

        if (cause != null) {
            sb.append(NL).append(NL)
                    .append("Root cause:").append(NL)
                    .append(" -> ").append(getRootCause(cause));
        }

        return sb.toString();
    }

    public static String fillParameterizedType(final Class<?> type) {
        return new StringBuilder(INITIAL_SB_SIZE)
                .append("cannot fill() parameterized types").append(NL)
                .append(NL)
                .append("The following methods do not support populating parameterized types, except List<E> and Map<K, V>:").append(NL)
                .append(NL)
                .append(" -> Instancio.fill(Object)").append(NL)
                .append(" -> Instancio.ofObject(Object).fill()").append(NL)
                .append(NL)
                .append("The provided argument is of type:").append(NL)
                .append(NL)
                .append(" -> ").append(Format.simpleNameWithTypeParameters(type))
                .toString();
    }

    public static String getContainerElementMismatchMessage(
            final String errorMsg,
            final Object value,
            final InternalNode containerNode,
            final InternalNode elementNode) {

        final StringBuilder sb = new StringBuilder(INITIAL_SB_SIZE)
                .append(errorMsg).append(": ").append(nodePathToRootBlock(containerNode)).append(NL)
                .append(NL).append(NL);

        appendTypeMismatchDetails(value, elementNode, sb);
        return sb.toString();
    }

    public static String abstractRootWithoutSubtype(final Class<?> klass) {
        final StringBuilder sb = new StringBuilder(INITIAL_SB_SIZE)
                .append("could not create an instance of ").append(klass).append(NL)
                .append(NL)
                .append("Cause:").append(NL)
                .append(NL);

        if (Feed.class.isAssignableFrom(klass)) {
            return sb.append(" -> The specified class is an instance of ")
                    .append(Feed.class.getName()).append(NL)
                    .append(NL)
                    .append("To resolve this error:").append(NL)
                    .append(NL)
                    .append(" -> Use the createFeed(Class) method:").append(NL)
                    .append(NL)
                    .append("    ExampleFeed feed = Instancio.createFeed(ExampleFeed.class);").append(NL)
                    .append(NL)
                    .append(" -> Or the builder API:").append(NL)
                    .append(NL)
                    .append("    ExampleFeed feed = Instancio.of(ExampleFeed.class)").append(NL)
                    .append("        // snip ...").append(NL)
                    .append("        .create();").append(NL)
                    .toString();
        }


        return sb
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
            final ModelContext context,
            final InternalNode node,
            final int targetSize) {

        final Settings settings = context.getSettings();

        return new StringBuilder(INITIAL_SB_SIZE)
                .append("unable to populate Collection of size ").append(targetSize).append(": ")
                .append(nodePathToRootBlock(node)).append(NL)
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
            final ModelContext context,
            final InternalNode node,
            final int targetSize) {

        final Settings settings = context.getSettings();

        return new StringBuilder(INITIAL_SB_SIZE)
                .append("unable to populate Map of size ").append(targetSize).append(": ")
                .append(nodePathToRootBlock(node)).append(NL)
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

        final String nodeDescription = formatNode(node);
        final String argType = withoutPackage(value.getClass());
        final String argValue = StringUtils.quoteStringValue(value);

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
        final String fieldName = formatField(field);
        final String argType = withoutPackage(value.getClass());
        final String argValue = StringUtils.quoteStringValue(value);

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
        final String fieldName = formatField(node.getField());
        final SetterStyle setterStyle = settings.get(Keys.SETTER_STYLE);
        final OnSetMethodNotFound onSetMethodNotFound = settings.get(Keys.ON_SET_METHOD_NOT_FOUND);

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
        final String argType = value == null ? "n/a" : withoutPackage(value.getClass());
        final String argValue = StringUtils.quoteStringValue(value);

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

    public static String selectorNotNullErrorMessage(
            final String message, final String methodName, final String invokedMethods, final Throwable t) {
        final String template = "%n" +
                "  %s%n" +
                "  method invocation: %s%n" +
                "  at %s";
        final String invocation = String.format("%s.%s( -> null <- )", invokedMethods, methodName);
        final String at = Format.firstNonInstancioStackTraceLine(t);
        return String.format(template, message, invocation, at);
    }

    public static String invalidAnnotationHandlerMethod(
            final Class<?> annotationProcessorClass,
            final Method method,
            final Annotation annotation,
            final Generator<?> resolvedGenerator,
            final InternalNode node) {

        final Class<?>[] paramTypes = method.getParameterTypes();
        final Class<?> specifiedGeneratorType = paramTypes.length < 2 ? null : paramTypes[1];
        final boolean isValidSpec = specifiedGeneratorType != null
                && resolvedGenerator.getClass().isAssignableFrom(specifiedGeneratorType);

        final String resolvedGeneratorName = withoutPackage(resolvedGenerator.getClass());
        final String specifiedGeneratorName = specifiedGeneratorType == null ? null : withoutPackage(specifiedGeneratorType);

        final StringBuilder sb = new StringBuilder(INITIAL_SB_SIZE);

        appendAnnotationHandlerMessageHeading(sb, annotationProcessorClass, method);

        sb.append(NL)
                .append(" -> Annotation ...............: ").append(withoutPackage(annotation.annotationType())).append(NL)
                .append(" -> Annotated type ...........: ").append(withoutPackage(node.getTargetClass())).append(NL)
                .append(" -> Resolved generator spec ..: ").append(resolvedGeneratorName).append(NL)
                .append(" -> Specified generator ......: ").append(specifiedGeneratorName);

        if (!isValidSpec) {
            sb.append(" (not valid)");
        }
        sb.append(NL)
                .append(" -> Matched node .............: ").append(nodePathToRootBlock(node)).append(NL)
                .append(NL)
                .append("To resolve this issue:").append(NL)
                .append(NL)
                .append(" -> check if the annotated type is compatible with the annotation").append(NL)
                .append(" -> update the @AnnotationHandler method signature by specifying the correct generator spec class").append(NL)
                .append(NL);

        appendAnnotationHandlerUsage(sb);

        return sb.toString();
    }

    public static String annotationHandlerInvalidNumberOfParameters(
            final Class<?> annotationProcessorClass,
            final Method method) {

        final Class<?>[] paramTypes = method.getParameterTypes();

        final StringBuilder sb = new StringBuilder(INITIAL_SB_SIZE);
        appendAnnotationHandlerMessageHeading(sb, annotationProcessorClass, method);
        sb.append(NL)
                .append("Invalid number of method parameters: ").append(paramTypes.length).append(NL)
                .append(NL);

        appendAnnotationHandlerUsage(sb);
        return sb.toString();
    }

    private static void appendAnnotationHandlerMessageHeading(
            final StringBuilder sb,
            final Class<?> annotationProcessorClass,
            final Method method) {

        sb.append("invalid @AnnotationHandler method defined by ").append(annotationProcessorClass.getName()).append(NL)
                .append(NL)
                .append("    @AnnotationHandler").append(NL)
                .append("    ").append(methodNameWithParams(method)).append(NL);
    }

    private static void appendAnnotationHandlerUsage(final StringBuilder sb) {
        sb.append("The accepted signatures for @AnnotationHandler methods are:").append(NL)
                .append(NL)
                .append(" -> void example(Annotation annotation, GeneratorSpec<?> spec)").append(NL)
                .append(" -> void example(Annotation annotation, GeneratorSpec<?> spec, Node node)").append(NL)
                .append(NL)
                .append("where:").append(NL)
                .append(NL)
                .append(" - 'annotation' and 'spec' parameters can be subtypes of Annotation and GeneratorSpec, respectively.").append(NL)
                .append(" - 'node' parameter is optional.").append(NL)
                .append(NL)
                .append("Example:").append(NL)
                .append(NL)
                .append("  @Retention(RetentionPolicy.RUNTIME)").append(NL)
                .append("  public @interface HexString {").append(NL)
                .append("      int length();").append(NL)
                .append("  }").append(NL)
                .append(NL)
                .append("  @AnnotationHandler").append(NL)
                .append("  void handleZipCode(HexString annotation, StringGeneratorSpec spec) {").append(NL)
                .append("      spec.hex().length(annotation.length());").append(NL)
                .append("  }");
    }

    public static String invalidStringTemplate(final String template, final String reason) {
        return new StringBuilder(INITIAL_SB_SIZE)
                .append("the specified template is invalid").append(NL)
                .append(NL)
                .append(" -> \"").append(template).append('"').append(NL)
                .append(NL)
                .append("Cause: ").append(reason)
                .toString();
    }

    public static String feedComponentMethodNotFound(
            final Class<?> feedClass,
            final String componentMethodName,
            final SpecMethod specMethodDeclaringTheAnnotation) {

        return new StringBuilder(INITIAL_SB_SIZE)
                .append("invalid method name '").append(componentMethodName)
                .append("' specified by the `@FunctionSpec.params` attribute in feed class:").append(NL)
                .append(NL)
                .append(" -> ").append(feedClass.getName()).append(NL)
                .append(NL)
                .append("To resolve this error:").append(NL)
                .append(NL)
                .append(" -> Check the annotation attributes of the following method:").append(NL)
                .append(NL)
                .append(formatMethodWithFence(specMethodDeclaringTheAnnotation.getMethod()))
                .toString();
    }

    public static String jacksonNotOnClasspathErrorMessage() {
        return new StringBuilder(INITIAL_SB_SIZE)
                .append("JSON feeds require 'jackson-databind' (not included with Instancio) to be on the classpath").append(NL)
                .append(NL)
                .append("To resolve this error:").append(NL)
                .append(NL)
                .append(" -> Add the following dependency:").append(NL)
                .append(NL)
                .append("    https://central.sonatype.com/artifact/com.fasterxml.jackson.core/jackson-databind")
                .toString();
    }

    public static String feedDataSourceIoErrorMessage(final DataSource dataSource, final Exception ex) {
        final StringBuilder sb = new StringBuilder(INITIAL_SB_SIZE)
                .append("failed loading feed data").append(NL);

        if (dataSource.getName() != null) {
            sb.append(NL)
                    .append(" -> Source: ").append(dataSource.getName()).append(NL);
        }
        return sb.append(NL)
                .append("Root cause:").append(NL)
                .append(NL)
                .append(" -> ").append(getRootCause(ex))
                .toString();
    }

    public static String feedWithoutDataSource(final Class<?> feedClass) {
        return new StringBuilder(INITIAL_SB_SIZE)
                .append("no data source provided for feed ").append(feedClass.getName()).append(NL)
                .append(NL)
                .append("To resolve this error, please specify a data source using any of the options below.").append(NL)
                .append(NL)
                .append(" -> Via the @Feed.Source annotation:").append(NL)
                .append(NL)
                .append("    @Feed.Source(resource = \"path/to/data/sample.csv\")").append(NL)
                .append("    interface SampleFeed extends Feed { ... }").append(NL)
                .append(NL)
                .append(" -> Using the builder API:").append(NL)
                .append(NL)
                .append("    import org.instancio.feed.DataSource;").append(NL)
                .append(NL)
                .append("    SampleFeed feed = Instancio.ofFeed(SampleFeed.class)").append(NL)
                .append("        .withDataSource(myDataSource)").append(NL)
                .append("        .create();")
                .toString();
    }

    public static String feedWithInvalidMethodName(
            final Class<?> feedClass,
            final String invalidPropertyName,
            final Collection<String> availableProperties) {

        final String properties = String.join(", ", availableProperties);

        return new StringBuilder(INITIAL_SB_SIZE)
                .append("unmatched spec method '").append(invalidPropertyName)
                .append("()' declared by the feed class").append(NL)
                .append(NL)
                .append(" -> ").append(feedClass.getName()).append(NL)
                .append(NL)
                .append("The property name '").append(invalidPropertyName)
                .append("' does not map to any property in the data:").append(NL)
                .append(NL)
                .append(" -> [").append(properties).append(']')
                .toString();
    }

    public static String feedDataEnd(
            final Class<?> feedClass,
            final Settings settings) {

        final String dataAccessKey = keyDesc(Keys.FEED_DATA_ACCESS);
        final String dataEndKey = keyDesc(Keys.FEED_DATA_END_ACTION);
        final String currentDataAccess = enumValueDesc(settings.get(Keys.FEED_DATA_ACCESS));
        final String curentDataEndStrategy = enumValueDesc(settings.get(Keys.FEED_DATA_END_ACTION));

        return new StringBuilder(INITIAL_SB_SIZE)
                .append("reached end of data for feed class").append(NL)
                .append(NL)
                .append(" -> ").append(feedClass.getName()).append(NL)
                .append(NL)
                .append("The error was triggered because of the following settings:").append(NL)
                .append(NL)
                .append(" -> ").append(dataAccessKey).append(" ......: ").append(currentDataAccess).append(NL)
                .append(" -> ").append(dataEndKey).append(" ..: ").append(curentDataEndStrategy).append(NL)
                .append(NL)
                .append("To resolve this error:").append(NL)
                .append(NL)
                .append(" -> Ensure the data source provides a sufficient number of records").append(NL)
                .append(" -> Set ").append(dataEndKey).append(" to ")
                .append(enumValueDesc(FeedDataEndAction.RECYCLE)).append(NL)
                .append(" -> Set ").append(dataAccessKey).append(" to ")
                .append(enumValueDesc(FeedDataAccess.RANDOM)).append(NL)
                .append(NL)
                .append("Note that the last two options may result in duplicate values being produced.")
                .toString();
    }

    public static String unmappedFeedProperties(
            final Collection<String> unmappedProperties,
            final Settings settings) {

        final String properties = unmappedProperties.stream()
                .sorted()
                .collect(Collectors.joining(", "));

        final String onFeedPropertyUnmatchedKey = keyDesc(Keys.ON_FEED_PROPERTY_UNMATCHED);
        final String currentOnFeedPropertyUnmatched = settings.get(Keys.ON_FEED_PROPERTY_UNMATCHED).toString();

        return new StringBuilder(INITIAL_SB_SIZE)
                .append("unmapped feed properties").append(NL)
                .append(NL)
                .append(" -> The feed specified in the applyFeed() method contains the following").append(NL)
                .append("    properties that do not map to any field in the target class:").append(NL)
                .append(NL)
                .append("    ").append(properties).append(NL)
                .append(NL)
                .append("The error was triggered because of the following setting:").append(NL)
                .append(NL)
                .append(" -> ").append(onFeedPropertyUnmatchedKey).append(": ").append(currentOnFeedPropertyUnmatched).append(NL)
                .append(NL)
                .append("To resolve this error, consider one of the following:").append(NL)
                .append(NL)
                .append(" -> Ensure that each property in the feed has a corresponding field in the target class").append(NL)
                .append(NL)
                .append(" -> Update the ").append(onFeedPropertyUnmatchedKey).append(" setting to: ").append(OnFeedPropertyUnmatched.IGNORE).append(NL)
                .append("    if the unmatched properties are intentional")
                .toString();
    }

    public static String invalidFunctionSpecHandlerMethod(
            final Class<?> feedClass,
            final Class<?> providerClass,
            final SpecMethod parentSpecMethod) {

        return new StringBuilder(INITIAL_SB_SIZE)
                .append("the following FunctionProvider implementation should contain exactly one method:").append(NL)
                .append(NL)
                .append(" -> ").append(providerClass.getName()).append(NL)
                .append(NL)
                .append("The provider is referenced by:").append(NL)
                .append(NL)
                .append(formatMethodWithFence(parentSpecMethod.getMethod())).append(NL)
                .append(NL)
                .append("declared by the feed class:").append(NL)
                .append(NL)
                .append(" -> ").append(feedClass.getName()).append(NL)
                .append(NL)
                .append("To resolve this error:").append(NL)
                .append(NL)
                .append(" -> Verify that the provider class contains exactly one method.").append(NL)
                .append(NL)
                .append(" -> Ensure the number of parameters and parameter types match the").append(NL)
                .append("    parameters declared by the '@SpecFunction.params' attribute.").append(NL)
                .toString();
    }

    public static String invalidSpecMethod(final Method method) {
        return new StringBuilder(INITIAL_SB_SIZE)
                .append("invalid spec method '").append(method.getName())
                .append("()' declared by the feed class").append(NL)
                .append(NL)
                .append(" -> ").append(method.getDeclaringClass().getName()).append(NL)
                .append(NL)
                .append(formatMethodWithFence(method)).append(NL)
                .append(NL)
                .append("To resolve this error:").append(NL)
                .append(NL)
                .append(" -> Ensure the spec method returns a FeedSpec<T>").append(NL)
                .append(NL)
                .append("    // Example").append(NL)
                .append("    FeedSpec<Integer> age();")
                .toString();
    }

    public static String errorMappingStringToTargetType(
            final String propertyKey,
            final String value,
            final Exception ex) {

        return new StringBuilder(INITIAL_SB_SIZE)
                .append("error mapping String value to target type").append(NL)
                .append(NL)
                .append(" -> Property name ..: \"").append(propertyKey).append('"').append(NL)
                .append(" -> Value ..........: \"").append(value).append('"').append(NL)
                .append(NL)
                .append("Root cause: ").append(getRootCause(ex))
                .toString();
    }

    public static String errorInvokingFunctionSpecHandlerMethod(
            final Class<?> feedClass,
            final Class<?> providerClass,
            final SpecMethod parentSpecMethod,
            final Method specFunctionHandler,
            final Exception ex) {

        final String methodName = specFunctionHandler.getName();

        return new StringBuilder(INITIAL_SB_SIZE)
                .append("exception thrown by spec method '")
                .append(parentSpecMethod.getMethod().getName())
                .append("()' declared by the feed class:").append(NL)
                .append(NL)
                .append(" -> ").append(feedClass.getName()).append(NL)
                .append(NL)
                .append("The error was caused by calling the method declared by ")
                .append(withoutPackage(providerClass)).append(':').append(NL)
                .append(NL)
                .append(formatMethodWithFence(specFunctionHandler)).append(NL)
                .append(NL)
                .append("Root cause: ").append(getRootCause(ex)).append(NL)
                .append(NL)
                .append("To resolve this error:").append(NL)
                .append(NL)
                .append(" -> Verify that the spec properties specified by the '@FunctionSpec.params' attribute").append(NL)
                .append("    match the number of parameters and parameter types defined by the '")
                .append(methodName).append("' method").append(NL)
                .append(NL)
                .append(" -> Ensure the method does not throw an exception")
                .toString();
    }

    private static String keyDesc(final SettingKey<?> key) {
        final String keyName = key.propertyKey().replace('.', '_').toUpperCase(Locale.ROOT);
        return "Keys." + keyName;
    }

    private static String enumValueDesc(final Enum<?> e) {
        return String.format("%s.%s", e.getClass().getSimpleName(), e.name());
    }

    private static Throwable getRootCause(final Throwable t) {
        Throwable cause = t;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }
}

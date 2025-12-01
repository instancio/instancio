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
import org.jspecify.annotations.Nullable;

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
        "PMD.GodClass",
        "PMD.ExcessiveImports",
        "UnnecessaryStringBuilder"
})
public final class ErrorMessageUtils {
    private static final int INITIAL_SB_SIZE = 1024;

    private ErrorMessageUtils() {
        // non-instantiable
    }

    public static String maxDepthReached(final InternalNode node, final Integer maxDepth) {
        return """
                max depth reached while generating object graph
                
                %s
                
                 -> Generation stopped at depth %d
                    (configured using the %s setting)
                
                This error was thrown because:
                
                 -> %s = true
                
                To resolve this error:
                
                 -> Increase the value of the %s setting
                 -> Use withMaxDepth() to override the depth for this model
                 -> Set %s to false to skip creating deeper objects\
                """.formatted(
                nodePathToRootBlock(node),
                maxDepth,
                keyDesc(Keys.MAX_DEPTH),
                keyDesc(Keys.FAIL_ON_MAX_DEPTH_REACHED),
                keyDesc(Keys.MAX_DEPTH),
                keyDesc(Keys.FAIL_ON_MAX_DEPTH_REACHED)
        );
    }

    public static String maxGenerationAttemptsExceeded(final InternalNode node, final int maxGenerationAttempts) {
        return """
                failed generating a value for node:
                
                %s
                
                 -> Generation was abandoned after %d attempts to avoid an infinite loop
                    (configured using the %s settings)
                
                Possible causes:
                
                 -> filter() predicate rejected too many generated values, exceeding the maximum number of attempts
                 -> withUnique() method unable to generate a sufficient number of unique values
                 -> a hash-based collection (a Set or Map) of a given size could not be populated
                
                To resolve this error:
                
                 -> update the generation parameters to address the root cause
                 -> increase the value of the %s setting\
                """.formatted(
                nodePathToRootBlock(node),
                maxGenerationAttempts,
                keyDesc(Keys.MAX_GENERATION_ATTEMPTS),
                keyDesc(Keys.MAX_GENERATION_ATTEMPTS)
        );
    }

    public static String filterPredicateErrorMessage(final Object value, final InternalNode node, final Throwable ex) {
        final String argValue = StringUtils.quoteStringValue(value);
        return """
                filter() predicate threw an exception
                 -> Value provided to predicate: %s
                 -> Target node: %s
                
                Root cause:
                 -> %s\
                """.formatted(argValue, nodePathToRootBlock(node), getRootCause(ex));
    }

    public static String unableToGetValueFromField(final Field field, final Object target) {
        return """
                unable to get value from field.
                
                 -> Field........: %s
                 -> Target type..: %s
                
                If you think this is a bug, please submit a bug report including the stacktrace:
                https://github.com/instancio/instancio/issues\
                """.formatted(field, target.getClass());
    }

    public static String unableToResolveFieldFromMethodRef(final Class<?> targetClass, final String lambdaImplMethodName) {
        final String at = Format.firstNonInstancioStackTraceLine(new Throwable());
        return """
                
                
                Unable to resolve the field from method reference:
                -> %s::%s
                   at %s
                
                Potential causes:
                -> The method and the corresponding field do not follow expected naming conventions
                   See: https://www.instancio.org/user-guide/#method-reference-selector
                -> The method is abstract, declared in a superclass, and the field is declared in a subclass
                -> The method reference is expressed as a lambda function
                   Example:     field((SamplePojo pojo) -> pojo.getValue())
                   Instead of:  field(SamplePojo::getValue)
                -> You are using Kotlin and passing a method reference of a Kotlin class
                
                Possible solutions:
                -> Resolve the above issues, if applicable
                -> Specify the field name explicitly, e.g.
                   field(Example.class, "someField")
                -> If using Kotlin, consider creating a 'KSelect' utility class, for example:
                
                   class KSelect {
                       companion object {
                           fun <T, V> field(property: KProperty1<T, V>): TargetSelector {
                               val field = property.javaField!!
                               return Select.field(field.declaringClass, field.name)
                           }
                       }
                   }
                
                   Usage: KSelect.field(SamplePojo::value)
                """.formatted(withoutPackage(targetClass), lambdaImplMethodName, at);
    }

    public static String createSetterSelectorWithFieldAssignmentErrorMessage(final String selectorLocation) {
        return """
                setter() selector cannot be used with AssignmentType.FIELD:
                 -> %s
                
                Root cause:
                
                Instancio provides the 'Keys.ASSIGNMENT_TYPE' setting that
                determines how objects are populated. The setting supports two options:
                
                 -> AssignmentType.FIELD (default behaviour)
                    Objects are populated via fields only. Setter methods are ignored.
                    This is the recommended setting for most uses cases.
                
                 -> AssignmentType.METHOD
                    Objects are populated via methods and (optionally) fields.
                
                Using setter() selectors is only supported with METHOD assignment.
                
                    // Example
                    Settings settings = Settings.create()
                        .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD);
                
                    Pojo pojo = Instancio.of(Pojo.class)
                        .withSettings(settings)
                        .set(setter(Pojo::setValue), "foo")
                        .create();
                
                See https://www.instancio.org/user-guide/#assignment-settings for details\
                """.formatted(selectorLocation);
    }

    public static String getTypeMismatchErrorMessage(final Object value, final InternalNode node) {
        return getTypeMismatchErrorMessage(value, node, null);
    }

    public static String getTypeMismatchErrorMessage(final Object value, final InternalNode node, @Nullable final Throwable cause) {
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
        return """
                cannot fill() parameterized types
                
                The following methods do not support populating parameterized types, except List<E> and Map<K, V>:
                
                 -> Instancio.fill(Object)
                 -> Instancio.ofObject(Object).fill()
                
                The provided argument is of type:
                
                 -> %s\
                """.formatted(Format.simpleNameWithTypeParameters(type));
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
            return sb.append("""
                             -> The specified class is an instance of %s
                            
                            To resolve this error:
                            
                             -> Use the createFeed(Class) method:
                            
                                ExampleFeed feed = Instancio.createFeed(ExampleFeed.class);
                            
                             -> Or the builder API:
                            
                                ExampleFeed feed = Instancio.of(ExampleFeed.class)
                                    // snip ...
                                    .create();
                            """.formatted(Feed.class.getName()))
                    .toString();
        }

        return sb.append("""
                         -> It is an abstract class and no subtype was provided
                        
                        To resolve this error:
                        
                         -> Specify the subtype using the builder API:
                        
                            AbstractPojo pojo = Instancio.of(AbstractPojo.class)
                                .subtype(all(AbstractPojo.class), ConcretePojo.class)
                                .create();
                        
                         -> Or alternatively, specify the subtype using Settings:
                        
                            Settings settings = Settings.create()
                                .mapType(AbstractPojo.class, ConcretePojo.class);
                        
                            AbstractPojo pojo = Instancio.of(AbstractPojo.class)
                                .withSettings(settings)
                                .create();
                        
                        For more information see: https://www.instancio.org/user-guide/#subtype-mapping\
                        """)
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

        return """
                unable to populate Collection of size %d: %s
                
                
                Could not generate enough elements to populate the collection.
                This typically occurs with Sets when the number of potential values is
                limited and the target set cannot be generated due to duplicate element
                values, for example:
                
                 -> The element type is an enum
                
                 -> The element type is a POJO, but blank POJOs are being generated
                    because the configured maximum depth has been reached
                
                 -> The element is an abstract type and no subtype was specified.
                    See https://www.instancio.org/user-guide/#subtype-mapping for details
                
                Model properties:
                
                 -> Collection target size: %d
                
                    The size was either chosen randomly based on current settings,
                    or may have been specified explicitly via the API.
                
                 -> Current size settings are
                
                    Keys.COLLECTION_MIN_SIZE: %d
                    Keys.COLLECTION_MAX_SIZE: %d
                
                 -> Keys.MAX_DEPTH: %d
                
                 -> Model max depth: %d
                
                    Unless overridden using withMaxDepth() method,
                    this value should be the same as Keys.MAX_DEPTH
                
                For more information see: https://www.instancio.org/user-guide/#error-handling\
                """.formatted(
                targetSize,
                nodePathToRootBlock(node),
                targetSize,
                settings.get(Keys.COLLECTION_MIN_SIZE),
                settings.get(Keys.COLLECTION_MAX_SIZE),
                settings.get(Keys.MAX_DEPTH),
                context.getMaxDepth()
        );
    }

    /**
     * @see #collectionCouldNotBePopulated(ModelContext, InternalNode, int)
     */
    public static String mapCouldNotBePopulated(
            final ModelContext context,
            final InternalNode node,
            final int targetSize) {

        final Settings settings = context.getSettings();

        return """
                unable to populate Map of size %d: %s
                
                
                Could not generate enough entries to populate the map.
                This occurs when the number of potential map keys is limited
                and the target map cannot be generated due to duplicate keys,
                for example:
                
                 -> The key type is an enum
                
                 -> The key type is a POJO, but blank POJOs are being generated
                    because the configured maximum depth has been reached
                
                 -> The key or value is an abstract type and no subtype was specified.
                    See https://www.instancio.org/user-guide/#subtype-mapping for details
                
                Model properties:
                
                 -> Map target size: %d
                
                    The size was either chosen randomly based on current settings,
                    or may have been specified explicitly via the API.
                
                 -> Current size settings are
                
                    Keys.MAP_MIN_SIZE: %d
                    Keys.MAP_MAX_SIZE: %d
                
                 -> Keys.MAX_DEPTH: %d
                
                 -> Model max depth: %d
                
                    Unless overridden using withMaxDepth() method,
                    this value should be the same as Keys.MAX_DEPTH
                
                For more information see: https://www.instancio.org/user-guide/#error-handling\
                """.formatted(
                targetSize,
                nodePathToRootBlock(node),
                targetSize,
                settings.get(Keys.MAP_MIN_SIZE),
                settings.get(Keys.MAP_MAX_SIZE),
                settings.get(Keys.MAX_DEPTH),
                context.getMaxDepth()
        );
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

        return """
                
                Throwing exception because:
                 -> Keys.ON_SET_FIELD_ERROR = %s
                
                Error assigning value to field:
                
                 -> Target field: ............: %s
                 -> Provided argument type ...: %s
                 -> Provided argument value ..: %s
                
                Root cause:
                 -> %s
                
                To ignore the error and leave the field uninitialised
                 -> Update Keys.ON_SET_FIELD_ERROR setting to: %s
                """.formatted(
                onSetFieldError,
                fieldName,
                argType,
                argValue,
                getRootCause(cause),
                OnSetFieldError.IGNORE
        );
    }

    public static String setterNotFound(final InternalNode node, final Settings settings) {
        final String fieldName = formatField(node.getField());
        final SetterStyle setterStyle = settings.get(Keys.SETTER_STYLE);
        final OnSetMethodNotFound onSetMethodNotFound = settings.get(Keys.ON_SET_METHOD_NOT_FOUND);

        return """
                
                Throwing exception because:
                 -> Keys.ASSIGNMENT_TYPE = %s
                 -> Keys.ON_SET_METHOD_NOT_FOUND = %s
                
                Setter method could not be resolved for field:
                 -> %s
                
                Using:
                 -> Keys.SETTER_STYLE = %s
                
                To resolve the error, consider one of the following:
                 -> Add the expected setter method
                 -> Update Keys.ON_SET_METHOD_NOT_FOUND setting to:
                    -> %s to assign value via field
                    -> %s to leave value uninitialised
                """.formatted(
                AssignmentType.METHOD,
                onSetMethodNotFound,
                fieldName,
                setterStyle,
                OnSetMethodNotFound.ASSIGN_FIELD,
                OnSetMethodNotFound.IGNORE
        );
    }

    public static String getSetterInvocationErrorMessage(
            final Object value,
            final String method,
            final Throwable cause,
            final Settings settings) {

        final OnSetMethodError onSetMethodError = settings.get(Keys.ON_SET_METHOD_ERROR);
        final String argType = value == null ? "n/a" : withoutPackage(value.getClass());
        final String argValue = StringUtils.quoteStringValue(value);

        return """
                
                Throwing exception because:
                 -> Keys.ASSIGNMENT_TYPE = %s
                 -> Keys.ON_SET_METHOD_ERROR = %s
                
                Method invocation failed:
                
                 -> Method ...................: %s
                 -> Provided argument type ...: %s
                 -> Provided argument value ..: %s
                
                Root cause:
                 -> %s
                
                To resolve the error, consider one of the following:
                 -> Address the root cause that triggered the exception
                 -> Update Keys.ON_SET_METHOD_ERROR setting to
                    -> %s to assign value via field
                    -> %s to leave value uninitialised
                """.formatted(
                AssignmentType.METHOD,
                onSetMethodError,
                method,
                argType,
                argValue,
                getRootCause(cause),
                OnSetMethodError.ASSIGN_FIELD,
                OnSetMethodError.IGNORE
        );
    }

    public static String selectorNotNullErrorMessage(
            final String message, final String methodName, final String invokedMethods, final Throwable t) {
        final String invocation = "%s.%s( -> null <- )".formatted(invokedMethods, methodName);
        final String at = Format.firstNonInstancioStackTraceLine(t);
        return """
                
                  %s
                  method invocation: %s
                  at %s\
                """.formatted(message, invocation, at);
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
        sb.append("""
                The accepted signatures for @AnnotationHandler methods are:
                
                 -> void example(Annotation annotation, GeneratorSpec<?> spec)
                 -> void example(Annotation annotation, GeneratorSpec<?> spec, Node node)
                
                where:
                
                 - 'annotation' and 'spec' parameters can be subtypes of Annotation and GeneratorSpec, respectively.
                 - 'node' parameter is optional.
                
                Example:
                
                  @Retention(RetentionPolicy.RUNTIME)
                  public @interface HexString {
                      int length();
                  }
                
                  @AnnotationHandler
                  void handleZipCode(HexString annotation, StringGeneratorSpec spec) {
                      spec.hex().length(annotation.length());
                  }\
                """);
    }

    public static String invalidStringTemplate(final String template, final String reason) {
        return """
                the specified template is invalid
                
                 -> "%s"
                
                Cause: %s\
                """.formatted(template, reason);
    }

    public static String feedComponentMethodNotFound(
            final Class<?> feedClass,
            final String componentMethodName,
            final SpecMethod specMethodDeclaringTheAnnotation) {

        return """
                invalid method name '%s' specified by the `@FunctionSpec.params` attribute in feed class:
                
                 -> %s
                
                To resolve this error:
                
                 -> Check the annotation attributes of the following method:
                
                %s\
                """.formatted(
                componentMethodName,
                feedClass.getName(),
                formatMethodWithFence(specMethodDeclaringTheAnnotation.getMethod())
        );
    }

    public static String jacksonNotOnClasspathErrorMessage() {
        return """
                JSON feeds require 'jackson-databind' (not included with Instancio) to be on the classpath
                
                To resolve this error:
                
                 -> Add the following dependency:
                
                    https://central.sonatype.com/artifact/tools.jackson.core/jackson-databind\
                """;
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
        return """
                no data source provided for feed %s
                
                To resolve this error, please specify a data source using any of the options below.
                
                 -> Via the @Feed.Source annotation:
                
                    @Feed.Source(resource = "path/to/data/sample.csv")
                    interface SampleFeed extends Feed { ... }
                
                 -> Using the builder API:
                
                    import org.instancio.feed.DataSource;
                
                    SampleFeed feed = Instancio.ofFeed(SampleFeed.class)
                        .withDataSource(myDataSource)
                        .create();\
                """.formatted(feedClass.getName());
    }

    public static String feedWithInvalidMethodName(
            final Class<?> feedClass,
            final String invalidPropertyName,
            final Collection<String> availableProperties) {

        final String properties = String.join(", ", availableProperties);

        return """
                unmatched spec method '%s()' declared by the feed class
                
                 -> %s
                
                The property name '%s' does not map to any property in the data:
                
                 -> [%s]\
                """.formatted(
                invalidPropertyName,
                feedClass.getName(),
                invalidPropertyName,
                properties
        );
    }

    public static String feedDataEnd(
            final Class<?> feedClass,
            final Settings settings) {

        final String dataAccessKey = keyDesc(Keys.FEED_DATA_ACCESS);
        final String dataEndKey = keyDesc(Keys.FEED_DATA_END_ACTION);
        final String currentDataAccess = enumValueDesc(settings.get(Keys.FEED_DATA_ACCESS));
        final String curentDataEndStrategy = enumValueDesc(settings.get(Keys.FEED_DATA_END_ACTION));

        return """
                reached end of data for feed class
                
                 -> %s
                
                The error was triggered because of the following settings:
                
                 -> %s ......: %s
                 -> %s ..: %s
                
                To resolve this error:
                
                 -> Ensure the data source provides a sufficient number of records
                 -> Set %s to %s
                 -> Set %s to %s
                
                Note that the last two options may result in duplicate values being produced.\
                """.formatted(
                feedClass.getName(),
                dataAccessKey,
                currentDataAccess,
                dataEndKey,
                curentDataEndStrategy,
                dataEndKey,
                enumValueDesc(FeedDataEndAction.RECYCLE),
                dataAccessKey,
                enumValueDesc(FeedDataAccess.RANDOM)
        );
    }

    public static String unmappedFeedProperties(
            final Collection<String> unmappedProperties,
            final Settings settings) {

        final String properties = unmappedProperties.stream()
                .sorted()
                .collect(Collectors.joining(", "));

        final String onFeedPropertyUnmatchedKey = keyDesc(Keys.ON_FEED_PROPERTY_UNMATCHED);
        final String currentOnFeedPropertyUnmatched = settings.get(Keys.ON_FEED_PROPERTY_UNMATCHED).toString();

        return """
                unmapped feed properties
                
                 -> The feed specified in the applyFeed() method contains the following
                    properties that do not map to any field in the target class:
                
                    %s
                
                The error was triggered because of the following setting:
                
                 -> %s: %s
                
                To resolve this error, consider one of the following:
                
                 -> Ensure that each property in the feed has a corresponding field in the target class
                
                 -> Update the %s setting to: %s
                    if the unmatched properties are intentional\
                """.formatted(
                properties,
                onFeedPropertyUnmatchedKey,
                currentOnFeedPropertyUnmatched,
                onFeedPropertyUnmatchedKey,
                OnFeedPropertyUnmatched.IGNORE
        );
    }

    public static String invalidFunctionSpecHandlerMethod(
            final Class<?> feedClass,
            final Class<?> providerClass,
            final SpecMethod parentSpecMethod) {

        return """
                the following FunctionProvider implementation should contain exactly one method:
                
                 -> %s
                
                The provider is referenced by:
                
                %s
                
                declared by the feed class:
                
                 -> %s
                
                To resolve this error:
                
                 -> Verify that the provider class contains exactly one method.
                
                 -> Ensure the number of parameters and parameter types match the
                    parameters declared by the '@SpecFunction.params' attribute.
                """.formatted(
                providerClass.getName(),
                formatMethodWithFence(parentSpecMethod.getMethod()),
                feedClass.getName()
        );
    }

    public static String invalidSpecMethod(final Method method) {
        return """
                invalid spec method '%s()' declared by the feed class
                
                 -> %s
                
                %s
                
                To resolve this error:
                
                 -> Ensure the spec method returns a FeedSpec<T>
                
                    // Example
                    FeedSpec<Integer> age();\
                """.formatted(
                method.getName(),
                method.getDeclaringClass().getName(),
                formatMethodWithFence(method)
        );
    }

    public static String errorMappingStringToTargetType(
            final String propertyKey,
            final String value,
            final Exception ex) {

        return """
                error mapping String value to target type
                
                 -> Property name ..: "%s"
                 -> Value ..........: "%s"
                
                Root cause: %s\
                """.formatted(propertyKey, value, getRootCause(ex));
    }

    public static String errorInvokingFunctionSpecHandlerMethod(
            final Class<?> feedClass,
            final Class<?> providerClass,
            final SpecMethod parentSpecMethod,
            final Method specFunctionHandler,
            final Exception ex) {

        final String methodName = specFunctionHandler.getName();

        return """
                exception thrown by spec method '%s()' declared by the feed class:
                
                 -> %s
                
                The error was caused by calling the method declared by %s:
                
                %s
                
                Root cause: %s
                
                To resolve this error:
                
                 -> Verify that the spec properties specified by the '@FunctionSpec.params' attribute
                    match the number of parameters and parameter types defined by the '%s' method
                
                 -> Ensure the method does not throw an exception\
                """.formatted(
                parentSpecMethod.getMethod().getName(),
                feedClass.getName(),
                withoutPackage(providerClass),
                formatMethodWithFence(specFunctionHandler),
                getRootCause(ex),
                methodName
        );
    }

    private static String keyDesc(final SettingKey<?> key) {
        final String keyName = key.propertyKey().replace('.', '_').toUpperCase(Locale.ROOT);
        return "Keys." + keyName;
    }

    private static String enumValueDesc(final Enum<?> e) {
        return String.format("%s.%s", e.getDeclaringClass().getSimpleName(), e.name());
    }

    private static Throwable getRootCause(final Throwable t) {
        Throwable cause = t;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }
}

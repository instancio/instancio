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
package org.instancio.internal;

import org.instancio.Node;
import org.instancio.SelectorGroup;
import org.instancio.TargetSelector;
import org.instancio.TypeTokenSupplier;
import org.instancio.generator.Generator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.selectors.PrimitiveAndWrapperSelectorImpl;
import org.instancio.internal.selectors.UnusedSelectorDescription;
import org.instancio.internal.util.ErrorMessageUtils;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.Format;
import org.instancio.internal.util.TypeUtils;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.SettingKey;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static org.instancio.internal.ApiValidatorMessageHelper.withTypeParametersNestedGenerics;
import static org.instancio.internal.ApiValidatorMessageHelper.withTypeParametersNonGenericClass;
import static org.instancio.internal.ApiValidatorMessageHelper.withTypeParametersNumberOfParameters;
import static org.instancio.internal.util.ErrorMessageUtils.createSetterSelectorWithFieldAssignmentErrorMessage;

@SuppressWarnings("PMD.GodClass")
public final class ApiValidator {

    // Note: include nested generic class in the example as it's used as a validation message for this use case
    private static final String CREATE_TYPE_TOKEN_HELP = """
            \tExample:
            \tMap<String, List<Integer>> map = Instancio.create(new TypeToken<Map<String, List<Integer>>>(){});

            \t// or the builder version
            \tMap<String, List<Integer>> map = Instancio.of(new TypeToken<Map<String, List<Integer>>>(){}).create();""";

    private static final String CREATE_CLASS_HELP = """
            \tExample:
            \tPerson person = Instancio.create(Person.class);

            \t// or the builder version
            \tPerson person = Instancio.of(Person.class).create();""";

    public static void validateRootClass(@Nullable final Type type) {
        isTrue(type != null,
                "class must not be null"
                        + "%n -> Please provide a valid class%n%n"
                        + CREATE_CLASS_HELP);
    }

    public static Type validateTypeToken(final TypeTokenSupplier<?> typeTokenSupplier) {
        notNull(typeTokenSupplier, "type token must not be null"
                + "%n -> Please provide a valid type token%n%n"
                + CREATE_TYPE_TOKEN_HELP);

        final Type type = typeTokenSupplier.get();
        notNull(type, "type token must not return a null Type"
                + "%n -> Please provide a valid Type%n%n"
                + CREATE_TYPE_TOKEN_HELP);

        return type;
    }

    public static <T> Class<T> validateOfListElementType(final Class<T> elementType) {
        return validateOfCollectionElementType(elementType, "ofList()");
    }

    public static <T> Class<T> validateOfSetElementType(final Class<T> elementType) {
        return validateOfCollectionElementType(elementType, "ofSet()");
    }

    public static <T> Class<T> validateOfCartesianProductElementType(final Class<T> elementType) {
        return validateOfCollectionElementType(elementType, "ofCartesianProduct()");
    }

    public static <T> Class<T> validateOfCollectionElementType(final Class<T> elementType, final String method) {
        isTrue(elementType != null, "%s element type must not be null", method);

        if (TypeUtils.getTypeParameterCount(elementType) > 0) {
            throw Fail.withUsageError(ApiValidatorMessageHelper.ofCollectionElementType(elementType, method));
        }
        return elementType;
    }

    public static <T> Class<T> validateOfMapKeyOrValueType(final Class<T> keyOrValueType) {
        isTrue(keyOrValueType != null, "ofMap() key/value type must not be null");

        if (TypeUtils.getTypeParameterCount(keyOrValueType) > 0) {
            throw Fail.withUsageError(ApiValidatorMessageHelper.ofMapKeyOrValueType(keyOrValueType));
        }
        return keyOrValueType;
    }

    public static void validateTypeParameters(final Type rootType, final List<Type> rootTypeParameters) {
        final Class<?> rootClass = TypeUtils.getRawType(rootType);
        final int typeVarsLength = rootClass.isArray()
                ? rootClass.getComponentType().getTypeParameters().length
                : rootClass.getTypeParameters().length;

        if (typeVarsLength == 0 && !rootTypeParameters.isEmpty()) {
            throw Fail.withUsageError(withTypeParametersNonGenericClass(rootClass));
        }

        isTrue(typeVarsLength == rootTypeParameters.size(),
                withTypeParametersNumberOfParameters(rootClass, rootTypeParameters));

        for (Type param : rootTypeParameters) {
            if (param instanceof Class<?> rawType && rawType.getTypeParameters().length > 0) {
                final String classWithTypeParams = Format.simpleNameWithTypeParameters(rawType);
                throw Fail.withUsageError(withTypeParametersNestedGenerics(classWithTypeParams));
            }
        }
    }

    public static void validateSubtype(final Class<?> from, final Class<?> to) {
        isTrue(from.isAssignableFrom(to), () -> String.format("" +
                "invalid subtype mapping" +
                "%n -> class '%s' is not a subtype of '%s'", to.getTypeName(), from.getTypeName()));
    }

    public static void validateKeyValue(final SettingKey<?> key, @Nullable final Object value) {
        notNull(key, "setting key must not be null");
        if (!key.allowsNullValue()) {
            isTrue(value != null, "setting value for key '%s' must not be null", key.propertyKey());
        }
    }

    public static <T> T[] notEmpty(@Nullable final T[] array, final String message, final Object... values) {
        isTrue(array != null && array.length > 0, message, values);
        return array;
    }

    public static <T> Collection<T> notEmpty(@Nullable final Collection<T> collection, final String message, final Object... values) {
        isTrue(collection != null && !collection.isEmpty(), message, values);
        return collection;
    }

    public static void validateGeneratorUsage(final Node node, final Generator<?> generator) {
        final AbstractGenerator<?> absGen = GeneratorSupport.unpackAbstractGenerator(generator);
        if (absGen == null) return;

        final String name = absGen.apiMethod();
        if (name != null) {
            isTrue(GeneratorSupport.supports(absGen, node.getTargetClass()),
                    () -> String.format(generateMismatchErrorMessageTemplate(node, name)));
        }
    }

    private static String generateMismatchErrorMessageTemplate(final Node node, final String apiMethodName) {
        return "the target type is incompatible with the generator"
                + "%n -> Method '" + apiMethodName + "' cannot be used for type: " + node.getTargetClass().getCanonicalName()
                + (node.getField() == null ? "" : "%n -> Field: " + node.getField());
    }

    public static void validateGenerateSecondArgument(final Object arg) {
        isFalse(arg == null, """
                the second argument of 'generate()' method must not be null
                 -> To generate a null value, use 'set(TargetSelector, null)'

                \tExample:
                \tPerson person = Instancio.of(Person.class)
                \t\t.set(field("firstName"), null)
                \t\t.create();""");
    }

    public static void validateGeneratorNotNull(@Nullable final Object obj) {
        validateSupplierOrGenerator(obj, "Generator");
    }

    public static void validateSupplierNotNull(@Nullable final Object obj) {
        validateSupplierOrGenerator(obj, "Supplier");
    }

    private static void validateSupplierOrGenerator(@Nullable final Object obj, final String supplierOrGenerator) {
        final String msg = """
                null %s passed to 'supply()' method
                 -> To generate a null value, use 'set(TargetSelector, null)'

                \tExample:
                \tPerson person = Instancio.of(Person.class)
                \t\t.set(field("firstName"), null)
                \t\t.create();""";

        isFalse(obj == null, () -> String.format(msg, supplierOrGenerator));
    }

    public static int validateSize(final int size) {
        isTrue(size >= 0, "size must not be negative: %s", size);
        return size;
    }

    public static int validateLength(final int length) {
        isTrue(length >= 0, "length must not be negative: %s", length);
        return length;
    }

    public static <T extends Comparable<T>> void validateStartEnd(final T min, final T max) {
        isTrue(min.compareTo(max) <= 0, "start must not exceed end: %s, %s", min, max);
    }

    public static <T> T notNull(@Nullable final T obj, final String message) {
        if (obj == null) throw Fail.withUsageError(message);
        return obj;
    }

    public static <T> T notNull(@Nullable final T obj, final Supplier<String> supplier) {
        if (obj == null) throw Fail.withUsageError(supplier.get());
        return obj;
    }

    public static <T> void doesNotContainNull(final T[] array, final Supplier<String> supplier) {
        for (T e : array) {
            if (e == null) throw Fail.withUsageError(supplier.get());
        }
    }

    public static void isTrue(final boolean condition, final String message, final Object... values) {
        if (!condition) throw Fail.withUsageError(message, values);
    }

    public static void isTrue(final boolean condition, final Supplier<String> message) {
        if (!condition) throw Fail.withUsageError(message.get());
    }

    public static void isFalse(final boolean condition, final String message, final Object... values) {
        if (condition) throw Fail.withUsageError(message, values);
    }

    public static void isFalse(final boolean condition, final Supplier<String> message) {
        if (condition) throw Fail.withUsageError(message.get());
    }

    public static int validateDepth(final int depth) {
        if (depth < 0) throw Fail.withUsageError("depth must not be negative: %s", depth);
        return depth;
    }

    public static void validateAssignmentOrigin(final TargetSelector selector) {
        notNull(selector, "origin selector must not be null");

        isFalse(selector instanceof SelectorGroup,
                """
                        invalid origin selector

                        Assignment origin must not match more than one target.
                        Therefore origin selector cannot be a group such as:

                        %s""", selector);

        if (selector instanceof final PrimitiveAndWrapperSelectorImpl pw) {
            throw Fail.withUsageError(
                    """
                            assignment origin must not be a primitive/wrapper selector such as %s

                            Please specify the type explicitly, for example: 'all(%s.class)' or 'all(%s.class)'""",
                    selector,
                    pw.getWrapper().getTargetClass().getSimpleName(),
                    pw.getPrimitive().getTargetClass().getSimpleName());
        }
    }

    public static void validateValueIsAssignableToTargetClass(
            final Object value,
            final Class<?> targetClass,
            final InternalNode node) {

        if (!targetClass.isAssignableFrom(value.getClass())) {
            final String errorMsg = ErrorMessageUtils.getTypeMismatchErrorMessage(value, node);
            throw Fail.withUsageError(errorMsg);
        }
    }

    public static void validateValueIsAssignableToElementNode(
            final String errorMsg,
            final Object value,
            final InternalNode containerNode,
            final InternalNode elementNode) {

        if (value == null) {
            return;
        }

        final Class<?> valueClass = value.getClass();
        final Class<?> elementType = elementNode.getTargetClass();
        final Class<?> targetClass = elementType.isPrimitive()
                ? PrimitiveWrapperBiLookup.getEquivalent(elementType)
                : elementType;

        if (targetClass.isAssignableFrom(valueClass)) {
            return;
        }

        final String error = ErrorMessageUtils.getContainerElementMismatchMessage(
                errorMsg, value, containerNode, elementNode);

        throw Fail.withUsageError(error);
    }

    public static void failIfMethodSelectorIsUsedWithFieldAssignment(
            final AssignmentType assignmentType,
            final TargetSelector setMethodSelector) {

        if (assignmentType == AssignmentType.FIELD && setMethodSelector != null) {
            UnusedSelectorDescription desc = (UnusedSelectorDescription) setMethodSelector;

            throw Fail.withUsageError(createSetterSelectorWithFieldAssignmentErrorMessage(desc.getDescription()));
        }
    }

    private ApiValidator() {
        // non-instantiable
    }
}
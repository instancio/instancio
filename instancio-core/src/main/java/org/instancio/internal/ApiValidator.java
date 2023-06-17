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
package org.instancio.internal;

import org.instancio.Model;
import org.instancio.SelectorGroup;
import org.instancio.TargetSelector;
import org.instancio.TypeTokenSupplier;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.Generator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.selectors.PrimitiveAndWrapperSelectorImpl;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.Format;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.TypeUtils;
import org.instancio.settings.SettingKey;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static org.instancio.internal.ApiValidatorMessageHelper.withTypeParametersNestedGenerics;
import static org.instancio.internal.ApiValidatorMessageHelper.withTypeParametersNonGenericClass;
import static org.instancio.internal.ApiValidatorMessageHelper.withTypeParametersNumberOfParameters;

@SuppressWarnings("PMD.GodClass")
public final class ApiValidator {

    // Note: include nested generic class in the example as it's used as a validation message for this use case
    private static final String CREATE_TYPE_TOKEN_HELP = "" +
            "%n\tExample:" +
            "%n\tMap<String, List<Integer>> map = Instancio.create(new TypeToken<Map<String, List<Integer>>>(){});" +
            "%n%n\t// or the builder version" +
            "%n\tMap<String, List<Integer>> map = Instancio.of(new TypeToken<Map<String, List<Integer>>>(){}).create();";

    private static final String CREATE_CLASS_HELP = "" +
            "%n\tExample:" +
            "%n\tPerson person = Instancio.create(Person.class);" +
            "%n%n\t// or the builder version" +
            "%n\tPerson person = Instancio.of(Person.class).create();";

    public static <T> Class<T> validateRootClass(@Nullable final Class<T> klass) {
        isTrue(klass != null,
                "class must not be null"
                        + "%n -> Please provide a valid class%n"
                        + CREATE_CLASS_HELP);

        return klass;
    }

    public static Type validateTypeToken(@Nullable final TypeTokenSupplier<?> typeTokenSupplier) {
        isTrue(typeTokenSupplier != null,
                "type token must not be null"
                        + "%n -> Please provide a valid type token%n"
                        + CREATE_TYPE_TOKEN_HELP);

        final Type type = typeTokenSupplier.get();
        isTrue(type != null,
                "type token must not return a null Type"
                        + "%n -> Please provide a valid Type%n"
                        + CREATE_TYPE_TOKEN_HELP);

        return type;
    }

    public static <T> Class<T> validateOfListElementType(final Class<T> elementType) {
        return validateOfCollectionElementType(elementType, "ofList()");
    }

    public static <T> Class<T> validateOfSetElementType(final Class<T> elementType) {
        return validateOfCollectionElementType(elementType, "ofSet()");
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

    public static void validateTypeParameters(final Class<?> rootClass, final List<Type> rootTypeParameters) {
        final int typeVarsLength = rootClass.isArray()
                ? rootClass.getComponentType().getTypeParameters().length
                : rootClass.getTypeParameters().length;

        if (typeVarsLength == 0 && !rootTypeParameters.isEmpty()) {
            throw Fail.withUsageError(withTypeParametersNonGenericClass(rootClass));
        }

        isTrue(typeVarsLength == rootTypeParameters.size(),
                withTypeParametersNumberOfParameters(rootClass, rootTypeParameters));

        for (Type param : rootTypeParameters) {
            if (param instanceof Class<?>) {
                final Class<Object> paramRawType = TypeUtils.getRawType(param);

                if (paramRawType.getTypeParameters().length > 0) {
                    final String classWithTypeParams = String.format("%s<%s>",
                            paramRawType.getSimpleName(), Format.getTypeVariablesCsv(paramRawType));

                    throw Fail.withUsageError(withTypeParametersNestedGenerics(classWithTypeParams));
                }
            }
        }
    }

    public static void validateSubtype(final Class<?> from, final Class<?> to) {
        isTrue(from.isAssignableFrom(to), () -> String.format("" +
                "invalid subtype mapping" +
                "%n -> class '%s' is not a subtype of '%s'", to.getTypeName(), from.getTypeName()));
    }

    public static void validateKeyValue(@Nullable final SettingKey<?> key, @Nullable final Object value) {
        isTrue(key != null, "setting key must not be null");
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

    public static void validateGeneratorUsage(final InternalNode node, final Generator<?> generator) {
        final AbstractGenerator<?> absGen = GeneratorSupport.unpackGenerator(generator);
        if (absGen == null) return;

        final String name = absGen.apiMethod();
        if (name != null) {
            isTrue(GeneratorSupport.supports(absGen, node.getTargetClass()),
                    () -> String.format(generateMismatchErrorMessageTemplate(node, name)));
        }
    }

    private static String generateMismatchErrorMessageTemplate(final InternalNode node, final String apiMethodName) {
        return "the target type is incompatible with the generator"
                + "%n -> Method '" + apiMethodName + "' cannot be used for type: " + node.getTargetClass().getCanonicalName()
                + (node.getField() == null ? "" : "%n -> Field: " + node.getField());
    }

    public static void validateGenerateSecondArgument(final Object arg) {
        isFalse(arg == null, () ->
                String.format("the second argument of 'generate()' method must not be null"
                        + "%n -> To generate a null value, use 'set(TargetSelector, null)'"
                        + "%n%n\tExample:"
                        + "%n\tPerson person = Instancio.of(Person.class)"
                        + "%n\t\t.set(field(\"firstName\"), null)"
                        + "%n\t\t.create();"));
    }

    public static void validateGeneratorNotNull(@Nullable final Object obj) {
        validateSupplierOrGenerator(obj, "Generator");
    }

    public static void validateSupplierNotNull(@Nullable final Object obj) {
        validateSupplierOrGenerator(obj, "Supplier");
    }

    private static void validateSupplierOrGenerator(@Nullable final Object obj, final String supplierOrGenerator) {
        isFalse(obj == null, () ->
                String.format("null %s passed to 'supply()' method"
                        + "%n -> To generate a null value, use 'set(TargetSelector, null)'"
                        + "%n%n\tExample:"
                        + "%n\tPerson person = Instancio.of(Person.class)"
                        + "%n\t\t.set(field(\"firstName\"), null)"
                        + "%n\t\t.create();", supplierOrGenerator));
    }

    public static <E> Model<E> valueSpecDoesNotSupportToModel(final String specMethodName) {
        throw new InstancioApiException(specMethodName + " spec does not support toModel()");
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
        ApiValidator.isTrue(min.compareTo(max) <= 0, "start must not exceed end: %s, %s", min, max);
    }

    public static <T> T notNull(@Nullable final T obj, final String message, final Object... values) {
        if (obj == null) throw Fail.withUsageError(String.format(message, values));
        return obj;
    }

    public static <T> T notNull(@Nullable final T obj, final Supplier<String> supplier) {
        if (obj == null) throw Fail.withUsageError(supplier.get());
        return obj;
    }

    public static void isTrue(final boolean condition, final String message, final Object... values) {
        if (!condition) throw Fail.withUsageError(String.format(message, values));
    }

    public static void isTrue(final boolean condition, final Supplier<String> message) {
        if (!condition) throw Fail.withUsageError(message.get());
    }

    public static void isFalse(final boolean condition, final String message, final Object... values) {
        if (condition) throw Fail.withUsageError(String.format(message, values));
    }

    public static void isFalse(final boolean condition, final Supplier<String> message) {
        if (condition) throw Fail.withUsageError(message.get());
    }

    public static int validateDepth(final int depth) {
        if (depth < 0) throw Fail.withUsageError("depth must not be negative: %s", depth);
        return depth;
    }

    public static void validateField(final Class<?> declaringClass, final String fieldName, final String message) {
        notNull(declaringClass, message);
        notNull(fieldName, message);
        isTrue(ReflectionUtils.isValidField(declaringClass, fieldName), message);
    }

    public static void validateAssignmentOrigin(final TargetSelector selector) {
        ApiValidator.notNull(selector, "origin selector must not be null");

        ApiValidator.isFalse(selector instanceof SelectorGroup,
                "invalid origin selector%n%n" +
                        "Assignment origin must not match more than one target." +
                        "Therefore origin selector cannot be a group such as:%n%n" +
                        "%s", selector);

        if (selector instanceof PrimitiveAndWrapperSelectorImpl) {
            final PrimitiveAndWrapperSelectorImpl pw = (PrimitiveAndWrapperSelectorImpl) selector;
            throw Fail.withUsageError(
                    "assignment origin must not be a primitive/wrapper selector such as %s%n%n" +
                            "Please specify the type explicitly, for example: 'all(%s.class)' or 'all(%s.class)'",
                    selector,
                    pw.getWrapper().getTargetClass().getSimpleName(),
                    pw.getPrimitive().getTargetClass().getSimpleName());
        }
    }

    private ApiValidator() {
        // non-instantiable
    }
}
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
package org.instancio.internal;

import org.instancio.internal.util.Format;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.instancio.internal.util.Constants.NL;

@SuppressWarnings("StringBufferReplaceableByString")
final class ApiValidatorMessageHelper {

    private static final String INCORRECT_TYPE_PARAMETERS_SPECIFIED =
            "incorrect type parameters specified";

    static String withTypeParametersNonGenericClass(final Class<?> rootClass) {
        return new StringBuilder()
                .append(INCORRECT_TYPE_PARAMETERS_SPECIFIED).append(NL)
                .append(" -> class ").append(Format.withoutPackage(rootClass))
                .append(" is not generic and does not require type parameters")
                .toString();
    }

    static String ofCollectionElementType(final Class<?> elementType, final String method) {
        final String classWithTypeParams = String.format("%s<%s>",
                elementType.getSimpleName(), Format.getTypeVariablesCsv(elementType));

        return new StringBuilder()
                .append("invalid usage of ").append(method).append(" method").append(NL)
                .append(NL)
                .append("  -> The argument ").append(classWithTypeParams)
                .append(" is a generic class and also requires type parameter(s),").append(NL)
                .append("     but this method does not support nested generics.").append(NL)
                .append(NL)
                .append("To resolve this error:").append(NL)
                .append(NL)
                .append(" -> Use one of the methods that accepts a type token, e.g:").append(NL)
                .append(NL)
                .append("    List<Pair<String, Integer>> list = Instancio.ofList(new TypeToken<Pair<String, Integer>>() {})").append(NL)
                .append("            // additional API methods...").append(NL)
                .append("            .create();")
                .append(NL)
                .append("    or:").append(NL)
                .append(NL)
                .append("    List<Pair<String, Integer>> list = Instancio.of(new TypeToken<List<Pair<String, Integer>>>(){})").append(NL)
                .append("            // additional API methods...").append(NL)
                .append("            .create();")
                .toString();
    }

    static String ofMapKeyOrValueType(final Class<?> keyOrValue) {
        final String classWithTypeParams = String.format("%s<%s>",
                keyOrValue.getSimpleName(), Format.getTypeVariablesCsv(keyOrValue));

        return new StringBuilder()
                .append("invalid usage of ofMap() method").append(NL)
                .append(NL)
                .append("  -> The argument ").append(classWithTypeParams)
                .append(" is a generic class and also requires type parameter(s),").append(NL)
                .append("     but this method does not support nested generics.").append(NL)
                .append(NL)
                .append("To resolve this error:").append(NL)
                .append(NL)
                .append(" -> Use one of the methods that accepts a type token, e.g:").append(NL)
                .append(NL)
                .append("    Map<Item<String>, Pair<String, Integer>> map = Instancio.ofMap(").append(NL)
                .append("                    new TypeToken<Item<String>>() {},").append(NL)
                .append("                    new TypeToken<Pair<String, Integer>>() {})").append(NL)
                .append("            // additional API methods...").append(NL)
                .append("            .create();")
                .append(NL)
                .append("    or:").append(NL)
                .append(NL)
                .append("    Map<Item<String>, Pair<String, Integer>> map = Instancio.of(new TypeToken<Map<Item<String>, Pair<String, Integer>>>(){})").append(NL)
                .append("            // additional API methods...").append(NL)
                .append("            .create();")
                .toString();
    }

    static String withTypeParametersNestedGenerics(final String classWithTypeParams) {
        return new StringBuilder()
                .append(INCORRECT_TYPE_PARAMETERS_SPECIFIED).append(NL)
                .append(NL)
                .append("  -> The argument ").append(classWithTypeParams)
                .append(" is a generic class and also requires type parameter(s),").append(NL)
                .append("     but this method does not support nested generics.").append(NL)
                .append(NL)
                .append("To resolve this error:").append(NL)
                .append(NL)
                .append(" -> Use a type token, e.g:").append(NL)
                .append(NL)
                .append("    Map<String, List<Integer>> map = Instancio.create(new TypeToken<Map<String, List<Integer>>>(){});").append(NL)
                .append(NL)
                .append("    or the builder version:").append(NL)
                .append(NL)
                .append("    Map<String, List<Integer>> map = Instancio.of(new TypeToken<Map<String, List<Integer>>>(){})").append(NL)
                .append("            // additional API methods...").append(NL)
                .append("            .create();")
                .toString();
    }

    static String withTypeParametersNumberOfParameters(
            final Class<?> rootClass,
            final List<Type> rootTypeParameters) {

        final StringBuilder sb = new StringBuilder(1024)
                .append(INCORRECT_TYPE_PARAMETERS_SPECIFIED).append(NL)
                .append(NL)
                .append(" -> Class ").append(rootClass.getName()).append(" requires ")
                .append(rootClass.getTypeParameters().length).append(" type parameter(s): ")
                .append(Arrays.toString(rootClass.getTypeParameters())).append(NL)
                .append(" -> The number of parameters provided was ").append(rootTypeParameters.size());

        if (!rootTypeParameters.isEmpty()) {
            sb.append(": [").append(
                            rootTypeParameters.stream()
                                    .map(Format::withoutPackage)
                                    .collect(joining(", ")))
                    .append(']');
        }

        sb.append(NL).append(NL)
                .append("To resolve this error:").append(NL)
                .append(NL)
                .append(" -> Specify the correct number of parameters, e.g.").append(NL)
                .append(NL)
                .append("    Instancio.of(Map.class).").append(NL)
                .append("        .withTypeParameters(UUID.class, Person.class)").append(NL)
                .append("        .create();").append(NL)
                .append(NL)
                .append(" -> Or use a type token:").append(NL)
                .append(NL)
                .append("    Instancio.create(new TypeToken<Map<UUID, Person>>() {});");
        return sb.toString();
    }

    private ApiValidatorMessageHelper() {
        // non-instantiable
    }
}

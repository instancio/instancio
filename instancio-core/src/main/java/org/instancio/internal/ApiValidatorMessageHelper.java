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

import org.instancio.internal.util.Format;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.instancio.internal.util.Constants.NL;

@SuppressWarnings("StringBufferReplaceableByString")
final class ApiValidatorMessageHelper {

    private static final String INVALID_USAGE_OF_WITH_TYPE_PARAMETERS =
            "Invalid usage of withTypeParameters() method:";

    static String withTypeParametersNonGenericClass(final Class<?> rootClass) {
        return new StringBuilder().append(NL)
                .append(INVALID_USAGE_OF_WITH_TYPE_PARAMETERS).append(NL)
                .append(NL)
                .append(" -> Class ").append(Format.withoutPackage(rootClass))
                .append(" is not generic and does not require type parameters").append(NL)
                .toString();
    }

    static String withTypeParametersNestedGenerics(final String classWithTypeParams) {
        return new StringBuilder().append(NL)
                .append(INVALID_USAGE_OF_WITH_TYPE_PARAMETERS).append(NL)
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
                .append("    Map<String, List<Integer>> map = Instancio.of(new TypeToken<Map<String, List<Integer>>>(){}).create();")
                .toString();
    }

    static String withTypeParametersNumberOfParameters(
            final Class<?> rootClass,
            final List<Class<?>> rootTypeParameters) {

        final StringBuilder sb = new StringBuilder().append(NL)
                .append(INVALID_USAGE_OF_WITH_TYPE_PARAMETERS).append(NL)
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
                .append("    Instancio.create(new TypeToken<Map<UUID, Person>>() {});").append(NL)
                .append(NL);
        return sb.toString();
    }

    private ApiValidatorMessageHelper() {
        // non-instantiable
    }
}

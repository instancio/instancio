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
package org.instancio.internal;

import org.instancio.generator.Generator;
import org.instancio.generator.specs.ArrayGeneratorSpec;
import org.instancio.generator.specs.CollectionGeneratorSpec;
import org.instancio.generator.specs.EnumGeneratorSpec;
import org.instancio.generator.specs.MapGeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.internal.util.TypeUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

final class GeneratorSupport {

    private GeneratorSupport() {
        // non-instantiable
    }

    /**
     * Checks whether this generator can generate given type.
     *
     * @param type to check
     * @return {@code true} if generator can
     */
    static boolean supports(Generator<?> generator, Class<?> type) {
        if (type.isArray()) {
            return generator instanceof ArrayGeneratorSpec;
        }
        if (Collection.class.isAssignableFrom(type)) {
            return generator instanceof CollectionGeneratorSpec;
        }
        if (Map.class.isAssignableFrom(type)) {
            return generator instanceof MapGeneratorSpec;
        }
        if (type.isEnum()) {
            return generator instanceof EnumGeneratorSpec;
        }
        final Class<?> typeArg = TypeUtils.getGenericSuperclassTypeArgument(generator.getClass());
        if (typeArg != null) {
            return typeArg.isAssignableFrom(type)
                    || PrimitiveWrapperBiLookup.findEquivalent(typeArg)
                    .filter(type::isAssignableFrom)
                    .isPresent();
        }
        // couldn't determine type arg ('this' is probably a lambda)
        return false;
    }

    /**
     * Returns the public API method name of the generator.
     * Will return an empty result if the class represents a lambda.
     *
     * @return spec name, if defined
     */
    static Optional<String> apiMethodName(final Class<?> generatorClass) {
        return Optional.ofNullable(Generators.getApiMethod(generatorClass));
    }
}

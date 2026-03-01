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

import org.instancio.generator.Generator;
import org.instancio.generator.specs.ArrayGeneratorSpec;
import org.instancio.generator.specs.CollectionGeneratorSpec;
import org.instancio.generator.specs.EnumGeneratorSpec;
import org.instancio.generator.specs.EnumSetGeneratorSpec;
import org.instancio.generator.specs.MapGeneratorSpec;
import org.instancio.generator.specs.OneOfArrayGeneratorSpec;
import org.instancio.generator.specs.OneOfCollectionGeneratorSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.util.Sonar;
import org.instancio.internal.util.TypeUtils;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

final class GeneratorSupport {

    private GeneratorSupport() {
        // non-instantiable
    }

    /**
     * Checks whether this generator can generate given type.
     *
     * @param type to check
     * @return {@code true} if given generator supports the specified type
     */
    static boolean supports(Generator<?> generator, Class<?> type) {
        if (type == Object.class) {
            return true;
        } else if (generator instanceof OneOfArrayGeneratorSpec<?> || generator instanceof OneOfCollectionGeneratorSpec<?>) {
            // Cannot validate because any types can be passed in to these generators.
            // If invalid type is passed, it will be reported as an assignment error.
            return true;
        } else if (generator instanceof ArrayGeneratorSpec) {
            return type.isArray();
        } else if (generator instanceof EnumSetGeneratorSpec) {
            return EnumSet.class.isAssignableFrom(type);
        } else if (generator instanceof CollectionGeneratorSpec) {
            return (Collection.class.isAssignableFrom(type) || type == Iterable.class)
                   && type != EnumSet.class;
        } else if (generator instanceof MapGeneratorSpec) {
            return Map.class.isAssignableFrom(type);
        } else if (generator instanceof EnumGeneratorSpec) {
            return type.isEnum();
        }

        final Class<?> typeArg = TypeUtils.getGenericSuperclassTypeArgument(generator.getClass());
        if (typeArg != null) {
            if (type.isAssignableFrom(typeArg)) {
                return true;
            }
            final Class<?> equivalent = PrimitiveWrapperBiLookup.getEquivalent(typeArg);
            return equivalent != null && type.isAssignableFrom(equivalent);
        }
        // couldn't determine type arg ('this' is probably a lambda)
        return false;
    }

    @Nullable
    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    static AbstractGenerator<?> unpackAbstractGenerator(@Nullable final Generator<?> generator) {
        if (generator instanceof AbstractGenerator<?> g) {
            return g;
        }
        if (generator instanceof GeneratorDecorator<?> decorator) {
            final Generator<?> delegate = decorator.getDelegate();
            if (delegate instanceof AbstractGenerator<?> g) {
                return g;
            }
        }
        return null;
    }
}

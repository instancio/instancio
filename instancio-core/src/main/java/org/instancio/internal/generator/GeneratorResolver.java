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
package org.instancio.internal.generator;

import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.SubtypeGeneratorSpec;
import org.instancio.internal.generator.array.ArrayGenerator;
import org.instancio.internal.generator.lang.EnumGenerator;
import org.instancio.internal.generator.util.CollectionGenerator;
import org.instancio.internal.generator.util.MapGenerator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeKind;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.Sonar;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static org.instancio.internal.generator.GeneratorUtil.instantiateInternalGenerator;

@SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
public class GeneratorResolver {

    private final GeneratorContext context;
    private final Map<Class<?>, Generator<?>> cache = new HashMap<>();

    public GeneratorResolver(final GeneratorContext context) {
        this.context = context;
    }

    /**
     * Returns a generator for the given {@code node}.
     * This method returns a new generator instance on each call.
     *
     * @see #getCached(InternalNode)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Generator<?> get(final InternalNode node) {
        final Class<?> klass = node.getTargetClass();

        Generator<?> generator = getBuiltInGenerator(klass);

        if (generator == null) {
            if (klass.isArray()) {
                generator = new ArrayGenerator<>(context, klass);
            } else if (klass.isEnum()) {
                generator = new EnumGenerator(context, klass);
            } else if (node.is(NodeKind.MAP)) {
                generator = new MapGenerator<>(context).subtype(node.getTargetClass());
            } else if (node.is(NodeKind.COLLECTION)) {
                generator = new CollectionGenerator<>(context).subtype(node.getTargetClass());
            }
        }
        return generator;
    }

    /**
     * Since this method returns a cached generator,
     * callers must not update the generator's state.
     *
     * @see #get(InternalNode)
     */
    @SuppressWarnings(Sonar.MAP_COMPUTE_IF_ABSENT)
    public Generator<?> getCached(final InternalNode node) {
        final Class<?> targetClass = node.getTargetClass();

        Generator<?> generator = cache.get(targetClass);

        if (generator == null) {
            generator = get(node);
            cache.put(targetClass, generator);
        }

        return generator;
    }

    /**
     * Java modules will not have these legacy classes unless explicitly
     * imported via e.g. "requires java.sql". In case the classes are not
     * available, load them by name to avoid class not found error
     */
    @Nullable
    @SuppressWarnings(Sonar.USE_INSTANCEOF)
    private Generator<?> getGeneratorForLegacyClass(final Class<?> klass) {
        final String className = klass.getName();

        if ("java.sql.Date".equals(className)) {
            return loadByClassName(context, "org.instancio.internal.generator.sql.SqlDateGenerator");
        }
        if ("java.sql.Timestamp".equals(className)) {
            return loadByClassName(context, "org.instancio.internal.generator.sql.TimestampGenerator");
        }
        if ("javax.xml.datatype.XMLGregorianCalendar".equals(className)) {
            return loadByClassName(context, "org.instancio.internal.generator.xml.XMLGregorianCalendarGenerator");
        }
        if ("javax.xml.namespace.QName".equals(className)) {
            return loadByClassName(context, "org.instancio.internal.generator.xml.QNameGenerator");
        }
        return null;
    }

    private static Generator<?> loadByClassName(
            final GeneratorContext context,
            final String generatorClassName) {

        final Class<?> generatorClass = ReflectionUtils.loadClass(generatorClassName);
        return instantiateInternalGenerator(generatorClass, context);
    }

    /**
     * Since this method returns a cached generator,
     * callers must not update the generator's state.
     */
    @SuppressWarnings(Sonar.MAP_COMPUTE_IF_ABSENT)
    public Generator<?> getCachedBuiltInGenerator(final Class<?> targetClass) {
        Generator<?> generator = cache.get(targetClass);

        if (generator == null) {
            generator = getBuiltInGenerator(targetClass);
            cache.put(targetClass, generator);
        }

        return generator;
    }

    private Generator<?> getBuiltInGenerator(final Class<?> targetClass) {
        final Class<?> genClass = GeneratorResolverMaps.getGenerator(targetClass);
        if (genClass == null) {
            return getGeneratorForLegacyClass(targetClass);
        }

        final Generator<?> generator = instantiateInternalGenerator(genClass, context);
        if (generator instanceof SubtypeGeneratorSpec<?> subtypeSpec) {
            final Class<?> subtype = GeneratorResolverMaps.getSubtype(targetClass);
            if (subtype != null) {
                subtypeSpec.subtype(subtype);
            }
        }
        return generator;
    }
}

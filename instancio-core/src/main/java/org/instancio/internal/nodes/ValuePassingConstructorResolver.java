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
package org.instancio.internal.nodes;

import org.instancio.internal.PrimitiveWrapperBiLookup;
import org.instancio.internal.util.ConstructorParameterNames;
import org.instancio.internal.util.ReflectionUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Resolves a constructor that is invoked with generated values as arguments,
 * by matching its parameters to the fields of the target class.
 *
 * @see org.instancio.settings.InstantiationStrategy#ALL_ARGS
 */
final class ValuePassingConstructorResolver {

    private static final Logger LOG = LoggerFactory.getLogger(ValuePassingConstructorResolver.class);

    private ValuePassingConstructorResolver() {
        // non-instantiable
    }

    /**
     * Resolves a constructor whose arguments are generated values. Every
     * parameter must be matched to a field: a constructor with an unmatched
     * parameter is not used, since the value of such a parameter would be
     * generated from the parameter type alone and could not be reached by a
     * selector. A candidate whose parameter names cannot be resolved is
     * likewise skipped, since its parameters cannot be matched at all.
     */
    @Nullable
    static ConstructorDescriptor resolve(final InternalNode node, final List<InternalNode> children) {
        // Without fields there is nothing to match parameters against
        if (children.isEmpty()) {
            return null;
        }

        final Class<?> targetClass = node.getTargetClass();

        for (Constructor<?> candidate : getCandidateConstructors(targetClass)) {
            final List<InternalNode> parameterNodes = mapParametersToFields(candidate, children);

            if (parameterNodes != null) {
                LOG.trace("Resolved constructor for {}: {}", node, candidate);
                return new ConstructorDescriptor(candidate, parameterNodes, children);
            }
        }

        LOG.trace("No constructor with parameters matching the fields of {}", targetClass);
        return null;
    }

    /**
     * Returns constructors that could be used to instantiate the class,
     * most parameters first (ties broken deterministically).
     */
    private static List<Constructor<?>> getCandidateConstructors(final Class<?> targetClass) {
        record Candidate(Constructor<?> constructor, String parameterTypeNames) {}

        final Constructor<?>[] declared = targetClass.getDeclaredConstructors();
        final List<Candidate> candidates = new ArrayList<>(declared.length);

        for (Constructor<?> constructor : declared) {
            if (!constructor.isSynthetic() && constructor.getParameterCount() > 0) {
                final Class<?>[] parameterTypes = constructor.getParameterTypes();

                // NOTE: an exact type check, since a single parameter that is
                // a supertype of the class is common with erased generics,
                // e.g. Item<K> { K value; Item(K value) {...} }
                final boolean isCopyConstructor = parameterTypes.length == 1
                        && parameterTypes[0] == targetClass;

                if (!ReflectionUtils.isBuilderConstructor(constructor)
                        && !isCopyConstructor
                        && constructor.trySetAccessible()) {

                    candidates.add(new Candidate(constructor, Arrays.toString(parameterTypes)));
                }
            }
        }

        candidates.sort(Comparator
                .comparingInt((Candidate c) -> -c.constructor().getParameterCount())
                .thenComparingInt(c -> Modifier.isPublic(c.constructor().getModifiers()) ? 0 : 1)
                .thenComparing(Candidate::parameterTypeNames));

        final List<Constructor<?>> results = new ArrayList<>(candidates.size());
        for (Candidate candidate : candidates) {
            results.add(candidate.constructor());
        }
        return results;
    }

    /**
     * Maps each constructor parameter to a field-backed child node
     * by name and compatible type.
     *
     * @return a node per parameter, in declaration order, or {@code null} if
     * the parameter names could not be resolved or any parameter matched no
     * field, in which case the constructor cannot be used
     */
    @Nullable
    @SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
    private static List<InternalNode> mapParametersToFields(
            final Constructor<?> constructor,
            final List<InternalNode> children) {

        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final String[] parameterNames = ConstructorParameterNames.resolve(constructor);

        if (parameterNames == null) {
            LOG.debug("Unable to resolve parameter names of {}; the class will be instantiated"
                    + " without a constructor. Compiling it with debug information ('-g')"
                    + " or with '-parameters' would allow the constructor to be used.", constructor);
            return null;
        }

        final List<InternalNode> parameterNodes = new ArrayList<>(parameterTypes.length);

        // Parameter names are unique within a constructor,
        // so no child can be claimed by two parameters
        for (int i = 0; i < parameterTypes.length; i++) {
            final InternalNode match = findByName(parameterNames[i], parameterTypes[i], children);

            if (match == null) {
                return null;
            }
            parameterNodes.add(match);
        }
        return parameterNodes;
    }

    @Nullable
    private static InternalNode findByName(
            final String parameterName,
            final Class<?> parameterType,
            final List<InternalNode> children) {

        for (InternalNode child : children) {
            final Field field = child.getField();

            if (field != null
                    && field.getName().equals(parameterName)
                    && PrimitiveWrapperBiLookup.isAssignableConsideringBoxing(
                    parameterType, field.getType())) {
                return child;
            }
        }
        return null;
    }

}

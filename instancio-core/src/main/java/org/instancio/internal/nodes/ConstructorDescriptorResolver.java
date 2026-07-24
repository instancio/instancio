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

import org.instancio.internal.context.ModelContext;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.RecordUtils;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.settings.InternalInstantiationStrategies;
import org.instancio.settings.InstantiationStrategy;
import org.instancio.settings.Keys;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;

final class ConstructorDescriptorResolver {

    private final InternalInstantiationStrategies instantiationStrategies;

    ConstructorDescriptorResolver(final ModelContext modelContext) {
        this.instantiationStrategies = (InternalInstantiationStrategies)
                modelContext.getSettings().get(Keys.INSTANTIATION_STRATEGIES);
    }

    /**
     * Resolves a constructor for the given node, or returns {@code null}
     * if the node's value should be created without invoking a constructor.
     *
     * <p>Every parameter node is one of the node's existing {@code children}
     * instances, since a constructor is only used if all of its parameters
     * map to a field.
     *
     * @param node     node to resolve a constructor for
     * @param children the node's children created from class members
     *                 (the node's children may not have been set yet)
     * @return constructor descriptor, or {@code null} if not applicable
     */
    @Nullable
    ConstructorDescriptor resolve(final InternalNode node, final List<InternalNode> children) {
        return switch (node.getNodeKind()) {
            case RECORD -> resolveForRecord(node, children);
            case POJO -> resolveForPojo(node, children);
            default -> throw Fail.withInternalError("Unexpected NodeKind: {}", node.getNodeKind());
        };
    }

    @Nullable
    private static ConstructorDescriptor resolveForRecord(final InternalNode node, final List<InternalNode> children) {
        final Class<?> targetClass = node.getTargetClass();

        // A record node may have no children if the maximum depth has been reached
        if (children.size() != targetClass.getRecordComponents().length) {
            // this should result in a blank record with default values for each parameter
            return null;
        }

        final Constructor<?> constructor = RecordUtils.getCanonicalConstructor(targetClass);
        return new ConstructorDescriptor(constructor, children, List.of());
    }

    @Nullable
    private ConstructorDescriptor resolveForPojo(final InternalNode node, final List<InternalNode> children) {
        if (!isEligible(node.getTargetClass())) {
            return null;
        }

        for (InstantiationStrategy strategy : instantiationStrategies.getStrategies()) {
            final ConstructorDescriptor descriptor = switch (strategy) {
                case NO_ARGS -> resolveNoArgsConstructor(node.getTargetClass(), children);
                case ALL_ARGS -> ValuePassingConstructorResolver.resolve(node, children);
                // Allocating an instance without a constructor is done by the
                // Instantiator, so there is no descriptor to resolve here
                case BYPASS_CONSTRUCTOR -> null;
            };

            // BYPASS_CONSTRUCTOR always succeeds, therefore
            // no strategy listed after it can be reached
            if (descriptor != null || strategy == InstantiationStrategy.BYPASS_CONSTRUCTOR) {
                return descriptor;
            }
        }
        return null;
    }

    @Nullable
    private static ConstructorDescriptor resolveNoArgsConstructor(
            final Class<?> targetClass,
            final List<InternalNode> children) {

        for (Constructor<?> constructor : targetClass.getDeclaredConstructors()) {
            // Visibility is not considered: the default constructor of a private nested
            // class is itself private, and skipping it would prevent field initializers
            // from running. A constructor that rejects instantiation by throwing is
            // handled by the ON_CONSTRUCTOR_ERROR setting.
            if (constructor.getParameterCount() == 0 && constructor.trySetAccessible()) {
                return new ConstructorDescriptor(constructor, List.of(), children);
            }
        }
        return null;
    }

    private static boolean isEligible(final Class<?> targetClass) {
        // Constructors of non-static member classes and local/anonymous
        // classes have implicit parameters (e.g. the enclosing instance)
        return !ReflectionUtils.isInterfaceOrAbstract(targetClass)
                && !targetClass.isAnonymousClass()
                && !targetClass.isLocalClass()
                && !(targetClass.isMemberClass() && !Modifier.isStatic(targetClass.getModifiers()));
    }

}

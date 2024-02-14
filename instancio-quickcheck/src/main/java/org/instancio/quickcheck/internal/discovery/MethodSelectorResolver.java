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
package org.instancio.quickcheck.internal.discovery;

import org.instancio.quickcheck.internal.descriptor.InstancioClassBasedTestDescriptor;
import org.instancio.quickcheck.internal.descriptor.InstancioQuickcheckTestMethodTestDescriptor;
import org.instancio.quickcheck.internal.discovery.predicates.IsNestedTestClass;
import org.instancio.quickcheck.internal.discovery.predicates.IsPropertyMethod;
import org.instancio.quickcheck.internal.discovery.predicates.IsTestClassWithProperties;
import org.instancio.quickcheck.internal.util.ClassUtils;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.discovery.MethodSelector;
import org.junit.platform.engine.discovery.NestedMethodSelector;
import org.junit.platform.engine.discovery.UniqueIdSelector;
import org.junit.platform.engine.support.discovery.SelectorResolver;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectUniqueId;
import static org.junit.platform.engine.support.discovery.SelectorResolver.Resolution.unresolved;

/**
 * This class is from the
 * <a href="https://github.com/junit-team/junit5/">JUnit Jupiter</a> library.
 *
 * <p>This is a modified version of
 * {@code org.junit.jupiter.engine.discovery.MethodSelectorResolver}.
 */
class MethodSelectorResolver implements SelectorResolver {
    private static final String SEGMENT_TYPE = "method";

    private final Predicate<Class<?>> testClassPredicate = new IsTestClassWithProperties().or(new IsNestedTestClass());
    private final IsPropertyMethod isPropertyMethod = new IsPropertyMethod();

    @Override
    public Resolution resolve(MethodSelector selector, Context context) {
        return resolve(context, emptyList(), selector.getJavaClass(), selector::getJavaMethod, Match::exact);
    }

    @Override
    public Resolution resolve(NestedMethodSelector selector, Context context) {
        return resolve(context, selector.getEnclosingClasses(), selector.getNestedClass(), selector::getMethod,
                Match::exact);
    }

    private Resolution resolve(Context context, List<Class<?>> enclosingClasses, Class<?> testClass,
                               Supplier<Method> methodSupplier,
                               BiFunction<TestDescriptor, Supplier<Set<? extends DiscoverySelector>>, Match> matchFactory) {

        if (!testClassPredicate.test(testClass)) {
            return unresolved();
        }

        final Method method = methodSupplier.get();
        return resolve(enclosingClasses, testClass, method, context)
                .map(testDescriptor -> matchFactory.apply(testDescriptor, Collections::emptySet))
                .map(Resolution::match)
                .orElse(unresolved());
    }

    @Override
    public Resolution resolve(UniqueIdSelector selector, Context context) {
        UniqueId uniqueId = selector.getUniqueId();
        // @formatter:off
        return resolveUniqueIdIntoTestDescriptor(uniqueId, context)
            .map(testDescriptor -> {
                boolean exactMatch = uniqueId.equals(testDescriptor.getUniqueId());
                return Resolution.match(exactMatch
                        ? Match.exact(testDescriptor)
                        : Match.partial(testDescriptor, Collections::emptySet));
            })
            .orElse(unresolved());
    }

    private Optional<TestDescriptor> resolve(List<Class<?>> enclosingClasses, Class<?> testClass, Method method, Context context) {
        if (!isPropertyMethod.test(method)) {
            return Optional.empty();
        }
        return context.addToParent(() -> selectClass(enclosingClasses, testClass), //
            parent -> Optional.of(createTestDescriptor(createUniqueId(method, parent), testClass, method)));
    }

    private DiscoverySelector selectClass(List<Class<?>> enclosingClasses, Class<?> testClass) {
        if (enclosingClasses.isEmpty()) {
            return DiscoverySelectors.selectClass(testClass);
        }
        return DiscoverySelectors.selectNestedClass(enclosingClasses, testClass);
    }

    private Optional<TestDescriptor> resolveUniqueIdIntoTestDescriptor(UniqueId uniqueId, Context context) {
        UniqueId.Segment lastSegment = uniqueId.getLastSegment();

        return context.addToParent(() -> selectUniqueId(uniqueId.removeLastSegment()), parent -> {
            String methodSpecPart = lastSegment.getValue();
            Class<?> testClass = ((InstancioClassBasedTestDescriptor) parent).getTestClass();
            return MethodFinder.findMethod(methodSpecPart, testClass)
                .filter(isPropertyMethod)
                .map(method -> createTestDescriptor(createUniqueId(method, parent), testClass, method));
        });
    }

    private UniqueId createUniqueId(Method method, TestDescriptor parent) {
        String methodId = String.format("%s(%s)", method.getName(),
            ClassUtils.nullSafeToString(method.getParameterTypes()));
        return parent.getUniqueId().append(SEGMENT_TYPE, methodId);
    }

    private TestDescriptor createTestDescriptor(UniqueId uniqueId, Class<?> testClass, Method method) {
        return new InstancioQuickcheckTestMethodTestDescriptor(uniqueId, testClass, method);
    }
}

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
package org.instancio.quickcheck.internal.discovery;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectUniqueId;
import static org.junit.platform.engine.support.discovery.SelectorResolver.Resolution.unresolved;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.instancio.quickcheck.internal.descriptor.InstancioClassBasedTestDescriptor;
import org.instancio.quickcheck.internal.descriptor.InstancioQuickcheckTestMethodTestDescriptor;
import org.instancio.quickcheck.internal.discovery.predicates.IsPropertyMethod;
import org.instancio.quickcheck.internal.discovery.predicates.IsTestClassWithProperties;
import org.junit.jupiter.engine.descriptor.Filterable;
import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor;
import org.junit.jupiter.engine.discovery.predicates.IsNestedTestClass;
import org.junit.platform.commons.util.ClassUtils;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.discovery.MethodSelector;
import org.junit.platform.engine.discovery.NestedMethodSelector;
import org.junit.platform.engine.discovery.UniqueIdSelector;
import org.junit.platform.engine.support.discovery.SelectorResolver;

class MethodSelectorResolver implements SelectorResolver {
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
            .map(testDescriptor -> matchFactory.apply(testDescriptor, expansionCallback(testDescriptor)))
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
                if (testDescriptor instanceof Filterable) {
                    Filterable filterable = (Filterable) testDescriptor;
                    if (exactMatch) {
                        filterable.getDynamicDescendantFilter().allowAll();
                    }
                    else {
                        filterable.getDynamicDescendantFilter().allowUniqueIdPrefix(uniqueId);
                    }
                }
                return Resolution.match(exactMatch ? Match.exact(testDescriptor) : Match.partial(testDescriptor, expansionCallback(testDescriptor)));
            })
            .orElse(unresolved());
    }

    private Supplier<Set<? extends DiscoverySelector>> expansionCallback(TestDescriptor testDescriptor) {
        return () -> {
            if (testDescriptor instanceof Filterable) {
                Filterable filterable = (Filterable) testDescriptor;
                filterable.getDynamicDescendantFilter().allowAll();
            }
            return emptySet();
        };
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

        if (TestMethodTestDescriptor.SEGMENT_TYPE.equals(lastSegment.getType())) {
            return context.addToParent(() -> selectUniqueId(uniqueId.removeLastSegment()), parent -> {
                String methodSpecPart = lastSegment.getValue();
                Class<?> testClass = ((InstancioClassBasedTestDescriptor) parent).getTestClass();
                return MethodFinder.findMethod(methodSpecPart, testClass)
                    .filter(isPropertyMethod)
                    .map(method -> createTestDescriptor(createUniqueId(method, parent), testClass, method));
            });
        }

        return Optional.empty();
    }

    private UniqueId createUniqueId(Method method, TestDescriptor parent) {
        String methodId = String.format("%s(%s)", method.getName(),
            ClassUtils.nullSafeToString(method.getParameterTypes()));
        return parent.getUniqueId().append(TestMethodTestDescriptor.SEGMENT_TYPE, methodId);
    }

    private TestDescriptor createTestDescriptor(UniqueId uniqueId, Class<?> testClass, Method method) {
        return new InstancioQuickcheckTestMethodTestDescriptor(uniqueId, testClass, method);
    }
}

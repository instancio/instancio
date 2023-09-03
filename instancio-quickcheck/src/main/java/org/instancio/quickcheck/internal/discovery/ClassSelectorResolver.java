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

import static java.util.stream.Collectors.toCollection;
import static org.junit.platform.commons.util.ReflectionUtils.findMethods;
import static org.junit.platform.engine.support.discovery.SelectorResolver.Resolution.unresolved;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.instancio.quickcheck.internal.descriptor.InstancioQuickcheckClassTestDescriptor;
import org.instancio.quickcheck.internal.discovery.predicates.IsPropertyMethod;
import org.instancio.quickcheck.internal.discovery.predicates.IsTestClassWithProperties;
import org.junit.jupiter.engine.descriptor.ClassTestDescriptor;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.discovery.NestedClassSelector;
import org.junit.platform.engine.discovery.UniqueIdSelector;
import org.junit.platform.engine.support.discovery.SelectorResolver;

class ClassSelectorResolver implements SelectorResolver {
    private final IsTestClassWithProperties isTestClassWithProperties = new IsTestClassWithProperties();
    private final IsPropertyMethod isPropertyMethod = new IsPropertyMethod();
    
    private final Predicate<String> classNameFilter;

    ClassSelectorResolver(Predicate<String> classNameFilter) {
        this.classNameFilter = classNameFilter;
    }

    @Override
    public Resolution resolve(ClassSelector selector, Context context) {
        Class<?> testClass = selector.getJavaClass();

        // Nested tests are never filtered out
        if (isTestClassWithProperties.test(testClass) && classNameFilter.test(testClass.getName())) {
            return toResolution(context.addToParent(parent -> Optional.of(newClassTestDescriptor(parent, testClass))));
        }

        return unresolved();
    }

    @Override
    public Resolution resolve(NestedClassSelector selector, Context context) {
        return unresolved();
    }

    @Override
    public Resolution resolve(UniqueIdSelector selector, Context context) {
        UniqueId uniqueId = selector.getUniqueId();
        UniqueId.Segment lastSegment = uniqueId.getLastSegment();
        if (ClassTestDescriptor.SEGMENT_TYPE.equals(lastSegment.getType())) {
            String className = lastSegment.getValue();
            return ReflectionUtils.tryToLoadClass(className).toOptional().filter(isTestClassWithProperties).map(
                testClass -> toResolution(
                    context.addToParent(parent -> Optional.of(newClassTestDescriptor(parent, testClass))))).orElse(
                        unresolved());
        }
        return unresolved();
    }

    private InstancioQuickcheckClassTestDescriptor newClassTestDescriptor(TestDescriptor parent, Class<?> testClass) {
        return new InstancioQuickcheckClassTestDescriptor(parent.getUniqueId().append(ClassTestDescriptor.SEGMENT_TYPE,
            testClass.getName()), testClass);
    }

    private Resolution toResolution(Optional<InstancioQuickcheckClassTestDescriptor> testDescriptor) {
        return testDescriptor.map(it -> {
            Class<?> testClass = it.getTestClass();
            List<Class<?>> testClasses = new ArrayList<>(it.getEnclosingTestClasses());
            testClasses.add(testClass);

            return Resolution.match(Match.exact(it, () -> {
                Stream<DiscoverySelector> methods = findMethods(testClass, isPropertyMethod).stream()
                        .map(method -> selectMethod(testClasses, method));
                return methods.collect(toCollection((Supplier<Set<DiscoverySelector>>) LinkedHashSet::new));
            }));
        }).orElse(unresolved());
    }

    private DiscoverySelector selectMethod(List<Class<?>> classes, Method method) {
        if (classes.size() == 1) {
            return DiscoverySelectors.selectMethod(classes.get(0), method);
        }
        int lastIndex = classes.size() - 1;
        return DiscoverySelectors.selectNestedMethod(classes.subList(0, lastIndex), classes.get(lastIndex), method);
    }
}

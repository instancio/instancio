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
package org.instancio.quickcheck.internal.discovery.predicates;

import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;

import java.util.function.Predicate;

public class IsTestClassWithProperties implements Predicate<Class<?>> {
    private final IsNestedTestClass isNestedTestClass = new IsNestedTestClass();
    private final IsPotentialTestContainer isPotentialTestContainer = new IsPotentialTestContainer();

    @SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
    @Override
    public boolean test(Class<?> candidate) {
        return isPotentialTestContainer.test(candidate) &&
                (hasPropertyMethods(candidate) || hasNestedTests(candidate));
    }

    private boolean hasPropertyMethods(Class<?> candidate) {
        return !ReflectionSupport.findMethods(candidate, new IsPropertyMethod(), HierarchyTraversalMode.TOP_DOWN).isEmpty();
    }

    private boolean hasNestedTests(Class<?> candidate) {
        return !ReflectionSupport.findNestedClasses(candidate, isNestedTestClass).isEmpty();
    }
}

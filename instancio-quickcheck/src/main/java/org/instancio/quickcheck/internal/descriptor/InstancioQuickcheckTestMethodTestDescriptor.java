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
package org.instancio.quickcheck.internal.descriptor;

import org.instancio.quickcheck.internal.util.ClassUtils;
import org.junit.jupiter.api.Tag;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.engine.TestTag;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

public class InstancioQuickcheckTestMethodTestDescriptor extends AbstractTestDescriptor {
    /**
     * Set of method-level tags; does not contain tags from parent.
     */
    private final Set<TestTag> tags;
    private final Class<?> testClass;
    private final Method testMethod;

    public InstancioQuickcheckTestMethodTestDescriptor(UniqueId uniqueId, Class<?> testClass, Method testMethod) {
        this(uniqueId, DisplayNameUtils.determineDisplayNameForMethod(testClass, testMethod), testClass, testMethod);
    }

    InstancioQuickcheckTestMethodTestDescriptor(UniqueId uniqueId, String displayName, Class<?> testClass, Method testMethod) {
        super(uniqueId, displayName, MethodSource.from(testClass, testMethod));
        this.testClass = Objects.requireNonNull(testClass, "Class must not be null");
        this.testMethod = testMethod;
        this.tags = getTags(testMethod);
    }

    @Override
    public final Set<TestTag> getTags() {
        // return modifiable copy
        Set<TestTag> allTags = new LinkedHashSet<>(this.tags);
        getParent().ifPresent(parentDescriptor -> allTags.addAll(parentDescriptor.getTags()));
        return allTags;
    }

    public final Class<?> getTestClass() {
        return this.testClass;
    }

    public final Method getTestMethod() {
        return this.testMethod;
    }

    @Override
    public String getLegacyReportingName() {
        return String.format("%s(%s)", testMethod.getName(),
                ClassUtils.nullSafeToString(Class::getSimpleName, testMethod.getParameterTypes()));
    }

    @Override
    public Type getType() {
        return Type.TEST;
    }

    static Set<TestTag> getTags(AnnotatedElement element) {
        return AnnotationSupport.findRepeatableAnnotations(element, Tag.class).stream()
                .map(Tag::value)
                .filter(TestTag::isValid)
                .map(TestTag::create)
                .collect(collectingAndThen(toCollection(LinkedHashSet::new), Collections::unmodifiableSet));
    }
}

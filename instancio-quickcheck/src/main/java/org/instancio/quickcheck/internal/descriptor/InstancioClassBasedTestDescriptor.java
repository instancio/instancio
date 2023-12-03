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
package org.instancio.quickcheck.internal.descriptor;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static org.junit.platform.commons.util.AnnotationUtils.findRepeatableAnnotations;

import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Tag;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.TestTag;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

public abstract class InstancioClassBasedTestDescriptor extends AbstractTestDescriptor {
    private final Class<?> testClass;
    private final Set<TestTag> tags;

    InstancioClassBasedTestDescriptor(UniqueId uniqueId, Class<?> testClass, String displayName, TestSource source) {
        super(uniqueId, displayName, source);
        this.testClass = testClass;
        this.tags = getTags(testClass);
    }
    
    @Override
    public Type getType() {
        return Type.CONTAINER;
    }
    
    public Class<?> getTestClass() {
        return testClass;
    }
    
    @Override
    public Set<TestTag> getTags() {
        return new LinkedHashSet<>(this.tags);
    }
    
    public List<Class<?>> getEnclosingTestClasses() {
        return emptyList();
    }

    public abstract Object createTestInstance();

    static Set<TestTag> getTags(AnnotatedElement element) {
        return findRepeatableAnnotations(element, Tag.class).stream()
            .map(Tag::value)
            .filter(tag -> TestTag.isValid(tag))
            .map(TestTag::create)
            .collect(collectingAndThen(toCollection(LinkedHashSet::new), Collections::unmodifiableSet));
    }
}

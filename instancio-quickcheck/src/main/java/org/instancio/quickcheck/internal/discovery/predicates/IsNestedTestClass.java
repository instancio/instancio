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

import org.instancio.quickcheck.internal.util.ReflectionUtils;
import org.junit.jupiter.api.Nested;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.function.Predicate;

/**
 * This class is from the
 * <a href="https://github.com/junit-team/junit5/">JUnit Jupiter</a> library.
 *
 * <p>This is a modified version of
 * {@code org.junit.jupiter.engine.discovery.predicates.IsNestedTestClass}.
 */
public class IsNestedTestClass implements Predicate<Class<?>> {

    @Override
    @SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation") // false positive
    public boolean test(Class<?> candidate) {
        //please do not collapse into single return
        final boolean isNotPrivate = !ReflectionUtils.isPrivate(candidate);
        final boolean isInnerClass = ReflectionUtils.isInnerClass(candidate);

        return isNotPrivate
                && isInnerClass
                && AnnotationSupport.isAnnotated(candidate, Nested.class);
    }
}

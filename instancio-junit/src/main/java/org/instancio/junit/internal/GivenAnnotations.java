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
package org.instancio.junit.internal;

import org.instancio.documentation.InternalApi;
import org.instancio.junit.Given;
import org.instancio.junit.GivenProvider;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;

/**
 * Resolves {@link Given @Given} declarations for an annotated element.
 *
 * <p>{@code @Given} is repeatable, so an element may carry more than one,
 * whether declared directly or contributed by a meta-annotation. Since a
 * repeated annotation is stored in its container, these must be resolved
 * with {@code findRepeatableAnnotations()} rather than {@code findAnnotation()}
 * or {@code isAnnotated()}, which see only the container.
 */
@InternalApi
public final class GivenAnnotations {

    public static boolean isAnnotated(final AnnotatedElement element) {
        return !findGivenAnnotations(element).isEmpty();
    }

    /**
     * Returns the providers declared by every {@code @Given} that applies to
     * the element, so that providers contributed by meta-annotations combine
     * with those declared directly.
     */
    public static List<Class<? extends GivenProvider>> findProviderClasses(final AnnotatedElement element) {
        return findGivenAnnotations(element).stream()
                .map(Given::value)
                .flatMap(Arrays::stream)
                .distinct()
                .toList();
    }

    private static List<Given> findGivenAnnotations(final AnnotatedElement element) {
        return AnnotationSupport.findRepeatableAnnotations(element, Given.class);
    }

    private GivenAnnotations() {
        // non-instantiable
    }
}

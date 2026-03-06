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

import org.instancio.internal.ApiValidator;
import org.instancio.junit.Given;
import org.instancio.junit.GivenProvider;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ElementAnnotations {

    private final List<Annotation> annotations;
    private final List<Class<? extends GivenProvider>> providerClasses;

    public ElementAnnotations(final List<Annotation> annotations) {
        this.annotations = annotations;
        this.providerClasses = annotations.stream()
                .filter(a -> a.annotationType() == Given.class)
                .flatMap(a -> Arrays.stream(((Given) a).value()))
                .distinct()
                .collect(Collectors.toList());
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A getAnnotation(final Class<A> annotationType) {
        final List<Annotation> results = annotations.stream()
                .filter(a -> a.annotationType() == annotationType)
                .toList();

        if (results.isEmpty()) {
            return null;
        }

        ApiValidator.isFalse(results.size() > 1,
                "Found multiple annotations of type %s", annotationType.getName());

        return (A) results.get(0);
    }

    public List<Class<? extends GivenProvider>> getProviderClasses() {
        return providerClasses;
    }
}

/*
 * Copyright 2022-2025 the original author or authors.
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

import org.instancio.Random;
import org.instancio.internal.util.TypeUtils;
import org.instancio.junit.GivenProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

public class InternalElementContext implements GivenProvider.ElementContext {

    private final AnnotatedElement targetElement;
    private final ElementAnnotations elementAnnotations;
    private final Type targetType;
    private final Random random;

    public InternalElementContext(
            final AnnotatedElement targetElement,
            final Type targetType,
            final ElementAnnotations elementAnnotations,
            final Random random) {

        this.targetElement = targetElement;
        this.targetType = targetType;
        this.elementAnnotations = elementAnnotations;
        this.random = random;
    }

    @Override
    public AnnotatedElement getTargetElement() {
        return targetElement;
    }

    @Override
    public Type getTargetType() {
        return targetType;
    }

    @Override
    public Class<?> getTargetClass() {
        return TypeUtils.getRawType(targetType);
    }

    @Override
    public Random random() {
        return random;
    }

    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotationType) {
        return elementAnnotations.getAnnotation(annotationType);
    }
}

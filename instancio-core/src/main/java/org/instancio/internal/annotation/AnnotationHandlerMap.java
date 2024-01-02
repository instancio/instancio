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
package org.instancio.internal.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

class AnnotationHandlerMap {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotationHandlerMap.class);

    private final Map<Class<?>, FieldAnnotationHandler> handlerMap = new HashMap<>();

    final <A extends Annotation> void put(
            final Supplier<Class<A>> annotationTypeSupplier,
            final FieldAnnotationHandler handler) {

        try {
            handlerMap.put(annotationTypeSupplier.get(), handler);
        } catch (NoClassDefFoundError error) {
            LOG.trace("Annotation not available on classpath: {}", error.toString());
        }
    }

    final FieldAnnotationHandler get(final Annotation annotation) {
        return handlerMap.get(annotation.annotationType());
    }
}

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
package org.instancio.internal.annotation;

import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.settings.BeanValidationTarget;
import org.instancio.settings.Keys;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class AnnotationExtractor {
    private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];

    private final BeanValidationTarget beanValidationTarget;
    private final GetterMethodResolver getterMethodResolver;

    public AnnotationExtractor(final ModelContext context) {
        this.beanValidationTarget = context.getSettings().get(Keys.BEAN_VALIDATION_TARGET);
        this.getterMethodResolver = new GetterMethodResolverImpl();
    }

    @NotNull
    public Annotation[] getAnnotations(final InternalNode node) {
        final Field field = node.getField();

        if (field == null) {
            return getTypeUseAnnotations(node);
        }

        if (beanValidationTarget == BeanValidationTarget.FIELD) {
            return field.getDeclaredAnnotations();
        }

        // Get constraint annotations from the getter, if exists
        final Method getter = getterMethodResolver.getGetter(node);
        return getter == null ? EMPTY_ANNOTATIONS : getter.getDeclaredAnnotations();
    }

    /**
     * Extract field's {@link ElementType#TYPE_USE} annotations,
     * e.g. {@code Map<@Email String, @Negative Integer>}.
     */
    private static Annotation[] getTypeUseAnnotations(final InternalNode node) {
        final Field parentField = node.getParent() == null ? null : node.getParent().getField();

        if (parentField != null && parentField.getAnnotatedType() instanceof AnnotatedParameterizedType) {
            final AnnotatedParameterizedType apt = (AnnotatedParameterizedType) parentField.getAnnotatedType();
            final int childIndex = CollectionUtils.identityIndexOf(node, node.getParent().getChildren());

            if (childIndex != -1) {
                final AnnotatedType[] annotatedTypes = apt.getAnnotatedActualTypeArguments();
                return annotatedTypes[childIndex].getAnnotations();
            }
        }
        return EMPTY_ANNOTATIONS;
    }
}

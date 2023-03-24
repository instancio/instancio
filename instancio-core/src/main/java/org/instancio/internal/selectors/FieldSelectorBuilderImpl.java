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
package org.instancio.internal.selectors;

import org.instancio.FieldSelectorBuilder;
import org.instancio.PredicateSelector;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.util.Format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@SuppressWarnings("PMD.InsufficientStringBufferDeclaration")
public class FieldSelectorBuilderImpl implements FieldSelectorBuilder, SelectorBuilder {

    private final List<Predicate<Field>> fieldPredicates = new ArrayList<>(4);
    private final StringBuilder description = new StringBuilder("fields()");

    @Override
    public FieldSelectorBuilder named(final String fieldName) {
        ApiValidator.notNull(fieldName, () -> Format.selectorErrorMessage(
                "Field name must not be null.",
                "named", description.toString(), new Throwable()));

        fieldPredicates.add(field -> field.getName().equals(fieldName));
        description.append(".named(\"").append(fieldName).append("\")");
        return this;
    }

    @Override
    public FieldSelectorBuilder matching(final String regex) {
        ApiValidator.notNull(regex, () -> Format.selectorErrorMessage(
                "Regex must not be null.",
                "matching", description.toString(), new Throwable()));

        fieldPredicates.add(field -> field.getName().matches(regex));
        description.append(".matching(\"").append(regex).append("\")");
        return this;
    }

    @Override
    public FieldSelectorBuilder ofType(final Class<?> fieldType) {
        ApiValidator.notNull(fieldType, () -> Format.selectorErrorMessage(
                "Field type must not be null.",
                "ofType", description.toString(), new Throwable()));

        fieldPredicates.add(field -> fieldType.isAssignableFrom(field.getType()));
        description.append(".ofType(").append(fieldType.getSimpleName()).append(')');
        return this;
    }

    @Override
    public FieldSelectorBuilder declaredIn(final Class<?> type) {
        ApiValidator.notNull(type, () -> Format.selectorErrorMessage(
                "Declaring type must not be null.",
                "declaredIn", description.toString(), new Throwable()));

        fieldPredicates.add(field -> field.getDeclaringClass() == type);
        description.append(".declaredIn(").append(type.getSimpleName()).append(')');
        return this;
    }

    @Override
    public <A extends Annotation> FieldSelectorBuilder annotated(final Class<? extends A> annotation) {
        ApiValidator.notNull(annotation, () -> Format.selectorErrorMessage(
                "Field's declared annotation must not be null.",
                "annotated", description.toString(), new Throwable()));

        fieldPredicates.add(field -> field.getDeclaredAnnotation(annotation) != null);
        description.append(".annotated(").append(annotation.getSimpleName()).append(')');
        return this;
    }

    @Override
    public PredicateSelector build() {
        Predicate<Field> predicate = Objects::nonNull;
        for (Predicate<Field> p : fieldPredicates) {
            predicate = predicate.and(p);
        }
        return new PredicateSelectorImpl(SelectorTargetKind.FIELD, predicate, null, null, description.toString());
    }

    @Override
    public String toString() {
        return description.toString();
    }
}

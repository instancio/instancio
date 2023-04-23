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
import org.instancio.internal.ApiValidator;
import org.instancio.internal.util.Format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class FieldSelectorBuilderImpl
        extends PredicateSelectorBuilderTemplate<Field>
        implements FieldSelectorBuilder {

    @Override
    protected String apiMethod() {
        return "fields()";
    }

    @Override
    protected PredicateSelectorImpl.Builder createBuilder() {
        return PredicateSelectorImpl.builder().fieldPredicate(buildPredicate());
    }

    @Override
    public FieldSelectorBuilder named(final String fieldName) {
        ApiValidator.notNull(fieldName, () -> Format.selectorErrorMessage(
                "field name must not be null.",
                "named", description().toString(), new Throwable()));

        addPredicate(field -> field.getName().equals(fieldName));
        description().append(".named(\"").append(fieldName).append("\")");
        return this;
    }

    @Override
    public FieldSelectorBuilder matching(final String regex) {
        ApiValidator.notNull(regex, () -> Format.selectorErrorMessage(
                "regex must not be null.",
                "matching", description().toString(), new Throwable()));

        addPredicate(field -> field.getName().matches(regex));
        description().append(".matching(\"").append(regex).append("\")");
        return this;
    }

    @Override
    public FieldSelectorBuilder ofType(final Class<?> fieldType) {
        ApiValidator.notNull(fieldType, () -> Format.selectorErrorMessage(
                "field type must not be null.",
                "ofType", description().toString(), new Throwable()));

        addPredicate(field -> fieldType.isAssignableFrom(field.getType()));
        description().append(".ofType(").append(fieldType.getSimpleName()).append(')');
        return this;
    }

    @Override
    public FieldSelectorBuilder declaredIn(final Class<?> type) {
        ApiValidator.notNull(type, () -> Format.selectorErrorMessage(
                "declaring type must not be null.",
                "declaredIn", description().toString(), new Throwable()));

        addPredicate(field -> field.getDeclaringClass() == type);
        description().append(".declaredIn(").append(type.getSimpleName()).append(')');
        return this;
    }

    @Override
    public <A extends Annotation> FieldSelectorBuilder annotated(final Class<? extends A> annotation) {
        ApiValidator.notNull(annotation, () -> Format.selectorErrorMessage(
                "field's declared annotation must not be null.",
                "annotated", description().toString(), new Throwable()));

        addPredicate(field -> field.getDeclaredAnnotation(annotation) != null);
        description().append(".annotated(").append(annotation.getSimpleName()).append(')');
        return this;
    }
}

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

import org.instancio.TypeSelectorBuilder;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.util.Format;

import java.lang.annotation.Annotation;

public class TypeSelectorBuilderImpl
        extends PredicateSelectorBuilderTemplate<Class<?>>
        implements TypeSelectorBuilder {

    @Override
    protected String apiMethod() {
        return "types()";
    }

    @Override
    protected PredicateSelectorImpl.Builder createBuilder() {
        return PredicateSelectorImpl.builder().typePredicate(buildPredicate());
    }

    @Override
    public TypeSelectorBuilder of(final Class<?> type) {
        ApiValidator.notNull(type, () -> Format.selectorErrorMessage(
                "type must not be null.",
                "of", description().toString(), new Throwable()));

        addPredicate(type::isAssignableFrom);
        description().append(".of(").append(type.getSimpleName()).append(')');
        return this;
    }

    @Override
    public <A extends Annotation> TypeSelectorBuilder annotated(final Class<? extends A> annotation) {
        ApiValidator.notNull(annotation, () -> Format.selectorErrorMessage(
                "type's declared annotation must not be null.",
                "annotated", description().toString(), new Throwable()));

        addPredicate(klass -> klass.getDeclaredAnnotation(annotation) != null);
        description().append(".annotated(").append(annotation.getSimpleName()).append(')');
        return this;
    }

    @Override
    public TypeSelectorBuilder excluding(final Class<?> type) {
        ApiValidator.notNull(type, () -> Format.selectorErrorMessage(
                "excluded type must not be null.",
                "excluding", description().toString(), new Throwable()));

        addPredicate(klass -> klass != type);
        description().append(".excluding(").append(type.getSimpleName()).append(')');
        return this;
    }
}

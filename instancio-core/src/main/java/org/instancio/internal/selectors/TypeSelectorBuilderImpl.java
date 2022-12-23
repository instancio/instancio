/*
 * Copyright 2022 the original author or authors.
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

import org.instancio.PredicateSelector;
import org.instancio.TypeSelectorBuilder;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.util.Format;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@SuppressWarnings("PMD.InsufficientStringBufferDeclaration")
public class TypeSelectorBuilderImpl implements TypeSelectorBuilder, SelectorBuilder {

    private final List<Predicate<Class<?>>> classPredicates = new ArrayList<>(3);
    private final StringBuilder description = new StringBuilder("types()");

    @Override
    public TypeSelectorBuilder of(final Class<?> type) {
        ApiValidator.notNull(type, () -> Format.selectorErrorMessage(
                "Type must not be null.",
                "of", description.toString(), new Throwable()));

        classPredicates.add(type::isAssignableFrom);
        description.append(".of(").append(type.getSimpleName()).append(')');
        return this;
    }

    @Override
    public <A extends Annotation> TypeSelectorBuilder annotated(final Class<? extends A> annotation) {
        ApiValidator.notNull(annotation, () -> Format.selectorErrorMessage(
                "Type's declared annotation must not be null.",
                "annotated", description.toString(), new Throwable()));

        classPredicates.add(klass -> klass.getDeclaredAnnotation(annotation) != null);
        description.append(".annotated(").append(annotation.getSimpleName()).append(')');
        return this;
    }

    @Override
    public TypeSelectorBuilder excluding(final Class<?> type) {
        ApiValidator.notNull(type, () -> Format.selectorErrorMessage(
                "Excluded type must not be null.",
                "excluding", description.toString(), new Throwable()));

        classPredicates.add(klass -> klass != type);
        description.append(".excluding(").append(type.getSimpleName()).append(')');
        return this;
    }

    @Override
    public PredicateSelector build() {
        Predicate<Class<?>> predicate = Objects::nonNull;
        for (Predicate<Class<?>> p : classPredicates) {
            predicate = predicate.and(p);
        }
        return new PredicateSelectorImpl(SelectorTargetKind.CLASS, null, predicate, description.toString());
    }

    @Override
    public String toString() {
        return description.toString();
    }
}

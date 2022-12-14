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
import org.instancio.TargetSelector;
import org.instancio.exception.InstancioException;
import org.instancio.internal.util.Format;
import org.instancio.internal.util.ObjectUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class PredicateSelectorImpl implements PredicateSelector, Flattener, UnusedSelectorDescription {
    private static final Predicate<Field> NON_NULL_FIELD = Objects::nonNull;
    private static final Predicate<Class<?>> NON_NULL_TYPE = Objects::nonNull;

    private final SelectorTargetKind selectorTargetKind;
    private final Predicate<Field> fieldPredicate;
    private final Predicate<Class<?>> classPredicate;
    private final String apiInvocationDescription;
    private final Throwable stackTraceHolder;

    public PredicateSelectorImpl(final SelectorTargetKind selectorTargetKind,
                                 @Nullable final Predicate<Field> fieldPredicate,
                                 @Nullable final Predicate<Class<?>> classPredicate,
                                 @Nullable final String apiInvocationDescription) {

        this(selectorTargetKind, fieldPredicate, classPredicate, apiInvocationDescription, new Throwable());
    }

    /**
     * @param selectorTargetKind       selector target's kind
     * @param fieldPredicate           field predicate, applicable to field selectors only
     * @param classPredicate           class predicate, applicable to type selectors only
     * @param apiInvocationDescription string describing builder method(s) invoked
     * @param stackTraceHolder         a throwable containing stacktrace line where selector was used
     */
    PredicateSelectorImpl(final SelectorTargetKind selectorTargetKind,
                          @Nullable final Predicate<Field> fieldPredicate,
                          @Nullable final Predicate<Class<?>> classPredicate,
                          @Nullable final String apiInvocationDescription,
                          final Throwable stackTraceHolder) {

        this.selectorTargetKind = selectorTargetKind;
        this.fieldPredicate = fieldPredicate == null ? null : NON_NULL_FIELD.and(fieldPredicate);
        this.classPredicate = classPredicate == null ? null : NON_NULL_TYPE.and(classPredicate);
        this.apiInvocationDescription = apiInvocationDescription;
        this.stackTraceHolder = stackTraceHolder;
    }

    @Override
    public List<TargetSelector> flatten() {
        return Collections.singletonList(this);
    }

    @Override
    public String getDescription() {
        return String.format("%s%n    at %s", this, Format.firstNonInstancioStackTraceLine(stackTraceHolder));
    }

    public SelectorTargetKind getSelectorTargetKind() {
        return selectorTargetKind;
    }

    public Predicate<Field> getFieldPredicate() {
        return fieldPredicate;
    }

    public Predicate<Class<?>> getClassPredicate() {
        return classPredicate;
    }

    private String buildCustomPredicateToString() {
        if (selectorTargetKind == SelectorTargetKind.FIELD) {
            return "fields(Predicate<Field>)";
        } else if (selectorTargetKind == SelectorTargetKind.CLASS) {
            return "types(Predicate<Class>)";
        }
        // should not be reachable
        throw new InstancioException("Unknown selector kind: " + selectorTargetKind);
    }

    @Override
    public String toString() {
        return ObjectUtils.defaultIfNull(apiInvocationDescription, this::buildCustomPredicateToString);
    }
}

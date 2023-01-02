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
package org.instancio;

import java.lang.annotation.Annotation;

/**
 * A builder for constructing predicate-based type selectors.
 *
 * <p>An instance of the builder can be obtained using {@link Select#types()}.
 * Other methods from this class can be chained to form logical {@code AND}
 * relationships, for example:</p>
 *
 * <pre>{@code
 * types().of(BaseEntity.class).annotated(Entity.class).annotated(Table.class)
 * }</pre>
 *
 * <p>will match {@code BaseEntity} including its subtypes, that are annotated with
 * {@code @Entity} <b>and</b> {@code @Table}.</p>
 *
 * @see Select
 * @see PredicateSelector
 * @see TargetSelector
 * @since 1.6.0
 */
public interface TypeSelectorBuilder extends TargetSelector {

    /**
     * Matches specified type, including subtypes.
     * For example, selecting types {@code of(Collection.class}}
     * will match {@code List}, {@code Set}, etc.
     *
     * @param type to match
     * @return selector builder
     * @since 1.6.0
     */
    TypeSelectorBuilder of(Class<?> type);

    /**
     * Matches types annotated with the specified annotation,
     * ignoring inherited annotations.
     *
     * <p><b>Note:</b> this method only matches annotations declared
     * on classes. To select annotated fields, use
     * {@link FieldSelectorBuilder#annotated(Class)}.</p>
     *
     * <p>The method can be chained to require multiple annotations.</p>
     *
     * @param annotation declared annotation to match
     * @param <A>        annotation type
     * @return selector builder
     * @since 1.6.0
     */
    <A extends Annotation> TypeSelectorBuilder annotated(Class<? extends A> annotation);

    /**
     * Excludes specified class from matching.
     * <p>
     * The method can be chained to exclude multiple types.
     *
     * @param type type to exclude
     * @return selector builder
     * @since 1.6.0
     */
    TypeSelectorBuilder excluding(Class<?> type);

}

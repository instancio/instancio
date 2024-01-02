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
package org.instancio;

import java.lang.annotation.Annotation;
import java.lang.reflect.TypeVariable;
import java.util.function.Predicate;

/**
 * A builder for constructing predicate-based field selectors.
 *
 * <p>An instance of the builder can be obtained using {@link Select#fields()}.
 * Other methods from this class can be chained to form logical {@code AND}
 * relationships, for example:</p>
 *
 * <pre>{@code
 * fields().ofType(String.class).annotated(Foo.class).annotated(Bar.class)
 * }</pre>
 *
 * <p>will match String fields annotated {@code @Foo} <b>and</b> {@code @Foo}.</p>
 *
 * @see Select
 * @see PredicateSelector
 * @see TargetSelector
 * @since 1.6.0
 */
public interface FieldSelectorBuilder extends GroupableSelector, DepthSelector, DepthPredicateSelector {

    /**
     * Matches fields with the specified name.
     *
     * @param fieldName exact field name to match
     * @return selector builder
     * @see #matching(String)
     * @since 1.6.0
     */
    FieldSelectorBuilder named(String fieldName);

    /**
     * Matches field names using the specified regular expression.
     *
     * @param regex for matching field names
     * @return selector builder
     * @see #named(String)
     * @since 2.2.0
     */
    FieldSelectorBuilder matching(String regex);

    /**
     * Matches fields against the specified type, including subtypes.
     * For example, selecting fields {@code ofType(Collection.class}}
     * will match fields declared as {@code List} or {@code Set}.
     *
     * <p><b>Note</b>: this method will <i>not</i> match fields declared
     * as a {@link TypeVariable}. For instance, in the following snippet,
     * even though {@code T} is bound to {@code String} type, the selector
     * {@code ofType(String.class)} will not match the field. In this case,
     * targeting the field by name would be a better option.</p>
     *
     * <pre>{@code
     * class Item<T> {
     *     private T value;
     * }
     *
     * Item<String> result = Instancio.of(new TypeToken<Item<String>>() {})
     *     .set(fields().ofType(String.class), "foo") // will not match Item.value
     *     .create();
     *
     * assertThat(result.getValue()).isNotEqualTo("foo");
     * }</pre>
     *
     * @param fieldType field type to match
     * @return selector builder
     * @since 1.6.0
     */
    FieldSelectorBuilder ofType(Class<?> fieldType);

    /**
     * Matches fields declared in the specified class.
     *
     * @param type class that declares target field(s)
     * @return selector builder
     * @since 1.6.0
     */
    FieldSelectorBuilder declaredIn(Class<?> type);

    /**
     * Matches fields annotated with the specified annotation,
     * ignoring inherited annotations.
     * <p>
     * The method can be chained to require multiple annotations.
     *
     * @param annotation declared annotation to match
     * @param <A>        annotation type
     * @return selector builder
     * @since 1.6.0
     */
    <A extends Annotation> FieldSelectorBuilder annotated(Class<? extends A> annotation);

    /**
     * {@inheritDoc}
     *
     * @since 2.14.0
     */
    @Override
    GroupableSelector atDepth(int depth);

    /**
     * {@inheritDoc}
     *
     * @since 2.14.0
     */
    @Override
    GroupableSelector atDepth(Predicate<Integer> atDepth);
}

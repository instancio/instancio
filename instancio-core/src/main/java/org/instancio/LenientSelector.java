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

/**
 * A lenient selector does not trigger the "unused selector" error
 * if it does not match any targets.
 *
 * @since 4.4.0
 */
public interface LenientSelector extends TargetSelector {

    /**
     * Marks this selector as lenient, which prevents the selector from
     * producing "unused selector" error if it does not match any target.
     * This provides an alternative to {@link LenientMode#lenient()},
     * which treats <b>all</b> selectors as lenient (not recommended).
     *
     * <p>This method can be useful when using Instancio to create
     * a generic {@link Model}, or in a helper method for creating
     * objects of arbitrary types.
     *
     * <p>For example, the following method will set all {@code lastUpdated}
     * fields to a date in the past:
     *
     * <pre>{@code
     * static <T> Model<T> baseModel(Class<T> klass) {
     *     TargetSelector lastUpdated = Select.fields()
     *         .ofType(Instant.class).named("lastUpdated").lenient();
     *
     *     return Instancio.of(klass)
     *         .generate(lastUpdated, gen -> gen.temporal().instant().past())
     *         .toModel();
     * }
     * }</pre>
     *
     * <p>Marking the selector as lenient will prevent the unused selector error
     * if a given {@code klass} does not have a {@code lastUpdated} field.
     *
     * <p>See also:
     * <a href="https://www.instancio.org/user-guide/#selector-strictness">Selector Strictness</a>
     * section of the User Guide.
     *
     * @return a lenient selector
     * @see LenientMode
     * @since 4.4.0
     */
    TargetSelector lenient();
}
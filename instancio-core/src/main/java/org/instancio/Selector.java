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

/**
 * A selector that can be:
 *
 * <ul>
 *   <li>grouped with other selectors</li>
 *   <li>narrowed down using {@link Scope}</li>
 *   <li>converted to {@link Scope}</li>
 * </ul>
 *
 * @see Select
 * @see TargetSelector
 * @since 1.2.0
 */
public interface Selector extends GroupableSelector, ConvertibleToScope {

    /**
     * Specifies the scope for this selector in order to narrow down the selector target.
     * <p>
     * For example, if the {@code Person} class has two {@code Phone} fields:
     * <p>
     * <pre>{@code
     *     class Person {
     *         private Phone home;
     *         private Phone cell;
     *         // snip...
     *     }
     * }</pre>
     * <p>
     * and we want to set only the {@code cell} phone to a specific value, we can narrow
     * down the selector as follows:
     *
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *         .set(field(Phone.class, "number").within(scope(Person.class, "cell")), "123-45-67")
     *         .create();
     * }</pre>
     * <p>
     * Multiple scopes can be specified top-down, that is starting from the outermost class.
     *
     * @param scopes one or more scopes to apply
     * @return a selector with the specified scope
     * @since 1.3.0
     */
    GroupableSelector within(Scope... scopes);
}
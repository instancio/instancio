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
 * Represents a selector that can be scoped using
 * {@code within(Scope...scopes)}.
 *
 * @since 3.0.0
 */
public interface ScopeableSelector extends GroupableSelector, ConvertibleToScope {

    /**
     * Specifies the scope for this selector in order to narrow
     * down its target.
     *
     * <p>For example, given the following classes:
     *
     * <pre>{@code
     * record Phone(String countryCode, String number) {}
     *
     * record Person(Phone home, Phone cell) {}
     * }</pre>
     *
     * <p>setting {@code home} and {@code cell} phone numbers to different
     * values would require differentiating between two
     * {@code field(Phone::number)}  selectors. This can be achieved using
     * scopes as follows:
     *
     * <pre>{@code
     * Scope homePhone = field(Person::home).toScope();
     * Scope cellPhone = field(Person::cell).toScope();
     *
     * Person person = Instancio.of(Person.class)
     *     .set(field(Phone::number).within(homePhone), "777-88-99")
     *     .set(field(Phone::number).within(cellPhone), "123-45-67")
     *     .create();
     * }</pre>
     *
     * <p>See <a href="https://instancio.org/user-guide/#selector-scopes">Selector Scopes</a>
     * section of the user guide for details.
     *
     * @param scopes one or more scopes to apply
     * @return a selector with the specified scope
     * @since 3.0.0
     */
    GroupableSelector within(Scope... scopes);
}
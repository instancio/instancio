/*
 * Copyright 2022-2025 the original author or authors.
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
 * A result containing a created object and seed that was used for populating its values.
 * A result can be obtained by calling the {@link InstancioApi#asResult()} method.
 *
 * <p>Example:
 * <pre>{@code
 * Result<Person> result = Instancio.of(Person.class)
 *     .set(field(Person::getName), "Homer Simpson")
 *     .asResult();
 *
 * Person person = result.get();
 * long seed = result.getSeed();
 * }</pre>
 *
 * @param <T> the result type
 * @since 1.5.1
 */
public interface Result<T> {

    /**
     * Returns the created object.
     *
     * @return created object
     * @since 1.5.1
     */
    T get();

    /**
     * Returns the seed that was used to populate the created object.
     *
     * @return the seed
     * @since 1.5.1
     */
    long getSeed();
}

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
 * A model containing all the information for populating a class that
 * can be obtained using the {@code 'toModel()'} method:
 * <p>
 * Models can be useful when class population needs to be customised
 * and the customisations re-used in different parts of the code.
 * <p>
 * Example:
 * <pre>{@code
 *     Model<Person> personModel = Instancio.of(Person.class)
 *         .supply(field("fullName"), () -> "Jane Doe")
 *         .generate(field("age"), gen -> gen.ints().min(18).max(65))
 *         .toModel();
 *
 *     // Re-use the model to create instances of Person class
 *     // without duplicating model's details
 *     Person person = Instancio.create(personModel);
 * }</pre>
 * <p>
 * Since the internal data of the model is not part of the public API,
 * this interface does not contain any methods.
 *
 * @param <T> type to be created by this model
 * @since 1.0.1
 */
@SuppressWarnings("unused")
public interface Model<T> {
}

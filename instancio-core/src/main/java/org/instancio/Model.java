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
 * A model is a template for creating objects and contains all
 * the parameters for populating a class specified using Instancio API.
 *
 * <p>A model can be created using the {@link InstancioApi#toModel()} method.
 * An instance of a model can then be used for creating and customising objects
 * and other models.
 *
 * <p>Example:
 * <pre>{@code
 * Model<Person> simpsons = Instancio.of(Person.class)
 *     .set(field(Person::getLastName), "Simpson")
 *     .set(field(Address::getCity), "Springfield")
 *     .generate(field(Person::getAge), gen -> gen.ints().range(40, 50))
 *     .toModel();
 *
 * Person homer = Instancio.of(simpsons)
 *     .set(field(Person::getFirstName), "Homer")
 *     .create();
 *
 * Person marge = Instancio.of(simpsons)
 *     .set(field(Person::getFirstName), "Marge")
 *     .create();
 * }</pre>
 *
 * @param <T> the type of object encapsulated by this model
 * @since 1.0.1
 */
@SuppressWarnings("unused")
public interface Model<T> {
}

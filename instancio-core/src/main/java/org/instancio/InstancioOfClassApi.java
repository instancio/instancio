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
 * Instancio API for defining type parameters for generic classes.
 *
 * @param <T> type to create
 * @since 1.0.1
 */
public interface InstancioOfClassApi<T> extends InstancioApi<T> {

    /**
     * Method for supplying type parameters for generic classes.
     * <p>
     * Example:
     * <pre>{@code
     * List<Address> addresses = Instancio.of(List.class)
     *     .withTypeParameters(Address.class)
     *     .create();
     * }</pre>
     *
     * <p>This method can be used when the type parameters need
     * to be supplied dynamically at runtime, however it also
     * has a couple of limitations:</p>
     *
     * <ul>
     *   <li>its usage produces an unchecked assignment warning</li>
     *   <li>it cannot be used for nested generics, such as
     *       {@code Map<String, List<Integer>> }</li>
     * </ul>
     *
     * <p>The recommended approach for instantiating generic classes
     * is using {@link TypeToken TypeTokens}:
     *
     * <pre>{@code
     * List<Address> addresses = Instancio.create(new TypeToken<List<Address>>() {});
     * }</pre>
     *
     * @param types one or more type arguments
     * @return API builder reference
     * @see Instancio#create(TypeTokenSupplier)
     * @see Instancio#of(TypeTokenSupplier)
     * @since 1.0.1
     */
    InstancioApi<T> withTypeParameters(Class<?>... types);
}

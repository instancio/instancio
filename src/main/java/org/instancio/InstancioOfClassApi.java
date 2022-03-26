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
package org.instancio;

/**
 * Instancio API for defining type parameters for generic classes.
 *
 * @param <T> type being created
 */
public interface InstancioOfClassApi<T> extends InstancioApi<T> {

    /**
     * Method for supplying type parameters for generic classes.
     * <p>
     * Example:
     * <pre>{@code
     *     List<Address> addresses = Instancio.of(List.class)
     *             .withTypeParameters(Address.class)
     *             .create();
     * }</pre>
     *
     * @param type one or more types
     * @return API builder reference
     */
    InstancioApi<T> withTypeParameters(Class<?>... type);
}

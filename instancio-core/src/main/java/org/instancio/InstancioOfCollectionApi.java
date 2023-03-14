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
 * Instancio API for generating collections populated with random data.
 *
 * @param <C> collection type to create
 * @since 2.0.0
 */
public interface InstancioOfCollectionApi<C> extends InstancioApi<C> {

    /**
     * Specifies collection size to generate.
     *
     * <p>This method is syntactic sugar for:
     *
     * <pre>{@code
     *   List<Integer> result = Instancio.ofList(Integer.class)
     *       .generate(root(), gen -> gen.collection().size(50))
     *       .create();
     * }</pre>
     *
     * <p>Therefore, if you modify the collection generator via {@code root()}
     * selector (for example, to specify the collection's type) then you will
     * need to specify the size using the generator as well.
     *
     * <p>For example, instead of:
     *
     * <pre>{@code
     *   List<Integer> result = Instancio.ofList(Integer.class)
     *       .size(50)
     *       .generate(root(), gen -> gen.collection().subtype(LinkedList.class))
     *       .create();
     * }</pre>
     *
     * <p>use:
     *
     * <pre>{@code
     *   List<Integer> result = Instancio.ofList(Integer.class)
     *       .generate(root(), gen -> gen.collection().subtype(LinkedList.class).size(50))
     *       .create();
     * }</pre>
     *
     * @param size of the collection to generate
     * @return API builder reference
     * @since 2.0.0
     */
    InstancioOfCollectionApi<C> size(int size);

}
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
 * A result containing a created object and seed that was used for populating its values.
 *
 * @param <T> result type
 * @since 1.5.1
 */
public final class Result<T> {
    private final T object;
    private final int seed;

    public Result(final T object, final int seed) {
        this.object = object;
        this.seed = seed;
    }

    /**
     * Returns the created object.
     *
     * @return created object
     * @since 1.5.1
     */
    public T get() {
        return object;
    }

    /**
     * Returns the seed that was used to populate the created object.
     *
     * @return the seed
     * @since 1.5.1
     */
    public int getSeed() {
        return seed;
    }

    @Override
    public String toString() {
        return String.format("Result[seed=%s, object=%s]", seed, object);
    }
}

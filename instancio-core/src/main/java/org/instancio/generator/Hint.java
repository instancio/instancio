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
package org.instancio.generator;

/**
 * A hint that can be passed from a generator to the engine.
 * Hints can be used to fine-tune the behaviour of the engine with respect to
 * objects created by the generator (see {@link Generator#hints()}).
 *
 * @param <T> hint type
 * @see Generator
 * @see Hints
 * @see AfterGenerate
 * @since 2.0.0
 */
public interface Hint<T extends Hint<T>> {

    /**
     * Returns the type of this hint.
     *
     * @return hint's type
     * @since 2.0.0
     */
    @SuppressWarnings("unchecked")
    default Class<T> type() {
        return (Class<T>) getClass();
    }
}

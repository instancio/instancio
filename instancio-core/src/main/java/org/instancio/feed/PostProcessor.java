/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.feed;

import org.instancio.Random;
import org.instancio.documentation.ExperimentalApi;
import org.instancio.feed.FeedSpecAnnotations.WithPostProcessor;

/**
 * An interface for applying additional processing
 * to data returned by a spec method of a {@link Feed}.
 *
 * @param <T> the type of value being processed
 * @see WithPostProcessor
 * @since 5.0.0
 */
@ExperimentalApi
@FunctionalInterface
public interface PostProcessor<T> {

    /**
     * Processes the given {@code input} value.
     *
     * @param input  value to process
     * @param random for randomising the output, if necessary
     * @return a processed result
     * @since 5.0.0
     */
    @ExperimentalApi
    T process(T input, Random random);
}

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
package org.instancio.quickcheck.api.artbitrary;

import java.util.stream.BaseStream;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.quickcheck.internal.arbitrary.ArbitraryStream;

/**
 * An extension point that allows providing a custom implementation strategy to 
 * generate samples for a property under the test.
 * @param <T> the type of the property 
 */
@ExperimentalApi
@FunctionalInterface
public interface Arbitrary<T> {
    /**
     * Returns the generator instance of a property under the test
     * @return the generator instance
     */
    ArbitraryGenerator<T> generator();

    /**
     * Creates the {@link Arbitrary} instance from the {@link BaseStream} instance.
     * @param <A> the type of the property 
     * @param stream the source {@link BaseStream} instance
     * @return an {@link Arbitrary} instance
     */
    static <A> Arbitrary<A> fromStream(BaseStream<A, ?> stream) {
        return () -> new ArbitraryStream<A>(stream);
    }
}

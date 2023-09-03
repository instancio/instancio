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
package org.instancio.quickcheck.internal.arbitrary;

import java.util.Iterator;
import java.util.stream.BaseStream;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.quickcheck.api.artbitrary.ArbitraryGenerator;
import org.junit.platform.commons.JUnitException;

@ExperimentalApi
public class ArbitraryStream<T> implements ArbitraryGenerator<T> {
    private final Iterator<T> iterator;

    public ArbitraryStream(BaseStream<T, ?> stream) {
        this.iterator = stream.iterator();
    }

    @Override
    public T generate() {
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            throw new JUnitException("The stream backed the Arbitrary generation "
                + "has no more elements to generate. Please make sure the stream could generate "
                + "at least the samples count.");
        }
    }

}

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
package org.instancio.jpa.generator;

import java.util.Objects;
import org.instancio.Random;
import org.instancio.generator.Generator;

public abstract class AbstractNumberSequenceGenerator<SELF extends AbstractNumberSequenceGenerator<SELF, T>, T extends Number> implements Generator<T> {
    private T start;
    private T end;
    private T nextValue;

    public AbstractNumberSequenceGenerator(T start, T end) {
        this.start = start;
        this.end = end;
        this.nextValue = start;
    }

    @Override
    public T generate(Random random) {
        T currentValue = nextValue;
        T nextVal = increment(nextValue);
        if (isGreater(nextVal, end)) {
            nextVal = start;
        }
        nextValue = nextVal;
        return currentValue;
    }

    protected abstract T increment(T value);

    protected abstract boolean isGreater(T v1, T v2);

    public SELF start(T start) {
        Objects.requireNonNull(start);
        this.start = start;
        this.nextValue = start;
        return (SELF) this;
    }

    public SELF end(T end) {
        Objects.requireNonNull(end);
        if (isGreater(this.start, end)) {
            throw new IllegalArgumentException("Sequence end must be greater or equal to sequence start");
        }
        this.end = end;
        return (SELF) this;
    }
}

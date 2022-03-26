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
package org.instancio.internal;

import javax.annotation.Nullable;
import java.util.StringJoiner;

class GeneratorResult<T> {
    private static final GeneratorResult<Void> NULL_RESULT = new GeneratorResult<>(
            null, GeneratedHints.builder().ignoreChildren(true).build());

    private final T value;
    private final GeneratedHints generatedHints;

    private GeneratorResult(@Nullable final T value, @Nullable final GeneratedHints generatedHints) {
        this.value = value;
        this.generatedHints = generatedHints;
    }

    /**
     * This result indicates that a null was generated, therefore the target's value should
     * be set to null. If the target is a field with a pre-initialised default value, it will be
     * overwritten with a null value.
     * <p>
     * An actual {@code null} {@code GeneratorResult} means that the target field will be ignored
     * (it would retain its default value, if any).
     *
     * @return null result
     */
    static GeneratorResult<Void> nullResult() {
        return NULL_RESULT;
    }

    static <T> GeneratorResult<T> create(@Nullable final T value) {
        return new GeneratorResult<>(value, null);
    }

    static <T> GeneratorResult<T> create(@Nullable final T value, @Nullable final GeneratedHints hints) {
        return new GeneratorResult<>(value, hints);
    }

    T getValue() {
        return value;
    }

    boolean ignoreChildren() {
        return generatedHints != null && generatedHints.ignoreChildren();
    }

    GeneratedHints getHints() {
        return generatedHints;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GeneratorResult.class.getSimpleName() + "[", "]")
                .add("value=" + value)
                .add("hints=" + generatedHints)
                .toString();
    }
}

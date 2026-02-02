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
package org.instancio.test.support.pojo.containers;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is a container class that must be created via Builder.build()
 * and populated by calling the builder's add() method, i.e.:
 *
 * <pre>{@code
 *    BuildableList.<String>builder()
 *          .add("one")
 *          .add("two")
 *          .build();
 * }</pre>
 * <p>
 * Container classes are populated via "add-to-container" function rather than
 * by setting its fields via reflection.
 */
public class BuildableList<E> {

    private final List<E> elements;
    private final Object originalElements;

    private BuildableList(final Builder<E> builder) {
        elements = builder.elements;
        originalElements = elements;
    }

    /**
     * We verify that originalElements elements is still the same instance
     * because this class must not be populated by setting its fields via reflection.
     */
    public void assertOriginalListNotOverwritten() {
        assertThat(elements).isNotNull().isSameAs(originalElements);
    }

    public List<E> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static <E> Builder<E> builder() {
        return new Builder<>();
    }

    public static final class Builder<E> {
        private final List<E> elements = new ArrayList<>();

        private Builder() {
        }

        // used via reflection
        @SuppressWarnings("unused")
        public static <E> Builder<E> builder() {
            return new Builder<>();
        }

        public Builder<E> add(final E element) {
            this.elements.add(element);
            return this;
        }

        public BuildableList<E> build() {
            return new BuildableList<>(this);
        }
    }
}

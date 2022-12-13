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
package org.instancio.internal.nodes;

/**
 * @since 1.5.0
 */
public enum NodeKind {

    /**
     * Fallback kind. A node of this kind could represent either a POJO class
     * or a JDK type that is considered to be a leaf node, such as
     * {@code String}, {@code LocalDate}, {@code BigDecimal}, etc.
     */
    DEFAULT,

    /**
     * Represents an array.
     */
    ARRAY,

    /**
     * {@code java.util.Collection}
     */
    COLLECTION,

    /**
     * {@code java.util.Map}
     */
    MAP,

    /**
     * {@code java.lang.Record}
     */
    RECORD,

    /**
     * Represents a data structure whose child nodes are not fields,
     * but type arguments.
     *
     * @since 2.0.0
     */
    CONTAINER
}

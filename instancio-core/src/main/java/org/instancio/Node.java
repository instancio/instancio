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

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Represents a single {@code Node} of a node hierarchy
 * created for a given {@link Type}. A node can represent:
 *
 * <ul>
 *   <li>the root class</li>
 *   <li>field of a class</li>
 *   <li>an element of a collection, or in general, a generic type argument,
 *       for example, the type of an {@link Optional} value</li>
 * </ul>
 *
 * @since 2.11.0
 */
public interface Node {

    /**
     * Returns the target class of this node.
     *
     * <p>If this node represents a field, generally the target class
     * will be the same as {@link Field#getType()}. However, there are
     * cases where the target class may differ, for example:
     *
     * <ul>
     *   <li>when a subtype is specified, <b>then</b>
     *       the node's target class will represent the subtype.</li>
     *   <li>when the node represents a generic type, <b>then</b>
     *       the target class will represent the resolved type argument.</li>
     * </ul>
     *
     * @return target class of this node, never {@code null}
     * @since 2.11.0
     */
    Class<?> getTargetClass();

    /**
     * Returns the field of this node, if available.
     *
     * @return field of this node, or {@code null} if the node has no field
     * @since 2.11.0
     */
    Field getField();

    Class<?> getParentTargetClass();
}

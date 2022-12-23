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
package org.instancio.internal.assigners;

import org.instancio.documentation.InternalApi;
import org.instancio.internal.nodes.Node;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Encapsulates the details of how values are assigned to an object's fields.
 *
 * @since 2.1.0
 */
@InternalApi
public interface Assigner {

    /**
     * Assigns given value to the target object's field.
     * The assignment itself can be performed by:
     *
     * <li>assigning the argument directly to the {@link Field}</li>
     * <li>setting the argument via a setter {@link Method}</li>
     *
     * @param node   contains information about the target field.
     * @param target that has the field to be assigned.
     * @param value  value to assign, can be {@code null};
     *               if the target field is a primitive,
     *               then {@code null} value is simply ignored.
     */
    void assign(Node node, Object target, Object value);
}

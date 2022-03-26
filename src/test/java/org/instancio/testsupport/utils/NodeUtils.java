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
package org.instancio.testsupport.utils;

import org.instancio.internal.model.Node;

import static org.assertj.core.api.Fail.fail;

public class NodeUtils {

    private NodeUtils() {
        // non-instantiable
    }

    public static Node getChildNode(Node parent, String fieldName) {
        return parent.getChildren().stream()
                .filter(it -> it.getField() != null && it.getField().getName().equals(fieldName))
                .findAny()
                .orElseGet(() -> fail("Expected child field node '%s' ", fieldName));
    }
}

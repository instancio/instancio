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
package org.instancio.internal.handlers;

import org.instancio.generator.Generator;
import org.instancio.internal.generator.lang.StringGenerator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.StringUtils;

class StringPrefixingPostProcessor implements GeneratedValuePostProcessor {

    private final boolean stringFieldPrefixEnabled;

    StringPrefixingPostProcessor(final boolean stringFieldPrefixEnabled) {
        this.stringFieldPrefixEnabled = stringFieldPrefixEnabled;
    }

    @Override
    public Object process(final Object value, final InternalNode node, final Generator<?> generator) {
        if (stringFieldPrefixEnabled
                && node.getField() != null
                && generator.getClass() == StringGenerator.class
                && !StringUtils.isEmpty((String) value)) {

            return node.getField().getName() + "_" + value;
        }
        return value;
    }
}

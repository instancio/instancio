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
package org.instancio.spi;

import lombok.Data;
import org.instancio.internal.spi.InternalServiceProvider;

import java.lang.reflect.Field;

public class CoreTestsInternalSpi implements InternalServiceProvider {

    public static @Data class IgnoredAndPrunedFieldPojo {
        public static final String IGNORED_FIELD_NAME = "thisFieldShouldBeIgnored";
        public static final String PRUNED_FIELD_NAME = "thisFieldShouldBePruned";

        private String thisFieldShouldBeIgnored;
        private String thisFieldShouldBePruned;
    }

    @Override
    public InternalNodeFilter getNodeFilter() {
        return node -> {
            final Field field = node.getField();

            if (field != null) {
                if (field.getName().equals(IgnoredAndPrunedFieldPojo.IGNORED_FIELD_NAME)) {
                    return InternalNodeFilter.Decision.IGNORE;
                }
                if (field.getName().equals(IgnoredAndPrunedFieldPojo.PRUNED_FIELD_NAME)) {
                    return InternalNodeFilter.Decision.PRUNE;
                }
            }
            return InternalNodeFilter.Decision.KEEP;
        };
    }
}

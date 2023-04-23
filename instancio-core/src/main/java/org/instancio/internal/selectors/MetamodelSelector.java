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
package org.instancio.internal.selectors;

import org.instancio.Selector;

/**
 * Selector for use in generated metamodel classes only.
 */
@Deprecated
public final class MetamodelSelector extends SelectorImpl {

    private MetamodelSelector(final Class<?> targetClass, final String fieldName) {
        super(targetClass, fieldName, false);
    }

    public static Selector of(final Class<?> targetClass, final String fieldName) {
        return new MetamodelSelector(targetClass, fieldName);
    }

    /**
     * Since selector in a metamodel class is a static final field, the {@code stackTraceHolder}
     * property it contains does not indicate where the metamodel property is used in client code.
     * This method creates a copy of the selector with a new {@code stackTraceHolder}, so that
     * correct location can be output in the "unused selectors" error message.
     *
     * @return a copy of this selector containing a new {@code stackTraceHolder}
     */
    public Selector copyWithNewStackTraceHolder() {
        return SelectorImpl.builder(this).stackTraceHolder(new Throwable()).build();
    }
}

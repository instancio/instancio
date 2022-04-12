/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.processor;

import java.util.List;

public class MetaModelClass {

    private final String name;
    private final String simpleName;
    private final String packageName;
    private final List<String> fieldNames;

    public MetaModelClass(final String name, final List<String> fieldNames) {
        final int idx = name.lastIndexOf('.');
        this.name = name;
        this.packageName = idx == -1 ? null : name.substring(0, idx);
        this.simpleName = idx == -1 ? name : name.substring(idx + 1);
        this.fieldNames = fieldNames;

    }

    /**
     * Returns fully qualified class name, as returned by {@link Class#getName()}.
     *
     * @return fully qualified class name
     */
    public String getName() {
        return name;
    }

    /**
     * Simple name, as returned by {@link Class#getSimpleName()}.
     *
     * @return simple name
     */
    public String getSimpleName() {
        return simpleName;
    }

    /**
     * Returns package name.
     *
     * @return package name, or {@code null} if none
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Returns a list of declared field names.
     *
     * @return declared field names
     */
    public List<String> getFieldNames() {
        return fieldNames;
    }

    @Override
    public String toString() {
        return name;
    }
}

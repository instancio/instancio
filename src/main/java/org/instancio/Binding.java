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
package org.instancio;

import javax.annotation.Nullable;

public class Binding {
    private enum BindingType {TYPE, FIELD}

    private final BindingType bindingType;
    private final Class<?> targetType;
    private final String fieldName;

    private Binding(final BindingType bindingType,
                    @Nullable final Class<?> targetType,
                    @Nullable final String fieldName) {

        this.bindingType = bindingType;
        this.targetType = targetType;
        this.fieldName = fieldName;
    }

    public static Binding fieldBinding(@Nullable final Class<?> targetType, final String fieldName) {
        return new Binding(BindingType.FIELD, targetType, fieldName);
    }

    public static Binding fieldBinding(final String fieldName) {
        return new Binding(BindingType.FIELD, null, fieldName);
    }

    public static Binding typeBinding(final Class<?> targetType) {
        return new Binding(BindingType.TYPE, targetType, null);
    }

    public boolean isFieldBinding() {
        return bindingType == BindingType.FIELD;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    public String getFieldName() {
        return fieldName;
    }
}

/*
 * Copyright 2022-2025 the original author or authors.
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

class MemberPair {
    private final Field field;
    private final Method setter;

    MemberPair(final Field field, final Method setter) {
        this.field = field;
        this.setter = setter;
    }

    Field getField() {
        return field;
    }

    Method getSetter() {
        return setter;
    }

    @Override
    public String toString() {
        return String.format("MemberPair[field=%s, setter=%s]", field, setter);
    }
}

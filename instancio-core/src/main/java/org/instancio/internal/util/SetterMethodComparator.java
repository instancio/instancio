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

package org.instancio.internal.util;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * This comparator assumes the method has exactly one parameter.
 */
public final class SetterMethodComparator implements Comparator<Method> {

    @Override
    public int compare(final Method a, final Method b) {
        int cmp = a.getName().compareTo(b.getName());
        return cmp == 0
                ? a.getParameterTypes()[0].getTypeName().compareTo(b.getParameterTypes()[0].getTypeName())
                : cmp;
    }
}
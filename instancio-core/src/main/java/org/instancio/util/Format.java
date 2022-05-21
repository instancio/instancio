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
package org.instancio.util;

import org.instancio.Scope;
import org.instancio.internal.selectors.ScopeImpl;

import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;

public final class Format {
    private Format() {
        // non-instantiable
    }

    public static String scopes(final List<Scope> scopes) {
        return scopes.stream()
                .map(ScopeImpl.class::cast)
                .map(s -> s.getField() == null
                        ? String.format("scope(%s)", s.getTargetClass().getSimpleName())
                        : String.format("scope(%s, \"%s\")", s.getTargetClass().getSimpleName(), s.getField().getName()))
                .collect(joining(", "));
    }

    public static String getTypeVariablesCsv(final Class<?> klass) {
        return Arrays.stream(klass.getTypeParameters())
                .map(TypeVariable::getName)
                .collect(joining(", "));
    }

    public static String paramsToCsv(final List<Class<?>> rootTypeParameters) {
        return rootTypeParameters.stream()
                .map(klass -> klass.getSimpleName() + ".class")
                .collect(joining(", "));
    }
}

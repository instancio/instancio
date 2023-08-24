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
package org.example.spi;

import org.instancio.Node;
import org.instancio.spi.InstancioServiceProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * A custom setter resolver for POJOs with fields prefixed with underscore.
 * In addition, this resolver matches methods by name only (ignoring the argument type).
 */
public class CustomSetterMethodResolver implements InstancioServiceProvider {

    private static class SetterMethodResolverImpl implements SetterMethodResolver {
        @Override
        public Method getSetter(final Node node) {
            final Field field = node.getField();

            // discard the '_' prefix
            final char[] ch = field.getName().substring(1).toCharArray();
            ch[0] = Character.toUpperCase(ch[0]);

            final String methodName = "set" + new String(ch);

            return Arrays.stream(field.getDeclaringClass().getDeclaredMethods())
                    .filter(m -> m.getName().equals(methodName))
                    .findFirst()
                    .orElse(null);
        }
    }

    @Override
    public SetterMethodResolver getSetterMethodResolver() {
        return new SetterMethodResolverImpl();
    }
}
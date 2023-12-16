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
package org.example.spi.internal;

import org.instancio.internal.spi.InternalServiceProvider;
import org.instancio.internal.util.ReflectionUtils;

/**
 * Custom SPI for resolving field from method reference selectors, for exmaple
 * {@code field(Pojo::getFoo)}.
 *
 * <p>This SPI maps {@code getFoo} to field name {@code "foo_"}.
 */
public class CustomGetterMethodFieldResolver implements InternalServiceProvider {

    @Override
    public InternalGetterMethodFieldResolver getGetterMethodFieldResolver() {
        return (declaringClass, methodName) -> {
            if (methodName.startsWith("get")) {
                final char[] ch = methodName.toCharArray();
                ch[3] = Character.toLowerCase(ch[3]);

                // discard "get" prefix and append underscore
                final String fieldName = new String(ch, 3, ch.length - 3) + "_";
                return ReflectionUtils.getFieldOrNull(declaringClass, fieldName);
            }
            return null;
        };
    }
}

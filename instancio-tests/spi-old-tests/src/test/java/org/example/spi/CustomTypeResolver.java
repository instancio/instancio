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

import org.instancio.spi.TypeResolver;
import org.instancio.test.support.pojo.basic.StringHolderAlternativeImpl;
import org.instancio.test.support.pojo.generics.basic.ItemAlternativeImpl;
import org.instancio.test.support.pojo.interfaces.ItemInterface;
import org.instancio.test.support.pojo.interfaces.StringHolderInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CustomTypeResolver implements TypeResolver {

    private static final Map<Class<?>, Class<?>> TYPE_MAP = new HashMap<Class<?>, Class<?>>() {{
        put(StringHolderInterface.class, StringHolderAlternativeImpl.class);
        put(ItemInterface.class, ItemAlternativeImpl.class);
    }};

    @Override
    public Optional<Class<?>> resolve(final Class<?> type) {
        return Optional.ofNullable(TYPE_MAP.get(type));
    }
}

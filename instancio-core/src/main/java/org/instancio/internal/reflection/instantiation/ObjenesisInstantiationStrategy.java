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
package org.instancio.internal.reflection.instantiation;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Delegates instantiation to Objenesis to instantiate a class without invoking
 * constructors. This handles cases where a class does not have a default constructor.
 */
public class ObjenesisInstantiationStrategy implements InstantiationStrategy {

    private final Objenesis objenesis = new ObjenesisStd();
    private final Map<Class<?>, ObjectInstantiator<?>> cache = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createInstance(final Class<T> klass) {
        ObjectInstantiator<?> instantiator = cache.computeIfAbsent(klass, objenesis::getInstantiatorOf);
        return (T) instantiator.newInstance();
    }

}

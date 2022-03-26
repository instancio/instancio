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
package org.instancio.internal.reflection.instantiation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

public class NoArgumentConstructorInstantiationStrategy implements InstantiationStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(NoArgumentConstructorInstantiationStrategy.class);

    @Override
    @SuppressWarnings({"unchecked", "java:S3011"})
    public <T> T createInstance(final Class<T> klass) {
        try {
            final Constructor<?> ctor = klass.getDeclaredConstructor();
            ctor.setAccessible(true);
            return (T) ctor.newInstance();
        } catch (Exception ex) {
            throw new InstantiationStrategyException("Error instantiating " + klass, ex);
        }
    }

}

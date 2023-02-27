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
package org.instancio.internal.instantiation;

import org.instancio.internal.util.Sonar;

import java.lang.reflect.Constructor;

class NoArgumentConstructorInstantiationStrategy implements InstantiationStrategy {

    @Override
    @SuppressWarnings({"unchecked", Sonar.ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED})
    public <T> T createInstance(final Class<T> klass) {
        try {
            final Constructor<?> ctor = getDefaultConstructor(klass);
            if (ctor == null) {
                return null;
            }
            ctor.setAccessible(true);
            return (T) ctor.newInstance();
        } catch (Exception ex) {
            throw new InstantiationStrategyException("Error instantiating " + klass, ex);
        }
    }

    private static Constructor<?> getDefaultConstructor(final Class<?> klass) {
        for (Constructor<?> ctor : klass.getDeclaredConstructors()) {
            if (ctor.getParameterCount() == 0) {
                return ctor;
            }
        }
        return null;
    }

}

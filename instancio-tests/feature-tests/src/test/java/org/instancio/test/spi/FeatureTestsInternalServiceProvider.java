/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.test.spi;

import org.instancio.internal.spi.InternalServiceProvider;
import org.instancio.test.support.pojo.containers.BuildableList;
import org.instancio.test.support.pojo.containers.OptionalLike;

import java.util.List;
import java.util.function.Function;

public class FeatureTestsInternalServiceProvider implements InternalServiceProvider {

    @Override
    public InternalContainerFactoryProvider getContainerFactoryProvider() {
        return new InternalContainerFactoryProvider() {
            @Override
            public <T, R> Function<T, R> getMappingFunction(final Class<R> type, final List<Class<?>> typeArguments) {
                return null; // none yet
            }

            @Override
            public boolean isContainer(final Class<?> type) {
                return type == BuildableList.class || type == OptionalLike.class;
            }
        };
    }
}

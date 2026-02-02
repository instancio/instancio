/*
 * Copyright 2022-2026 the original author or authors.
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

import org.instancio.test.support.pojo.basic.IntegerHolderWithoutDefaultConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnsafeInstantiationStrategyTest {

    private static final Class<?> CLASS_WITHOUT_DEFAULT_CTOR = IntegerHolderWithoutDefaultConstructor.class;

    @Spy
    @InjectMocks
    private UnsafeInstantiationStrategy strategy;

    @Test
    void createInstanceSuccessful() {
        assertThat(strategy.createInstance(CLASS_WITHOUT_DEFAULT_CTOR)).isNotNull();
    }

    @Test
    void shouldReturnNullIfUnsafeIsNotAvailable() {
        when(strategy.isUnsafeAvailable()).thenReturn(false);
        assertThat(strategy.createInstance(CLASS_WITHOUT_DEFAULT_CTOR)).isNull();
    }
}

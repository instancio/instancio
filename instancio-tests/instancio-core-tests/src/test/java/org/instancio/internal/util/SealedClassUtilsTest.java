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
package org.instancio.internal.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class SealedClassUtilsTest {

    //@formatter:off
    private sealed interface SealedInterfaceA permits SealedInterfaceB, SealedInterfaceImpl1 {}
    private sealed interface SealedInterfaceB extends SealedInterfaceA permits SealedAbstractClassB {}
    private static sealed abstract class SealedAbstractClassB implements SealedInterfaceB permits SealedInterfaceImpl2 {}
    private static final class SealedInterfaceImpl1 implements SealedInterfaceA {}
    private static final class SealedInterfaceImpl2 extends SealedAbstractClassB {}
    private static sealed class SealedConcreteClass permits SealedConcreteSubClass {}
    private static final class SealedConcreteSubClass extends SealedConcreteClass {}
    //@formatter:on

    @Test
    void getSealedClassImplementations() {
        assertThat(SealedClassUtils.getSealedClassImplementations(SealedInterfaceA.class))
                .containsExactlyInAnyOrder(SealedInterfaceImpl1.class, SealedInterfaceImpl2.class);

        assertThat(SealedClassUtils.getSealedClassImplementations(SealedInterfaceB.class))
                .containsOnly(SealedInterfaceImpl2.class);

        assertThat(SealedClassUtils.getSealedClassImplementations(SealedAbstractClassB.class))
                .containsOnly(SealedInterfaceImpl2.class);
    }

    @ValueSource(classes = {SealedInterfaceA.class, SealedInterfaceB.class, SealedAbstractClassB.class})
    @ParameterizedTest
    void isSealedAbstractTypeTrue(final Class<?> klass) {
        assertThat(SealedClassUtils.isSealedAbstractType(klass)).isTrue();
    }

    @ValueSource(classes = {SealedInterfaceImpl1.class, SealedConcreteClass.class, SealedConcreteSubClass.class})
    @ParameterizedTest
    void isSealedAbstractTypeFalse(final Class<?> klass) {
        assertThat(SealedClassUtils.isSealedAbstractType(klass)).isFalse();
    }
}

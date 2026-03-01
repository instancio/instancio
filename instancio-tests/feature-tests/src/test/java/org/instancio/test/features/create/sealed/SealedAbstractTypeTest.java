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
package org.instancio.test.features.create.sealed;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.util.Constants;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

/**
 * NOTE: this test fails in IntelliJ, run it using Maven
 */
@ExtendWith(InstancioExtension.class)
class SealedAbstractTypeTest {

    //@formatter:off
    private sealed interface SealedInterfaceA permits SealedInterfaceB, SealedInterfaceImpl1 {}
    private sealed interface SealedInterfaceB extends SealedInterfaceA permits SealedAbstractClassB {}
    private abstract static sealed class SealedAbstractClassB implements SealedInterfaceB permits SealedInterfaceImpl2 {}
    private static final class SealedInterfaceImpl1 implements SealedInterfaceA { @Nullable UUID uuid; }
    private static final class SealedInterfaceImpl2 extends SealedAbstractClassB { @Nullable String string; @Nullable Long num; }
    //@formatter:on

    /**
     * If no explicit subtype is specified, should use a random implementation class.
     */
    @Test
    void abstractTypeWithoutSubtype_shouldUseRandomImplementationClass() {
        final List<SealedInterfaceA> results = Instancio.stream(SealedInterfaceA.class)
                .limit(Constants.SAMPLE_SIZE_DD)
                .toList();

        assertThat(results)
                .filteredOn(r -> r.getClass() == SealedInterfaceImpl1.class)
                .isNotEmpty()
                .allSatisfy(SealedAbstractTypeTest::assertImpl1);

        assertThat(results)
                .filteredOn(r -> r.getClass() == SealedInterfaceImpl2.class)
                .isNotEmpty()
                .allSatisfy(SealedAbstractTypeTest::assertImpl2);
    }

    @Test
    void abstractTypeWithSubtype_shouldUseTheSpecifiedSubtype() {
        final Class<?> expectedSubtype = Instancio.gen().oneOf(SealedInterfaceImpl1.class, SealedInterfaceImpl2.class).get();

        final List<SealedInterfaceA> results = Instancio.of(SealedInterfaceA.class)
                .subtype(all(SealedInterfaceA.class), expectedSubtype)
                .stream()
                .limit(Constants.SAMPLE_SIZE_DD)
                .toList();

        assertThat(results).as("Should contain only instances of %s", expectedSubtype)
                .hasSize(Constants.SAMPLE_SIZE_DD)
                .hasOnlyElementsOfType(expectedSubtype);

        if (expectedSubtype == SealedInterfaceImpl1.class) {
            assertThat(results).allSatisfy(SealedAbstractTypeTest::assertImpl1);
        } else {
            assertThat(results).allSatisfy(SealedAbstractTypeTest::assertImpl2);
        }
    }

    @ValueSource(classes = {SealedInterfaceB.class, SealedAbstractClassB.class})
    @ParameterizedTest
    void abstractTypeWithSingleImplementation(final Class<?> sealedAbstractType) {
        final Object result = Instancio.create(sealedAbstractType);

        assertImpl2(result);
    }

    @Test
    void collectionOfSealedAbstractType_shouldUseRandomImplementationClass() {
        final List<SealedInterfaceA> results = Instancio.ofList(SealedInterfaceA.class)
                .size(Constants.SAMPLE_SIZE_DD)
                .create();

        final Set<Class<?>> elementTypes = results.stream()
                .map(SealedInterfaceA::getClass)
                .collect(Collectors.toSet());

        // A random type is picked, but once picked, all elements should be of the same type
        assertThat(elementTypes).hasSize(1);

        assertThat(results).hasSize(Constants.SAMPLE_SIZE_DD).allSatisfy(elem -> {
            if (elem instanceof SealedInterfaceImpl1) {
                assertImpl1(elem);
            } else {
                assertImpl2(elem);
            }
        });
    }

    private static void assertImpl1(final Object actual) {
        assertThat(actual).isExactlyInstanceOf(SealedInterfaceImpl1.class);
        assertThat(((SealedInterfaceImpl1) actual).uuid).isNotNull();
    }

    private static void assertImpl2(final Object actual) {
        assertThat(actual).isExactlyInstanceOf(SealedInterfaceImpl2.class);

        SealedInterfaceImpl2 impl = (SealedInterfaceImpl2) actual;
        assertThat(impl.string).isNotBlank();
        assertThat(impl.num).isPositive();
    }
}

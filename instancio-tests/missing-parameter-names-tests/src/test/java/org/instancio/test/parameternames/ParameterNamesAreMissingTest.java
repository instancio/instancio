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
package org.instancio.test.parameternames;

import org.instancio.internal.util.ConstructorParameterNames;
import org.instancio.test.parameternames.NoDebugInfoPojos.AllArgsCtor;
import org.instancio.test.parameternames.NoDebugInfoPojos.PartialArgsCtor;
import org.instancio.test.parameternames.NoDebugInfoPojos.PersonRecord;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guard for this module's premise.
 *
 * <p>Every other test here asserts what Instancio does when constructor
 * parameter names are unavailable. If the module's compiler configuration ever
 * drifts and the names become resolvable, those tests would keep passing while
 * silently verifying nothing. This test fails instead.
 *
 * <p>The test classes themselves <i>are</i> compiled with debug information, so
 * this also confirms that the fixtures in {@code src/main/java} are the ones
 * lacking it.
 */
class ParameterNamesAreMissingTest {

    @Test
    void fixtureParameterNamesAreNotResolvable() throws NoSuchMethodException {
        final Constructor<?> allArgs = AllArgsCtor.class.getDeclaredConstructor(String.class, int.class);
        final Constructor<?> partialArgs = PartialArgsCtor.class.getDeclaredConstructor(String.class, String.class);

        assertThat(ConstructorParameterNames.resolve(allArgs))
                .as("Fixtures must be compiled without debug information; see this module's pom.xml")
                .isNull();

        assertThat(ConstructorParameterNames.resolve(partialArgs))
                .as("Fixtures must be compiled without debug information; see this module's pom.xml")
                .isNull();
    }

    /**
     * Records carry a {@code Record} attribute naming their components, which
     * is not debug information, so their names survive regardless.
     */
    @Test
    void recordComponentNamesAreUnaffected() {
        assertThat(PersonRecord.class.getRecordComponents())
                .extracting(java.lang.reflect.RecordComponent::getName)
                .containsExactly("name", "age");
    }

    /**
     * Control: this test class is compiled with debug information, so names
     * declared here must resolve. Without this, the assertions above would also
     * pass if resolution were broken outright.
     */
    @Test
    void namesDeclaredInTestSourcesAreResolvable() throws NoSuchMethodException {
        final Constructor<?> ctor = CompiledWithDebugInfo.class
                .getDeclaredConstructor(String.class, int.class);

        assertThat(ConstructorParameterNames.resolve(ctor)).containsExactly("value", "number");
    }

    @SuppressWarnings("unused")
    private static class CompiledWithDebugInfo {
        CompiledWithDebugInfo(final String value, final int number) {
            // no-op
        }
    }
}

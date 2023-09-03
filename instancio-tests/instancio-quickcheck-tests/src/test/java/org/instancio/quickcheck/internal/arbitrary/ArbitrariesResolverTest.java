/*
 *  Copyright 2022-2023 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.quickcheck.internal.arbitrary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.instancio.quickcheck.api.ForAll;
import org.instancio.quickcheck.api.artbitrary.Arbitrary;
import org.instancio.quickcheck.api.artbitrary.ArbitraryGenerator;
import org.instancio.quickcheck.internal.config.DefaultInstancioQuickcheckConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.platform.engine.ConfigurationParameters;

public class ArbitrariesResolverTest {
    private final DefaultInstancioQuickcheckConfiguration configuration = new DefaultInstancioQuickcheckConfiguration(new ConfigurationParameters() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public Set<String> keySet() {
            return Collections.emptySet();
        }

        @Override
        public Optional<Boolean> getBoolean(String key) {
            return Optional.empty();
        }
        
        @Override
        public Optional<String> get(String key) {
            return Optional.empty();
        }
    });
    private ArbitrariesResolver resolver;

    @Test
    @DisplayName("Attempt to resolve missing arbitrary generator should fail")
    void resolveMissingArbitrary() throws NoSuchMethodException, SecurityException {
        final Method method = QuickcheckTest.class.getMethod("unknown", Integer.class);
        final List<Parameter> parameters = Arrays.asList(method.getParameters());

        resolver = new ArbitrariesResolver(parameters, configuration);
        assertThrows(ParameterResolutionException.class, () -> resolver.resolve(new QuickcheckTest()));
    }

    @Test
    @DisplayName("Attempt to resolve non-instantiable arbitrary generator should fail")
    void resolveNoninstantiableArbitrary() throws NoSuchMethodException, SecurityException {
        final Method method = QuickcheckTest.class.getMethod("noninstantiable", Integer.class);
        final List<Parameter> parameters = Arrays.asList(method.getParameters());

        resolver = new ArbitrariesResolver(parameters, configuration);
        assertThrows(ParameterResolutionException.class, () -> resolver.resolve(new QuickcheckTest()));
    }

    @Test
    @DisplayName("Attempt to resolve nullable arbitrary generator should fail")
    void resolveNullableArbitrary() throws NoSuchMethodException, SecurityException {
        final Method method = QuickcheckTest.class.getMethod("nullable", Integer.class);
        final List<Parameter> parameters = Arrays.asList(method.getParameters());

        resolver = new ArbitrariesResolver(parameters, configuration);
        assertThrows(ParameterResolutionException.class, () -> resolver.resolve(new QuickcheckTest()));
    }

    private static class QuickcheckTest {
        public void unknown(@ForAll("odds") Integer i) {
            assertThat(i % 2).isEqualTo(0);
        }
        
        public void noninstantiable(@ForAll("noninstantiable") Integer i) {
            assertThat(i % 2).isEqualTo(0);
        }

        public void nullable(@ForAll("nullable") Integer i) {
            assertThat(i % 2).isEqualTo(0);
        }

        Arbitrary<Integer> nullable() {
            return () -> null;
        }

        Arbitrary<Integer> noninstantiable(ArbitraryGenerator<Integer> generator) {
            return () -> generator;
        }
    }
}

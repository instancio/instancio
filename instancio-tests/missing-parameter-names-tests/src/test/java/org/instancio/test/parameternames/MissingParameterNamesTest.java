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

import org.instancio.Instancio;
import org.instancio.settings.InstantiationStrategies;
import org.instancio.settings.Keys;
import org.instancio.test.parameternames.NoDebugInfoPojos.AllArgsAndNoArgsCtor;
import org.instancio.test.parameternames.NoDebugInfoPojos.AllArgsCtor;
import org.instancio.test.parameternames.NoDebugInfoPojos.NoArgsOnly;
import org.instancio.test.parameternames.NoDebugInfoPojos.PartialArgsCtor;
import org.instancio.test.parameternames.NoDebugInfoPojos.PersonRecord;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.settings.InstantiationStrategy.ALL_ARGS;
import static org.instancio.settings.InstantiationStrategy.BYPASS_CONSTRUCTOR;
import static org.instancio.settings.InstantiationStrategy.NO_ARGS;

/**
 * Behaviour when constructor parameter names cannot be resolved.
 *
 * <p>Since parameters are matched to fields by name, a value-passing
 * constructor cannot be used at all if its parameter names are unavailable.
 * Instancio falls back to the next configured strategy, as though the class
 * declared no suitable constructor.
 *
 * <p>All POJOs used here are compiled without debug information; see
 * {@link ParameterNamesAreMissingTest} for the guard that keeps that true.
 *
 * <p><b>NOTE:</b> these tests can fail when running from IDE
 * depending on how the IDE compiles the classes.
 */
class MissingParameterNamesTest {

    /**
     * The core guarantee: the object is still created and its fields are still
     * populated, exactly as though the class declared no suitable constructor.
     */
    @Test
    void createsAndPopulatesTheObject() {
        final AllArgsCtor result = Instancio.create(AllArgsCtor.class);

        assertThat(result).isNotNull();
        assertThat(result.getValue()).isNotBlank();
        assertThat(result.getNumber()).isNotZero();
    }

    /**
     * The reason the fallback is safe: because no constructor is invoked, every
     * field is populated through the normal path, so selectors still apply.
     */
    @Test
    void selectorsMatchingFieldsStillApply() {
        final AllArgsCtor result = Instancio.of(AllArgsCtor.class)
                .set(field(AllArgsCtor::getValue), "expected-value")
                .set(field(AllArgsCtor::getNumber), 42)
                .create();

        assertThat(result.getValue()).isEqualTo("expected-value");
        assertThat(result.getNumber()).isEqualTo(42);
    }

    /**
     * A constructor whose parameters cannot be mapped to any field is never
     * invoked with values generated purely from the parameter types: such
     * values are reachable by no selector, so {@code value} would come out
     * as {@code prefix + random} instead of the value set below.
     */
    @Test
    void doesNotInvokeTheConstructorWithUnmatchedValues() {
        final PartialArgsCtor result = Instancio.of(PartialArgsCtor.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES,
                        InstantiationStrategies.of(ALL_ARGS, NO_ARGS, BYPASS_CONSTRUCTOR))
                .set(field(PartialArgsCtor::getValue), "expected-value")
                .create();

        assertThat(result.getValue()).isEqualTo("expected-value");
    }

    @Test
    void fallsBackToTheNextStrategy() {
        final AllArgsAndNoArgsCtor result = Instancio.of(AllArgsAndNoArgsCtor.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, InstantiationStrategies.of(ALL_ARGS, NO_ARGS))
                .set(field(AllArgsAndNoArgsCtor::getValue), "expected-value")
                .create();

        assertThat(result.getValue()).isEqualTo("expected-value");
    }

    /**
     * With no strategy left to fall back to, the outcome is the same as for
     * any class no strategy can instantiate: null, not an error.
     */
    @Test
    void returnsNullWhenThereIsNoStrategyToFallBackTo() {
        final AllArgsCtor result = Instancio.of(AllArgsCtor.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, InstantiationStrategies.of(ALL_ARGS))
                .create();

        assertThat(result).isNull();
    }

    /**
     * A class with no value-passing constructor has no candidate
     * for the names to be resolved for.
     */
    @Test
    void unaffectedWhenThereIsNoValuePassingConstructor() {
        final NoArgsOnly result = Instancio.create(NoArgsOnly.class);

        assertThat(result.getValue()).isNotBlank();
    }

    /**
     * Records are matched positionally and never need parameter names.
     */
    @Test
    void unaffectedForRecords() {
        final PersonRecord result = Instancio.create(PersonRecord.class);

        assertThat(result.name()).isNotBlank();
    }
}

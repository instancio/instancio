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
package org.instancio.test.features.instantiation;

import lombok.Getter;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.InstantiationStrategies;
import org.instancio.settings.InstantiationStrategy;
import org.instancio.settings.Keys;
import org.instancio.settings.OnConstructorError;
import org.instancio.test.support.pojo.misc.WithDefaultConstructorThrowingError;
import org.instancio.test.support.pojo.misc.WithNonDefaultConstructorThrowingError;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.settings.InstantiationStrategy.ALL_ARGS;
import static org.instancio.settings.InstantiationStrategy.BYPASS_CONSTRUCTOR;
import static org.instancio.settings.InstantiationStrategy.NO_ARGS;
import static org.instancio.settings.OnConstructorError.FAIL;
import static org.instancio.test.features.instantiation.InstantiationStrategyTest.Expected.CREATED;
import static org.instancio.test.features.instantiation.InstantiationStrategyTest.Expected.NULL;
import static org.instancio.test.features.instantiation.InstantiationStrategyTest.Expected.THROWS;

/**
 * Full matrix of {@link InstantiationStrategy} &times; constructor shape
 * &times; {@link OnConstructorError}, asserting the observable outcome of
 * {@code create()} for every combination.
 *
 * <p>Each strategy is exercised <b>in isolation</b> (a single-element strategy
 * list) so that each cell reflects exactly what that one strategy does, with no
 * fallback to another strategy. A strategy can only instantiate a class if the
 * class exposes the kind of constructor the strategy targets:
 *
 * <ul>
 *   <li>{@link InstantiationStrategy#ALL_ARGS} &mdash; a constructor whose
 *       every parameter maps to a field</li>
 *   <li>{@link InstantiationStrategy#NO_ARGS} &mdash; a no-argument
 *       constructor</li>
 *   <li>{@link InstantiationStrategy#BYPASS_CONSTRUCTOR} &mdash; no constructor
 *       is invoked, so it always produces an instance</li>
 * </ul>
 *
 * <p>The {@link OnConstructorError} axis collapses into the {@link Expected}
 * outcome: it only changes the result of a {@link Expected#THROWS} cell (a
 * constructor that is invoked and throws), and does so uniformly. See
 * {@link Expected} for details.
 *
 * <p>NOTE: constructor parameter names are resolved from the
 * {@code LocalVariableTable} class file attribute, therefore these tests rely
 * on being compiled with debug information, which the Maven build enables by
 * default.
 *
 * @see InstantiationStrategyAdhocTest for behaviour that a matrix cannot express
 */
@FeatureTag(Feature.INSTANTIATION_STRATEGIES)
@ExtendWith(InstancioExtension.class)
class InstantiationStrategyTest {

    /**
     * The expected outcome of a strategy for a constructor shape, independent
     * of {@link OnConstructorError}.
     */
    enum Expected {
        /**
         * A non-null instance is returned (constructor succeeds, or is bypassed).
         */
        CREATED,
        /**
         * {@code null} is returned because no constructor was invoked.
         */
        NULL,
        /**
         * The selected constructor is invoked and throws. Since each strategy is
         * tested in isolation, there is no next strategy to fall back to, so the
         * result depends solely on {@link OnConstructorError}:
         * <ul>
         *   <li>{@code FALLBACK} yields {@code null}</li>
         *   <li>{@code FAIL} throws an {@link InstancioApiException}.</li>
         * </ul>
         */
        THROWS
    }

    //
    // Constructor shapes. Each class isolates one shape; the two throwing
    // shapes are shared POJOs, the rest are declared here for locality.
    //

    /**
     * Only the implicit no-argument constructor; does not throw.
     */
    @Getter
    @SuppressWarnings("unused")
    private static class NoArgsOnly {
        private @Nullable String value;

        NoArgsOnly() { /* class default ctor only */ }
    }

    /**
     * A single constructor whose parameter maps to the {@code value} field.
     */
    @Getter
    @SuppressWarnings("unused")
    private static class AllArgsCtor {
        private final String value;

        AllArgsCtor(final String value) {
            this.value = value;
        }
    }

    /**
     * A constructor with one parameter ({@code value}) that maps to a field
     * and one ({@code prefix}) that maps to no field. {@code ALL_ARGS}
     * requires <i>every</i> parameter to map to a field, so it cannot use this
     * constructor: a value generated for {@code prefix} would be reachable by
     * no selector, yet would end up in the {@code value} field.
     */
    @Getter
    @SuppressWarnings("unused")
    private static class UnmatchedParamCtor {
        private final String value;

        UnmatchedParamCtor(final String prefix, final String value) {
            this.value = prefix + value;
        }
    }

    /**
     * Declares both a no-argument constructor (which throws) and a
     * value-passing constructor (which does not). Proves that
     * {@code ALL_ARGS} selects the value-passing constructor and never
     * touches the no-argument one, while {@code NO_ARGS} selects the
     * throwing no-argument constructor.
     */
    @Getter
    @SuppressWarnings("unused")
    private static class AllArgsAndThrowingNoArgCtor {
        private final String value;

        AllArgsAndThrowingNoArgCtor() {
            throw new UnsupportedOperationException("no-argument constructor");
        }

        AllArgsAndThrowingNoArgCtor(final String value) {
            this.value = value;
        }
    }

    @ParameterizedTest(name = "[{index}] {0} / {1} / {2} -> {3}")
    @MethodSource("matrix")
    void outcome(
            final Class<?> pojoClass,
            final InstantiationStrategy strategy,
            final OnConstructorError onConstructorError,
            final Expected expected) {

        final InstancioApi<?> api = Instancio.of(pojoClass)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, InstantiationStrategies.of(strategy))
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, onConstructorError);

        if (expected == THROWS && onConstructorError == FAIL) {
            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("failed instantiating");
        } else if (expected == CREATED) {
            assertThat(api.create()).isNotNull();
        } else {
            // NULL, or THROWS under FALLBACK (no next strategy to fall back to)
            assertThat(api.create()).isNull();
        }
    }

    private static Stream<Arguments> matrix() {
        return Stream.of(
                        shape(NoArgsOnly.class)
                                .expect(ALL_ARGS, NULL)
                                .expect(NO_ARGS, CREATED)
                                .expect(BYPASS_CONSTRUCTOR, CREATED),

                        shape(AllArgsCtor.class)
                                .expect(ALL_ARGS, CREATED)
                                .expect(NO_ARGS, NULL)
                                .expect(BYPASS_CONSTRUCTOR, CREATED),

                        shape(UnmatchedParamCtor.class)
                                .expect(ALL_ARGS, NULL)
                                .expect(NO_ARGS, NULL)
                                .expect(BYPASS_CONSTRUCTOR, CREATED),

                        shape(AllArgsAndThrowingNoArgCtor.class)
                                .expect(ALL_ARGS, CREATED)
                                .expect(NO_ARGS, THROWS)
                                .expect(BYPASS_CONSTRUCTOR, CREATED),

                        shape(WithDefaultConstructorThrowingError.class)
                                .expect(ALL_ARGS, NULL)
                                .expect(NO_ARGS, THROWS)
                                .expect(BYPASS_CONSTRUCTOR, CREATED),

                        shape(WithNonDefaultConstructorThrowingError.class)
                                .expect(ALL_ARGS, THROWS)
                                .expect(NO_ARGS, NULL)
                                .expect(BYPASS_CONSTRUCTOR, CREATED))
                .flatMap(Shape::stream);
    }

    private static Shape shape(final Class<?> pojoClass) {
        return new Shape(pojoClass);
    }

    /**
     * Maps each {@link InstantiationStrategy} to its {@link Expected} outcome
     * for one constructor shape, and expands the declaration into one
     * {@link Arguments} row per {@code (strategy, OnConstructorError)} cell.
     */
    private static final class Shape {
        private final Class<?> pojoClass;
        private final Map<InstantiationStrategy, Expected> expectations =
                new EnumMap<>(InstantiationStrategy.class);

        private Shape(final Class<?> pojoClass) {
            this.pojoClass = pojoClass;
        }

        Shape expect(final InstantiationStrategy strategy, final Expected expected) {
            expectations.put(strategy, expected);
            return this;
        }

        Stream<Arguments> stream() {
            if (expectations.size() != InstantiationStrategy.values().length) {
                throw new IllegalStateException(
                        "Every strategy must be declared for " + pojoClass.getSimpleName());
            }

            final List<Arguments> rows = new ArrayList<>();
            expectations.forEach((strategy, expected) -> {
                for (OnConstructorError onConstructorError : OnConstructorError.values()) {
                    rows.add(Arguments.of(
                            Named.of(pojoClass.getSimpleName(), pojoClass),
                            Named.of(strategy.name(), strategy),
                            Named.of(onConstructorError.name(), onConstructorError),
                            expected));
                }
            });
            return rows.stream();
        }
    }
}

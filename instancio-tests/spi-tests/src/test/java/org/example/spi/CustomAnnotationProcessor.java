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
package org.example.spi;

import org.instancio.Node;
import org.instancio.generator.specs.InstantSpec;
import org.instancio.generator.specs.LocalDateSpec;
import org.instancio.generator.specs.LongSpec;
import org.instancio.generator.specs.MapGeneratorSpec;
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.generator.specs.StringGeneratorSpec;
import org.instancio.spi.InstancioServiceProvider;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Warning: annotation processing is not invoked on any types
 * that are handled by {@link CustomGeneratorProvider}.
 */
public class CustomAnnotationProcessor implements InstancioServiceProvider {

    private static final AtomicInteger DUPLICATE_HANDLER_INVOCATION_COUNT = new AtomicInteger();

    @Retention(RetentionPolicy.RUNTIME)
    public @interface WithKeys {
        String[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface CustomLongMin {
        long value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface CustomLongMax {
        long value();
    }

    /**
     * Since we have a custom string generator defined in {@link CustomGeneratorProvider},
     * annotation processing on strings should not be invoked (this annotation should be ignored).
     */
    @Retention(RetentionPolicy.RUNTIME)
    public @interface EmptyString {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface PastLocalDate {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationForHandlerWithTooFewArgs {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationForHandlerWithTooManyArgs {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationWithTwoAnnotationHandlerMethods {
    }

    public static int getDuplicateHandlerInvocationCount() {
        return DUPLICATE_HANDLER_INVOCATION_COUNT.get();
    }

    @Override
    public AnnotationProcessor getAnnotationProcessor() {
        return new AnnotationProcessorImpl();
    }

    public static class AnnotationProcessorImpl implements AnnotationProcessor {

        @AnnotationHandler
        void withKeys(final WithKeys annotation, final MapGeneratorSpec<String, ?> spec, final Node node) {
            assertThat(node.getTargetClass()).isEqualTo(Map.class);

            spec.withKeys(annotation.value());
        }

        @AnnotationHandler
        void customLongMin(final CustomLongMin annotation, final LongSpec spec, final Node node) {
            spec.min(annotation.value());
        }

        @AnnotationHandler
        void customLongMax(final CustomLongMax annotation, final LongSpec spec, final Node node) {
            spec.max(annotation.value());
        }

        @AnnotationHandler
        void emptyString(final EmptyString annotation, final StringGeneratorSpec spec) {
            failIfCalled();
        }

        @AnnotationHandler
        void dupe1(final AnnotationWithTwoAnnotationHandlerMethods annotation, final NumberGeneratorSpec<Long> spec) {
            incrementInvocationCount(spec);
        }

        @AnnotationHandler
        void dupe2(final AnnotationWithTwoAnnotationHandlerMethods annotation, final NumberGeneratorSpec<Long> spec) {
            incrementInvocationCount(spec);
        }

        private static void incrementInvocationCount(final NumberGeneratorSpec<Long> spec) {
            long next = DUPLICATE_HANDLER_INVOCATION_COUNT.incrementAndGet();
            spec.min(next).max(next);
        }

        /**
         * This method should be ignored because the 1st argument is not an annotation.
         */
        @AnnotationHandler
        void handlerWithInvalidAnnotationParameter(final Object invalidArg, final StringGeneratorSpec spec) {
            failIfCalled();
        }

        /**
         * This method should be ignored because the spec class is not valid for the given annotation.
         */
        @AnnotationHandler
        void handlerWithInvalidGeneratorSpecParameter(final PastLocalDate annotation, final InstantSpec invalidSpec) {
            failIfCalled();
        }

        /**
         * This method should be ignored.
         */
        @AnnotationHandler
        void handlerWithZeroArgs() {
            failIfCalled();
        }

        @AnnotationHandler
        void handlerWithTooFewArgs(final AnnotationForHandlerWithTooFewArgs annotation) {
            failIfCalled();
        }

        @AnnotationHandler
        void handlerWithTooManyArgs(
                final AnnotationForHandlerWithTooManyArgs annotation,
                final LocalDateSpec spec,
                final Node node,
                final Object extraArg) { // extra args not allowed

            failIfCalled();
        }
    }

    private static void failIfCalled() {
        throw new AssertionError("Should not be called");
    }
}

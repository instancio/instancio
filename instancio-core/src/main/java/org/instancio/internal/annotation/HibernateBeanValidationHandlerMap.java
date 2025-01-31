/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.internal.annotation;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.specs.CollectionGeneratorSpec;
import org.instancio.generator.specs.DurationGeneratorSpec;
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.generator.specs.StringGeneratorSpec;
import org.instancio.internal.generator.lang.LongGenerator;
import org.instancio.internal.generator.lang.StringGenerator;
import org.instancio.internal.generator.specs.InternalLengthGeneratorSpec;
import org.instancio.internal.util.NumberUtils;
import org.instancio.internal.util.Range;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

import java.lang.annotation.Annotation;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

/**
 * Handler map for {@code org.hibernate.validator.constraints.*}.
 */
final class HibernateBeanValidationHandlerMap extends AnnotationHandlerMap {

    HibernateBeanValidationHandlerMap() {
        put(() -> org.hibernate.validator.constraints.time.DurationMin.class, new DurationMinHandler());
        put(() -> org.hibernate.validator.constraints.time.DurationMax.class, new DurationMaxHandler());
        put(() -> org.hibernate.validator.constraints.Length.class, new LengthHandler());
        put(() -> org.hibernate.validator.constraints.Range.class, new RangeHandler());
        put(() -> org.hibernate.validator.constraints.UniqueElements.class, new UniqueElementsHandler());
    }

    static AnnotationHandlerMap getInstance() {
        return Holder.INSTANCE;
    }

    private static final class DurationMinHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Class<?> targetClass,
                            final GeneratorContext generatorContext) {

            if (spec instanceof DurationGeneratorSpec) {
                final org.hibernate.validator.constraints.time.DurationMin d =
                        (org.hibernate.validator.constraints.time.DurationMin) annotation;

                final Duration min = Duration
                        .ofDays(d.days())
                        .plusHours(d.hours())
                        .plusMinutes(d.minutes())
                        .plusSeconds(d.seconds())
                        .plusMillis(d.millis())
                        .plusNanos(d.nanos());

                final DurationGeneratorSpec durationSpec = (DurationGeneratorSpec) spec;
                durationSpec.min(min.toNanos(), ChronoUnit.NANOS);
            }
        }
    }

    private static final class DurationMaxHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Class<?> targetClass,
                            final GeneratorContext generatorContext) {

            if (spec instanceof DurationGeneratorSpec) {
                final org.hibernate.validator.constraints.time.DurationMax d =
                        (org.hibernate.validator.constraints.time.DurationMax) annotation;

                final Duration max = Duration
                        .ofDays(d.days())
                        .plusHours(d.hours())
                        .plusMinutes(d.minutes())
                        .plusSeconds(d.seconds())
                        .plusMillis(d.millis())
                        .plusNanos(d.nanos());

                final DurationGeneratorSpec durationSpec = (DurationGeneratorSpec) spec;
                durationSpec.max(max.toNanos(), ChronoUnit.NANOS);
            }
        }
    }

    // Length is only applicable to character sequences
    private static final class LengthHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Class<?> targetClass,
                            final GeneratorContext generatorContext) {

            final org.hibernate.validator.constraints.Length length =
                    (org.hibernate.validator.constraints.Length) annotation;

            final Settings settings = generatorContext.getSettings();

            final Range<Integer> range = AnnotationUtils.calculateRange(
                    length.min(), length.max(), settings.get(Keys.STRING_MAX_LENGTH));

            if (spec instanceof InternalLengthGeneratorSpec<?>) {
                ((InternalLengthGeneratorSpec<?>) spec).length(range.min(), range.max());
            }
            if (range.min() > 0 && spec instanceof StringGeneratorSpec) {
                ((StringGeneratorSpec) spec).allowEmpty(false);
            }
        }
    }

    private static final class RangeHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Class<?> targetClass,
                            final GeneratorContext generatorContext) {

            final org.hibernate.validator.constraints.Range range =
                    (org.hibernate.validator.constraints.Range) annotation;

            if (spec instanceof NumberGeneratorSpec<?>) {
                final Function<Long, Number> fromLongConverter = NumberUtils.longConverter(targetClass);

                final NumberGeneratorSpec<Number> numSpec = (NumberGeneratorSpec<Number>) spec;
                numSpec
                        .min(fromLongConverter.apply(range.min()))
                        .max(fromLongConverter.apply(range.max()));

                AnnotationUtils.setSpecNullableToFalse(spec);

            } else if (spec instanceof StringGenerator) {
                final StringGenerator stringGenerator = (StringGenerator) spec;
                final LongGenerator numGenerator = new LongGenerator(stringGenerator.getContext())
                        .nullable(false)
                        .min(range.min())
                        .max(range.max());

                stringGenerator.setDelegate(numGenerator);
            }
        }
    }

    private static final class UniqueElementsHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Class<?> targetClass,
                            final GeneratorContext generatorContext) {

            if (spec instanceof CollectionGeneratorSpec<?>) {
                ((CollectionGeneratorSpec<?>) spec).unique();
            }
        }
    }

    private static final class Holder {
        private static final AnnotationHandlerMap INSTANCE = new HibernateBeanValidationHandlerMap();
    }
}

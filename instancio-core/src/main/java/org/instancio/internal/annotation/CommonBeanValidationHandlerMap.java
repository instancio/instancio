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

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.specs.ArrayGeneratorSpec;
import org.instancio.generator.specs.CollectionGeneratorSpec;
import org.instancio.generator.specs.MapGeneratorSpec;
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.generator.specs.StringGeneratorSpec;
import org.instancio.generator.specs.TemporalGeneratorSpec;
import org.instancio.internal.generator.array.ArrayGenerator;
import org.instancio.internal.generator.lang.BooleanGenerator;
import org.instancio.internal.generator.lang.StringGenerator;
import org.instancio.internal.generator.specs.InternalFractionalNumberGeneratorSpec;
import org.instancio.internal.generator.specs.InternalLengthGeneratorSpec;
import org.instancio.internal.generator.specs.InternalNumberGeneratorSpec;
import org.instancio.internal.generator.util.CollectionGenerator;
import org.instancio.internal.generator.util.MapGenerator;
import org.instancio.internal.util.NumberUtils;
import org.instancio.internal.util.Range;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Base class for {@code javax} and {@code jakarta} validation annotations.
 */
class CommonBeanValidationHandlerMap extends AnnotationHandlerMap {

    abstract static class AbstractDigitsHandler implements FieldAnnotationHandler {

        abstract int getFraction(Annotation annotation);

        abstract int getInteger(Annotation annotation);

        @Override
        public final void process(final Annotation annotation,
                                  final GeneratorSpec<?> spec,
                                  final Class<?> targetClass,
                                  final GeneratorContext generatorContext) {

            final int fraction = getFraction(annotation);

            if (spec instanceof StringGenerator) {
                final StringGenerator generator = (StringGenerator) spec;
                generator
                        .digits()
                        .length(getInteger(annotation))
                        .allowEmpty(false);

                if (fraction > 0) {
                    final Random random = generator.getContext().random();
                    generator.suffix("." + random.digits(fraction));
                }

            } else if (spec instanceof InternalNumberGeneratorSpec<?>) {
                final InternalNumberGeneratorSpec<Number> numSpec = (InternalNumberGeneratorSpec<Number>) spec;

                final int integer = getInteger(annotation);
                numSpec.integerMax(integer);

                AnnotationUtils.setSpecNullableToFalse(spec);

                if (spec instanceof InternalFractionalNumberGeneratorSpec) {
                    ((InternalFractionalNumberGeneratorSpec<?>) spec).scale(fraction);
                }
            }
        }
    }

    abstract static class AbstractMinHandler implements FieldAnnotationHandler {

        abstract long getValue(Annotation annotation);

        @Override
        public final void process(final Annotation annotation,
                                  final GeneratorSpec<?> spec,
                                  final Class<?> targetClass,
                                  final GeneratorContext generatorContext) {

            if (spec instanceof NumberGeneratorSpec<?>) {
                final Function<Long, Number> fromLongConverter = NumberUtils.longConverter(targetClass);
                ((NumberGeneratorSpec<Number>) spec).min(fromLongConverter.apply(getValue(annotation)));
                AnnotationUtils.setSpecNullableToFalse(spec);
            }
        }
    }

    abstract static class AbstractMaxHandler implements FieldAnnotationHandler {

        abstract long getValue(Annotation annotation);

        @Override
        public final void process(final Annotation annotation,
                                  final GeneratorSpec<?> spec,
                                  final Class<?> targetClass,
                                  final GeneratorContext generatorContext) {

            if (spec instanceof NumberGeneratorSpec<?>) {
                final Function<Long, Number> fromLongConverter = NumberUtils.longConverter(targetClass);
                ((NumberGeneratorSpec<Number>) spec).max(fromLongConverter.apply(getValue(annotation)));
                AnnotationUtils.setSpecNullableToFalse(spec);
            }
        }
    }

    abstract static class AbstractDecimalMinHandler implements FieldAnnotationHandler {

        abstract String getValue(Annotation annotation);

        @Override
        public final void process(final Annotation annotation,
                                  final GeneratorSpec<?> spec,
                                  final Class<?> targetClass,
                                  final GeneratorContext generatorContext) {

            if (spec instanceof NumberGeneratorSpec<?>) {
                final String value = getValue(annotation);
                final Function<BigDecimal, Number> converter = NumberUtils.bigDecimalConverter(targetClass);
                final BigDecimal min = new BigDecimal(value);
                final NumberGeneratorSpec<Number> numSpec = (NumberGeneratorSpec<Number>) spec;
                numSpec.min(converter.apply(min));
                AnnotationUtils.setSpecNullableToFalse(spec);
            }
        }
    }

    abstract static class AbstractDecimalMaxHandler implements FieldAnnotationHandler {

        abstract String getValue(Annotation annotation);

        @Override
        public final void process(final Annotation annotation,
                                  final GeneratorSpec<?> spec,
                                  final Class<?> targetClass,
                                  final GeneratorContext generatorContext) {

            if (spec instanceof NumberGeneratorSpec<?>) {
                final String value = getValue(annotation);
                final Function<BigDecimal, Number> converter = NumberUtils.bigDecimalConverter(targetClass);
                final BigDecimal max = new BigDecimal(value);
                ((NumberGeneratorSpec<Number>) spec).max(converter.apply(max));
                AnnotationUtils.setSpecNullableToFalse(spec);
            }
        }
    }

    static final class PositiveHandler implements FieldAnnotationHandler {
        private final BigDecimal min;

        PositiveHandler(final BigDecimal min) {
            this.min = min;
        }

        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Class<?> targetClass,
                            final GeneratorContext generatorContext) {

            if (spec instanceof InternalNumberGeneratorSpec<?>) {
                ((InternalNumberGeneratorSpec<Number>) spec).ensureMinIsGreaterThanOrEqualTo(min);
                AnnotationUtils.setSpecNullableToFalse(spec);
            }
        }
    }

    static final class NegativeHandler implements FieldAnnotationHandler {
        private final BigDecimal max;

        NegativeHandler(final BigDecimal max) {
            this.max = max;
        }

        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Class<?> targetClass,
                            final GeneratorContext generatorContext) {

            if (spec instanceof InternalNumberGeneratorSpec<?>) {
                ((InternalNumberGeneratorSpec<Number>) spec).ensureMaxIsLessThanOrEqualTo(max);
                AnnotationUtils.setSpecNullableToFalse(spec);
            }
        }
    }

    static final class PastHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Class<?> targetClass,
                            final GeneratorContext generatorContext) {

            if (spec instanceof TemporalGeneratorSpec<?>) {
                ((TemporalGeneratorSpec<?>) spec).past();
            }
        }
    }

    static final class FutureHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Class<?> targetClass,
                            final GeneratorContext generatorContext) {

            if (spec instanceof TemporalGeneratorSpec<?>) {
                ((TemporalGeneratorSpec<?>) spec).future();
            }
        }
    }

    abstract static class AbstractSizeHandler implements FieldAnnotationHandler {

        abstract int getMin(Annotation annotation);

        abstract int getMax(Annotation annotation);

        @Override
        public final void process(final Annotation annotation,
                                  final GeneratorSpec<?> spec,
                                  final Class<?> targetClass,
                                  final GeneratorContext generatorContext) {

            final Settings settings = generatorContext.getSettings();

            if (spec instanceof InternalLengthGeneratorSpec<?>) {
                final Range<Integer> range = AnnotationUtils.calculateRange(
                        getMin(annotation), getMax(annotation), settings.get(Keys.STRING_MAX_LENGTH));

                ((InternalLengthGeneratorSpec<?>) spec).length(range.min(), range.max());

                if (range.min() > 0 && spec instanceof StringGeneratorSpec) {
                    ((StringGeneratorSpec) spec).allowEmpty(false);
                }
            } else if (spec instanceof CollectionGeneratorSpec<?>) {
                final Range<Integer> range = AnnotationUtils.calculateRange(
                        getMin(annotation), getMax(annotation), settings.get(Keys.COLLECTION_MAX_SIZE));

                ((CollectionGeneratorSpec<?>) spec).minSize(range.min()).maxSize(range.max());
            } else if (spec instanceof MapGeneratorSpec<?, ?>) {
                final Range<Integer> range = AnnotationUtils.calculateRange(
                        getMin(annotation), getMax(annotation), settings.get(Keys.MAP_MAX_SIZE));

                ((MapGeneratorSpec<?, ?>) spec).minSize(range.min()).maxSize(range.max());
            } else if (spec instanceof ArrayGeneratorSpec<?>) {
                final Range<Integer> range = AnnotationUtils.calculateRange(
                        getMin(annotation), getMax(annotation), settings.get(Keys.ARRAY_MAX_LENGTH));

                ((ArrayGeneratorSpec<?>) spec).minLength(range.min()).maxLength(range.max());
            }
        }
    }

    static final class NotEmptyHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Class<?> targetClass,
                            final GeneratorContext generatorContext) {

            final Settings settings = generatorContext.getSettings();

            if (spec instanceof StringGenerator) {
                final StringGenerator generator = (StringGenerator) spec;
                generator.minLength(Math.max(generator.getMinLength(), 1))
                        .nullable(false)
                        .allowEmpty(false);

            } else if (spec instanceof ArrayGenerator<?>) {
                ((ArrayGenerator<?>) spec)
                        .nullable(false)
                        .minLength(settings.get(Keys.ARRAY_MIN_LENGTH));

            } else if (spec instanceof CollectionGenerator<?>) {
                ((CollectionGenerator<?>) spec)
                        .nullable(false)
                        .minSize(settings.get(Keys.COLLECTION_MIN_SIZE));

            } else if (spec instanceof MapGenerator<?, ?>) {
                ((MapGenerator<?, ?>) spec)
                        .nullable(false)
                        .minSize(settings.get(Keys.MAP_MIN_SIZE));
            }
        }
    }

    static final class NotNullHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Class<?> targetClass,
                            final GeneratorContext generatorContext) {

            AnnotationUtils.setSpecNullableToFalse(spec);
        }
    }

    static final class AssertBooleanHandler implements FieldAnnotationHandler {
        private final boolean generatedValue;

        AssertBooleanHandler(final boolean generatedValue) {
            this.generatedValue = generatedValue;
        }

        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Class<?> targetClass,
                            final GeneratorContext generatorContext) {

            if (spec instanceof BooleanGenerator) {
                ((BooleanGenerator) spec).probability(generatedValue ? 1 : 0);
                AnnotationUtils.setSpecNullableToFalse(spec);
            }
        }
    }
}

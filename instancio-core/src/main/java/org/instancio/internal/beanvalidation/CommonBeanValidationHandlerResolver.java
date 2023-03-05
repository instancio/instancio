/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.internal.beanvalidation;

import org.instancio.Random;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.specs.ArrayGeneratorSpec;
import org.instancio.generator.specs.BigDecimalGeneratorSpec;
import org.instancio.generator.specs.CollectionGeneratorSpec;
import org.instancio.generator.specs.MapGeneratorSpec;
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.generator.specs.StringGeneratorSpec;
import org.instancio.generator.specs.TemporalGeneratorSpec;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.array.ArrayGenerator;
import org.instancio.internal.generator.lang.AbstractRandomNumberGeneratorSpec;
import org.instancio.internal.generator.lang.StringGenerator;
import org.instancio.internal.generator.util.CollectionGenerator;
import org.instancio.internal.generator.util.MapGenerator;
import org.instancio.internal.util.BeanValidationUtils;
import org.instancio.internal.util.IntRange;
import org.instancio.internal.util.NumberUtils;
import org.instancio.internal.util.StringUtils;
import org.instancio.settings.Keys;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * Base class for {@code javax.validation.constraints} and {@code jakarta.validation.constraints}
 * non-primary annotations.
 *
 * <p>To get additional info about primary/non-primary annotations,
 * please read javadoc for {@link BeanValidationProcessor} class.
 */
class CommonBeanValidationHandlerResolver implements AnnotationHandlerResolver {

    private final Map<Class<?>, FieldAnnotationHandler> handlerMap;

    CommonBeanValidationHandlerResolver(Map<Class<?>, FieldAnnotationHandler> handlerMap) {
        this.handlerMap = Collections.unmodifiableMap(handlerMap);
    }

    @Override
    public final FieldAnnotationHandler resolveHandler(final Annotation annotation) {
        return handlerMap.get(annotation.annotationType());
    }

    abstract static class AbstractDigitsHandler implements FieldAnnotationHandler {

        abstract int getFraction(Annotation annotation);

        abstract int getInteger(Annotation annotation);

        @Override
        public final void process(final Annotation annotation,
                                  final GeneratorSpec<?> spec,
                                  final Field field,
                                  final Class<?> fieldType) {

            int fraction = getFraction(annotation);

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

            } else if (spec instanceof NumberGeneratorSpec<?>) {
                final NumberGeneratorSpec<Number> numSpec = (NumberGeneratorSpec<Number>) spec;

                final int integer = getInteger(annotation);

                // min/max integer range: e.g. 1000 - 9999
                final BigDecimal min = integer == 0 ? BigDecimal.ZERO : BigDecimal.TEN.pow(integer - 1);
                BigDecimal max = BigDecimal.TEN.pow(integer).subtract(BigDecimal.ONE);

                if (min.equals(BigDecimal.ZERO) && max.equals(BigDecimal.ZERO) && fraction > 0) {
                    max = new BigDecimal("0." + StringUtils.repeat("9", fraction));
                }

                final Function<BigDecimal, Number> converter = NumberUtils.bigDecimalConverter(fieldType);
                numSpec.min(converter.apply(min));
                numSpec.max(converter.apply(max));
                BeanValidationUtils.setNonNullablePrimitive(spec, field);

                // Currently fractions are supported for BigDecimal, but not float/double
                if (spec instanceof BigDecimalGeneratorSpec) {
                    ((BigDecimalGeneratorSpec) spec).scale(fraction);
                }
            }
        }
    }

    abstract static class AbstractMinHandler implements FieldAnnotationHandler {

        abstract long getValue(Annotation annotation);

        @Override
        public final void process(final Annotation annotation,
                                  final GeneratorSpec<?> spec,
                                  final Field field,
                                  final Class<?> fieldType) {

            if (spec instanceof NumberGeneratorSpec<?>) {
                final Function<Long, Number> fromLongConverter = NumberUtils.longConverter(fieldType);
                ((NumberGeneratorSpec<Number>) spec).min(fromLongConverter.apply(getValue(annotation)));
                BeanValidationUtils.setNonNullablePrimitive(spec, field);
            }
        }
    }

    abstract static class AbstractMaxHandler implements FieldAnnotationHandler {

        abstract long getValue(Annotation annotation);

        @Override
        public final void process(final Annotation annotation,
                                  final GeneratorSpec<?> spec,
                                  final Field field,
                                  final Class<?> fieldType) {

            if (spec instanceof NumberGeneratorSpec<?>) {
                final Function<Long, Number> fromLongConverter = NumberUtils.longConverter(fieldType);
                ((NumberGeneratorSpec<Number>) spec).max(fromLongConverter.apply(getValue(annotation)));
                BeanValidationUtils.setNonNullablePrimitive(spec, field);
            }
        }
    }

    abstract static class AbstractDecimalMinHandler implements FieldAnnotationHandler {

        abstract String getValue(Annotation annotation);

        @Override
        public final void process(final Annotation annotation,
                                  final GeneratorSpec<?> spec,
                                  final Field field,
                                  final Class<?> fieldType) {

            // TODO handle float/double
            if (spec instanceof NumberGeneratorSpec<?>) {
                final String value = getValue(annotation);
                final Function<BigDecimal, Number> converter = NumberUtils.bigDecimalConverter(fieldType);
                final BigDecimal min = new BigDecimal(value);
                final AbstractRandomNumberGeneratorSpec<Number> numSpec = (AbstractRandomNumberGeneratorSpec<Number>) spec;
                numSpec.min(converter.apply(min));
                BeanValidationUtils.setNonNullablePrimitive(spec, field);
            }
        }
    }

    abstract static class AbstractDecimalMaxHandler implements FieldAnnotationHandler {

        abstract String getValue(Annotation annotation);

        @Override
        public final void process(final Annotation annotation,
                                  final GeneratorSpec<?> spec,
                                  final Field field,
                                  final Class<?> fieldType) {

            // TODO handle float/double
            if (spec instanceof NumberGeneratorSpec<?>) {
                final String value = getValue(annotation);
                final Function<BigDecimal, Number> converter = NumberUtils.bigDecimalConverter(fieldType);
                final BigDecimal max = new BigDecimal(value);
                ((NumberGeneratorSpec<Number>) spec).max(converter.apply(max));
                BeanValidationUtils.setNonNullablePrimitive(spec, field);
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
                            final Field field,
                            final Class<?> fieldType) {

            if (spec instanceof NumberGeneratorSpec<?>) {
                final NumberGeneratorSpec<Number> numSpec = (NumberGeneratorSpec<Number>) spec;
                final Function<BigDecimal, Number> converter = NumberUtils.bigDecimalConverter(fieldType);
                final Number numberMaxValue = NumberUtils.getMaxValue(fieldType);
                numSpec.min(converter.apply(min))
                        .max(converter.apply(new BigDecimal(numberMaxValue.toString())));
                BeanValidationUtils.setNonNullablePrimitive(spec, field);
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
                            final Field field,
                            final Class<?> fieldType) {

            if (spec instanceof NumberGeneratorSpec<?>) {
                final NumberGeneratorSpec<Number> numSpec = (NumberGeneratorSpec<Number>) spec;
                final Function<BigDecimal, Number> converter = NumberUtils.bigDecimalConverter(fieldType);
                final Number numberMinValue = NumberUtils.getMinValue(fieldType);
                numSpec.min(converter.apply(new BigDecimal(numberMinValue.toString())))
                        .max(converter.apply(max));
                BeanValidationUtils.setNonNullablePrimitive(spec, field);
            }
        }
    }

    static final class PastHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Field field,
                            final Class<?> fieldType) {

            if (spec instanceof TemporalGeneratorSpec<?>) {
                ((TemporalGeneratorSpec<?>) spec).past();
            }
        }
    }

    static final class FutureHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Field field,
                            final Class<?> fieldType) {

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
                                  final Field field,
                                  final Class<?> fieldType) {

            if (spec instanceof StringGeneratorSpec) {
                final IntRange range = BeanValidationUtils.calculateRange(
                        getMin(annotation), getMax(annotation), Keys.STRING_MAX_LENGTH.defaultValue());

                final StringGeneratorSpec stringSpec = (StringGeneratorSpec) spec;
                stringSpec.length(range.min(), range.max());
                if (getMin(annotation) > 0) {
                    stringSpec.allowEmpty(false);
                }

            } else if (spec instanceof CollectionGeneratorSpec<?>) {
                final IntRange range = BeanValidationUtils.calculateRange(
                        getMin(annotation), getMax(annotation), Keys.COLLECTION_MAX_SIZE.defaultValue());

                ((CollectionGeneratorSpec<?>) spec).minSize(range.min()).maxSize(range.max());
            } else if (spec instanceof MapGeneratorSpec<?, ?>) {
                final IntRange range = BeanValidationUtils.calculateRange(
                        getMin(annotation), getMax(annotation), Keys.MAP_MAX_SIZE.defaultValue());

                ((MapGeneratorSpec<?, ?>) spec).minSize(range.min()).maxSize(range.max());
            } else if (spec instanceof ArrayGeneratorSpec<?>) {
                final IntRange range = BeanValidationUtils.calculateRange(
                        getMin(annotation), getMax(annotation), Keys.ARRAY_MAX_LENGTH.defaultValue());

                ((ArrayGeneratorSpec<?>) spec).minLength(range.min()).maxLength(range.max());
            }
        }
    }

    static final class NotEmptyHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Field field,
                            final Class<?> fieldType) {

            if (spec instanceof StringGenerator) {
                final StringGenerator generator = (StringGenerator) spec;
                generator.minLength(Math.max(generator.getMinLength(), 1))
                        .nullable(false)
                        .allowEmpty(false);

            } else if (spec instanceof ArrayGenerator<?>) {
                ((ArrayGenerator<?>) spec)
                        .nullable(false)
                        .minLength(Keys.ARRAY_MIN_LENGTH.defaultValue());

            } else if (spec instanceof CollectionGenerator<?>) {
                ((CollectionGenerator<?>) spec)
                        .nullable(false)
                        .minSize(Keys.COLLECTION_MIN_SIZE.defaultValue());

            } else if (spec instanceof MapGenerator<?, ?>) {
                ((MapGenerator<?, ?>) spec)
                        .nullable(false)
                        .minSize(Keys.MAP_MIN_SIZE.defaultValue());
            }
        }
    }

    static final class NotNullHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Field field,
                            final Class<?> fieldType) {

            if (spec instanceof AbstractGenerator<?>) {
                ((AbstractGenerator<?>) spec).nullable(false);
            }
        }
    }
}

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

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.instancio.internal.util.ExceptionHandler.runIgnoringTheNoClassDefFoundError;

@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.ExcessiveImports"})
final class JakartaBeanValidationHandlerResolver implements AnnotationHandlerResolver {

    private final Map<Class<?>, FieldAnnotationHandler> handlerMap;

    private JakartaBeanValidationHandlerResolver() {
        this.handlerMap = initHandlers();
    }

    static JakartaBeanValidationHandlerResolver getInstance() {
        return Holder.INSTANCE;
    }

    private static Map<Class<?>, FieldAnnotationHandler> initHandlers() {
        final Map<Class<?>, FieldAnnotationHandler> map = new HashMap<>();
        runIgnoringTheNoClassDefFoundError(() -> map.put(DecimalMax.class, new DecimalMaxHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(DecimalMin.class, new DecimalMinHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(Digits.class, new DigitsHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(Future.class, new FutureHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(FutureOrPresent.class, new FutureHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(Max.class, new MaxHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(Min.class, new MinHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(Negative.class, new NegativeHandler(new BigDecimal("-0.5"))));
        runIgnoringTheNoClassDefFoundError(() -> map.put(NotBlank.class, new NotEmptyHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(NotEmpty.class, new NotEmptyHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(NotNull.class, new NotNullHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(NegativeOrZero.class, new NegativeHandler(BigDecimal.ZERO)));
        runIgnoringTheNoClassDefFoundError(() -> map.put(Past.class, new PastHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(PastOrPresent.class, new PastHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(Positive.class, new PositiveHandler(new BigDecimal("0.5"))));
        runIgnoringTheNoClassDefFoundError(() -> map.put(PositiveOrZero.class, new PositiveHandler(BigDecimal.ZERO)));
        runIgnoringTheNoClassDefFoundError(() -> map.put(Size.class, new SizeHandler()));
        return Collections.unmodifiableMap(map);
    }

    @Override
    public FieldAnnotationHandler resolveHandler(final Annotation annotation) {
        return handlerMap.get(annotation.annotationType());
    }

    private static class DigitsHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Field field,
                            final Class<?> fieldType) {

            final Digits digits = (Digits) annotation;

            if (spec instanceof StringGenerator) {
                final StringGenerator generator = (StringGenerator) spec;
                generator
                        .digits()
                        .length(digits.integer())
                        .allowEmpty(false);

                if (digits.fraction() > 0) {
                    final Random random = generator.getContext().random();
                    final String fraction = random.digits(digits.fraction());
                    generator.suffix("." + fraction);
                }

            } else if (spec instanceof NumberGeneratorSpec<?>) {
                final NumberGeneratorSpec<Number> numSpec = (NumberGeneratorSpec<Number>) spec;

                final int integer = digits.integer();

                // min/max integer range: e.g. 1000 - 9999
                final BigDecimal min = integer == 0 ? BigDecimal.ZERO : BigDecimal.TEN.pow(integer - 1);
                BigDecimal max = BigDecimal.TEN.pow(integer).subtract(BigDecimal.ONE);

                if (min.equals(BigDecimal.ZERO) && max.equals(BigDecimal.ZERO) && digits.fraction() > 0) {
                    max = new BigDecimal("0." + StringUtils.repeat("9", digits.fraction()));
                }

                final Function<BigDecimal, Number> converter = NumberUtils.bigDecimalConverter(fieldType);
                numSpec.min(converter.apply(min));
                numSpec.max(converter.apply(max));
                BeanValidationUtils.setNonNullablePrimitive(spec, field);

                // NOTE: currently fractions only supported for BigDecimal, and partially at that.
                // See: https://github.com/instancio/instancio/issues/375
                if (spec instanceof BigDecimalGeneratorSpec) {
                    ((BigDecimalGeneratorSpec) spec).scale(digits.fraction());
                }
            }
        }
    }

    private static class MinHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Field field,
                            final Class<?> fieldType) {

            if (spec instanceof NumberGeneratorSpec<?>) {
                final Min min = (Min) annotation;
                final Function<Long, Number> fromLongConverter = NumberUtils.longConverter(fieldType);
                ((NumberGeneratorSpec<Number>) spec).min(fromLongConverter.apply(min.value()));
                BeanValidationUtils.setNonNullablePrimitive(spec, field);
            }
        }
    }

    private static class MaxHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Field field,
                            final Class<?> fieldType) {

            if (spec instanceof NumberGeneratorSpec<?>) {
                final Max max = (Max) annotation;
                final Function<Long, Number> fromLongConverter = NumberUtils.longConverter(fieldType);
                ((NumberGeneratorSpec<Number>) spec).max(fromLongConverter.apply(max.value()));
                BeanValidationUtils.setNonNullablePrimitive(spec, field);
            }
        }
    }

    private static class DecimalMinHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Field field,
                            final Class<?> fieldType) {

            // TODO handle float/double
            if (spec instanceof NumberGeneratorSpec<?>) {
                final String value = ((DecimalMin) annotation).value();
                final Function<BigDecimal, Number> converter = NumberUtils.bigDecimalConverter(fieldType);
                final BigDecimal min = new BigDecimal(value);
                final AbstractRandomNumberGeneratorSpec<Number> numSpec = (AbstractRandomNumberGeneratorSpec<Number>) spec;
                numSpec.min(converter.apply(min));
                BeanValidationUtils.setNonNullablePrimitive(spec, field);
            }
        }
    }

    private static class DecimalMaxHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Field field,
                            final Class<?> fieldType) {

            // TODO handle float/double
            if (spec instanceof NumberGeneratorSpec<?>) {
                final String value = ((DecimalMax) annotation).value();
                final Function<BigDecimal, Number> converter = NumberUtils.bigDecimalConverter(fieldType);
                final BigDecimal max = new BigDecimal(value);
                ((NumberGeneratorSpec<Number>) spec).max(converter.apply(max));
                BeanValidationUtils.setNonNullablePrimitive(spec, field);
            }
        }
    }

    private static class PositiveHandler implements FieldAnnotationHandler {
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

    private static class NegativeHandler implements FieldAnnotationHandler {
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

    private static class PastHandler implements FieldAnnotationHandler {
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

    private static class FutureHandler implements FieldAnnotationHandler {
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

    private static class SizeHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Field field,
                            final Class<?> fieldType) {

            final Size size = (Size) annotation;

            if (spec instanceof StringGeneratorSpec) {
                final IntRange range = BeanValidationUtils.calculateRange(
                        size.min(), size.max(), Keys.STRING_MAX_LENGTH.defaultValue());

                final StringGeneratorSpec stringSpec = (StringGeneratorSpec) spec;
                stringSpec.length(range.min(), range.max());
                if (size.min() > 0) {
                    stringSpec.allowEmpty(false);
                }

            } else if (spec instanceof CollectionGeneratorSpec<?>) {
                final IntRange range = BeanValidationUtils.calculateRange(
                        size.min(), size.max(), Keys.COLLECTION_MAX_SIZE.defaultValue());

                ((CollectionGeneratorSpec<?>) spec).minSize(range.min()).maxSize(range.max());
            } else if (spec instanceof MapGeneratorSpec<?, ?>) {
                final IntRange range = BeanValidationUtils.calculateRange(
                        size.min(), size.max(), Keys.MAP_MAX_SIZE.defaultValue());

                ((MapGeneratorSpec<?, ?>) spec).minSize(range.min()).maxSize(range.max());
            } else if (spec instanceof ArrayGeneratorSpec<?>) {
                final IntRange range = BeanValidationUtils.calculateRange(
                        size.min(), size.max(), Keys.ARRAY_MAX_LENGTH.defaultValue());

                ((ArrayGeneratorSpec<?>) spec).minLength(range.min()).maxLength(range.max());
            }
        }
    }

    private static class NotEmptyHandler implements FieldAnnotationHandler {
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

    private static class NotNullHandler implements FieldAnnotationHandler {
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

    private static class Holder {
        private static final JakartaBeanValidationHandlerResolver INSTANCE =
                new JakartaBeanValidationHandlerResolver();
    }
}

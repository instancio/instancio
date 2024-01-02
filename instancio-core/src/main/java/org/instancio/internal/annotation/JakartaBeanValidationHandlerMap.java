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
package org.instancio.internal.annotation;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;

/**
 * Handler map for {@code jakarta.validation.constraints.*}.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
final class JakartaBeanValidationHandlerMap extends CommonBeanValidationHandlerMap {

    JakartaBeanValidationHandlerMap() {
        put(() -> jakarta.validation.constraints.AssertFalse.class, new AssertBooleanHandler(false));
        put(() -> jakarta.validation.constraints.AssertTrue.class, new AssertBooleanHandler(true));
        put(() -> jakarta.validation.constraints.DecimalMax.class, new DecimalMaxHandler());
        put(() -> jakarta.validation.constraints.DecimalMin.class, new DecimalMinHandler());
        put(() -> jakarta.validation.constraints.Digits.class, new DigitsHandler());
        put(() -> jakarta.validation.constraints.Future.class, new FutureHandler());
        put(() -> jakarta.validation.constraints.FutureOrPresent.class, new FutureHandler());
        put(() -> jakarta.validation.constraints.Max.class, new MaxHandler());
        put(() -> jakarta.validation.constraints.Min.class, new MinHandler());
        put(() -> jakarta.validation.constraints.Negative.class, new NegativeHandler(new BigDecimal("-0.5")));
        put(() -> jakarta.validation.constraints.NotBlank.class, new NotEmptyHandler());
        put(() -> jakarta.validation.constraints.NotEmpty.class, new NotEmptyHandler());
        put(() -> jakarta.validation.constraints.NotNull.class, new NotNullHandler());
        put(() -> jakarta.validation.constraints.NegativeOrZero.class, new NegativeHandler(BigDecimal.ZERO));
        put(() -> jakarta.validation.constraints.Past.class, new PastHandler());
        put(() -> jakarta.validation.constraints.PastOrPresent.class, new PastHandler());
        put(() -> jakarta.validation.constraints.Positive.class, new PositiveHandler(new BigDecimal("0.5")));
        put(() -> jakarta.validation.constraints.PositiveOrZero.class, new PositiveHandler(BigDecimal.ZERO));
        put(() -> jakarta.validation.constraints.Size.class, new SizeHandler());
    }

    static AnnotationHandlerMap getInstance() {
        return Holder.INSTANCE;
    }

    private static final class DigitsHandler extends AbstractDigitsHandler {

        @Override
        int getFraction(Annotation annotation) {
            return ((jakarta.validation.constraints.Digits) annotation).fraction();
        }

        @Override
        int getInteger(Annotation annotation) {
            return ((jakarta.validation.constraints.Digits) annotation).integer();
        }
    }

    private static final class MinHandler extends AbstractMinHandler {

        @Override
        long getValue(Annotation annotation) {
            return ((jakarta.validation.constraints.Min) annotation).value();
        }
    }

    private static final class MaxHandler extends AbstractMaxHandler {

        @Override
        long getValue(Annotation annotation) {
            return ((jakarta.validation.constraints.Max) annotation).value();
        }
    }

    private static final class DecimalMinHandler extends AbstractDecimalMinHandler {

        @Override
        String getValue(Annotation annotation) {
            return ((jakarta.validation.constraints.DecimalMin) annotation).value();
        }
    }

    private static final class DecimalMaxHandler extends AbstractDecimalMaxHandler {

        @Override
        String getValue(Annotation annotation) {
            return ((jakarta.validation.constraints.DecimalMax) annotation).value();
        }
    }

    private static final class SizeHandler extends AbstractSizeHandler {

        @Override
        int getMin(Annotation annotation) {
            return ((jakarta.validation.constraints.Size) annotation).min();
        }

        @Override
        int getMax(Annotation annotation) {
            return ((jakarta.validation.constraints.Size) annotation).max();
        }
    }

    private static final class Holder {
        private static final AnnotationHandlerMap INSTANCE = new JakartaBeanValidationHandlerMap();
    }
}

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

import java.lang.annotation.Annotation;
import java.math.BigDecimal;

/**
 * Handler map for {@code javax.validation.constraints.*}.
 */
final class JavaxBeanValidationHandlerMap extends CommonBeanValidationHandlerMap {

    JavaxBeanValidationHandlerMap() {
        put(() -> javax.validation.constraints.AssertFalse.class, new AssertBooleanHandler(false));
        put(() -> javax.validation.constraints.AssertTrue.class, new AssertBooleanHandler(true));
        put(() -> javax.validation.constraints.DecimalMax.class, new DecimalMaxHandler());
        put(() -> javax.validation.constraints.DecimalMin.class, new DecimalMinHandler());
        put(() -> javax.validation.constraints.Digits.class, new DigitsHandler());
        put(() -> javax.validation.constraints.Future.class, new FutureHandler());
        put(() -> javax.validation.constraints.FutureOrPresent.class, new FutureHandler());
        put(() -> javax.validation.constraints.Max.class, new MaxHandler());
        put(() -> javax.validation.constraints.Min.class, new MinHandler());
        put(() -> javax.validation.constraints.Negative.class, new NegativeHandler(new BigDecimal("-0.5")));
        put(() -> javax.validation.constraints.NotBlank.class, new NotEmptyHandler());
        put(() -> javax.validation.constraints.NotEmpty.class, new NotEmptyHandler());
        put(() -> javax.validation.constraints.NotNull.class, new NotNullHandler());
        put(() -> javax.validation.constraints.NegativeOrZero.class, new NegativeHandler(BigDecimal.ZERO));
        put(() -> javax.validation.constraints.Past.class, new PastHandler());
        put(() -> javax.validation.constraints.PastOrPresent.class, new PastHandler());
        put(() -> javax.validation.constraints.Positive.class, new PositiveHandler(new BigDecimal("0.5")));
        put(() -> javax.validation.constraints.PositiveOrZero.class, new PositiveHandler(BigDecimal.ZERO));
        put(() -> javax.validation.constraints.Size.class, new SizeHandler());
    }

    static AnnotationHandlerMap getInstance() {
        return Holder.INSTANCE;
    }

    private static final class DigitsHandler extends AbstractDigitsHandler {

        @Override
        int getFraction(Annotation annotation) {
            return ((javax.validation.constraints.Digits) annotation).fraction();
        }

        @Override
        int getInteger(Annotation annotation) {
            return ((javax.validation.constraints.Digits) annotation).integer();
        }
    }

    private static final class MinHandler extends AbstractMinHandler {

        @Override
        long getValue(Annotation annotation) {
            return ((javax.validation.constraints.Min) annotation).value();
        }
    }

    private static final class MaxHandler extends AbstractMaxHandler {

        @Override
        long getValue(Annotation annotation) {
            return ((javax.validation.constraints.Max) annotation).value();
        }
    }

    private static final class DecimalMinHandler extends AbstractDecimalMinHandler {

        @Override
        String getValue(Annotation annotation) {
            return ((javax.validation.constraints.DecimalMin) annotation).value();
        }
    }

    private static final class DecimalMaxHandler extends AbstractDecimalMaxHandler {

        @Override
        String getValue(Annotation annotation) {
            return ((javax.validation.constraints.DecimalMax) annotation).value();
        }
    }

    private static final class SizeHandler extends AbstractSizeHandler {

        @Override
        int getMin(Annotation annotation) {
            return ((javax.validation.constraints.Size) annotation).min();
        }

        @Override
        int getMax(Annotation annotation) {
            return ((javax.validation.constraints.Size) annotation).max();
        }
    }

    private static final class Holder {
        private static final AnnotationHandlerMap INSTANCE = new JavaxBeanValidationHandlerMap();
    }
}

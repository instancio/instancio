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

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.instancio.internal.util.ExceptionUtils.runIgnoringTheNoClassDefFoundError;

/**
 * Contain {@link FieldAnnotationHandler}s for non-primary annotations from
 * {@code javax.validation.constraints} package.
 *
 * <p>To get additional info about primary/non-primary annotations,
 * please read javadoc for {@link BeanValidationProcessor} class.
 */
final class JavaxBeanValidationHandlerResolver extends CommonBeanValidationHandlerResolver {

    /*
     * Note: this class should not import `org.hibernate.validator.constraints.*`
     * to avoid class-not-found error if a constraint is not available on the classpath
     */

    static JavaxBeanValidationHandlerResolver getInstance() {
        return Holder.INSTANCE;
    }

    private JavaxBeanValidationHandlerResolver() {
        super(initHandlers());
    }

    private static Map<Class<?>, FieldAnnotationHandler> initHandlers() {
        final Map<Class<?>, FieldAnnotationHandler> map = new HashMap<>();
        runIgnoringTheNoClassDefFoundError(() -> map.put(javax.validation.constraints.DecimalMax.class, new DecimalMaxHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(javax.validation.constraints.DecimalMin.class, new DecimalMinHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(javax.validation.constraints.Digits.class, new DigitsHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(javax.validation.constraints.Future.class, new FutureHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(javax.validation.constraints.FutureOrPresent.class, new FutureHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(javax.validation.constraints.Max.class, new MaxHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(javax.validation.constraints.Min.class, new MinHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(javax.validation.constraints.Negative.class, new NegativeHandler(new BigDecimal("-0.5"))));
        runIgnoringTheNoClassDefFoundError(() -> map.put(javax.validation.constraints.NotBlank.class, new NotEmptyHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(javax.validation.constraints.NotEmpty.class, new NotEmptyHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(javax.validation.constraints.NotNull.class, new NotNullHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(javax.validation.constraints.NegativeOrZero.class, new NegativeHandler(BigDecimal.ZERO)));
        runIgnoringTheNoClassDefFoundError(() -> map.put(javax.validation.constraints.Past.class, new PastHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(javax.validation.constraints.PastOrPresent.class, new PastHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(javax.validation.constraints.Positive.class, new PositiveHandler(new BigDecimal("0.5"))));
        runIgnoringTheNoClassDefFoundError(() -> map.put(javax.validation.constraints.PositiveOrZero.class, new PositiveHandler(BigDecimal.ZERO)));
        runIgnoringTheNoClassDefFoundError(() -> map.put(javax.validation.constraints.Size.class, new SizeHandler()));
        return map;
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
        private static final JavaxBeanValidationHandlerResolver INSTANCE =
                new JavaxBeanValidationHandlerResolver();
    }
}

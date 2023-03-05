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

import static org.instancio.internal.util.ExceptionHandler.runIgnoringTheNoClassDefFoundError;

/**
 * Contain {@link FieldAnnotationHandler}s for non-primary annotations from
 * {@code jakarta.validation.constraints} package.
 *
 * <p>To get additional info about primary/non-primary annotations,
 * please read javadoc for {@link BeanValidationProcessor} class.
 */
final class JakartaBeanValidationHandlerResolver extends CommonBeanValidationHandlerResolver {

    /*
     * Note: this class should not import `jakarta.validation.constraints.*`
     * to avoid class-not-found error if a constraint is not available on the classpath
     */

    static JakartaBeanValidationHandlerResolver getInstance() {
        return Holder.INSTANCE;
    }

    private JakartaBeanValidationHandlerResolver() {
        super(initHandlers());
    }

    private static Map<Class<?>, FieldAnnotationHandler> initHandlers() {
        final Map<Class<?>, FieldAnnotationHandler> map = new HashMap<>();
        runIgnoringTheNoClassDefFoundError(() -> map.put(jakarta.validation.constraints.DecimalMax.class, new DecimalMaxHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(jakarta.validation.constraints.DecimalMin.class, new DecimalMinHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(jakarta.validation.constraints.Digits.class, new DigitsHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(jakarta.validation.constraints.Future.class, new FutureHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(jakarta.validation.constraints.FutureOrPresent.class, new FutureHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(jakarta.validation.constraints.Max.class, new MaxHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(jakarta.validation.constraints.Min.class, new MinHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(jakarta.validation.constraints.Negative.class, new NegativeHandler(new BigDecimal("-0.5"))));
        runIgnoringTheNoClassDefFoundError(() -> map.put(jakarta.validation.constraints.NotBlank.class, new NotEmptyHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(jakarta.validation.constraints.NotEmpty.class, new NotEmptyHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(jakarta.validation.constraints.NotNull.class, new NotNullHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(jakarta.validation.constraints.NegativeOrZero.class, new NegativeHandler(BigDecimal.ZERO)));
        runIgnoringTheNoClassDefFoundError(() -> map.put(jakarta.validation.constraints.Past.class, new PastHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(jakarta.validation.constraints.PastOrPresent.class, new PastHandler()));
        runIgnoringTheNoClassDefFoundError(() -> map.put(jakarta.validation.constraints.Positive.class, new PositiveHandler(new BigDecimal("0.5"))));
        runIgnoringTheNoClassDefFoundError(() -> map.put(jakarta.validation.constraints.PositiveOrZero.class, new PositiveHandler(BigDecimal.ZERO)));
        runIgnoringTheNoClassDefFoundError(() -> map.put(jakarta.validation.constraints.Size.class, new SizeHandler()));
        return map;
    }

    private static class DigitsHandler extends AbstractDigitsHandler {

        @Override
        int getFraction(Annotation annotation) {
            return ((jakarta.validation.constraints.Digits) annotation).fraction();
        }

        @Override
        int getInteger(Annotation annotation) {
            return ((jakarta.validation.constraints.Digits) annotation).integer();
        }
    }

    private static class MinHandler extends AbstractMinHandler {

        @Override
        long getValue(Annotation annotation) {
            return ((jakarta.validation.constraints.Min) annotation).value();
        }
    }

    private static class MaxHandler extends AbstractMaxHandler {

        @Override
        long getValue(Annotation annotation) {
            return ((jakarta.validation.constraints.Max) annotation).value();
        }
    }

    private static class DecimalMinHandler extends AbstractDecimalMinHandler {

        @Override
        String getValue(Annotation annotation) {
            return ((jakarta.validation.constraints.DecimalMin) annotation).value();
        }
    }

    private static class DecimalMaxHandler extends AbstractDecimalMaxHandler {

        @Override
        String getValue(Annotation annotation) {
            return ((jakarta.validation.constraints.DecimalMax) annotation).value();
        }
    }

    private static class SizeHandler extends AbstractSizeHandler {

        @Override
        int getMin(Annotation annotation) {
            return ((jakarta.validation.constraints.Size) annotation).min();
        }

        @Override
        int getMax(Annotation annotation) {
            return ((jakarta.validation.constraints.Size) annotation).max();
        }
    }

    private static class Holder {
        private static final JakartaBeanValidationHandlerResolver INSTANCE =
                new JakartaBeanValidationHandlerResolver();
    }
}

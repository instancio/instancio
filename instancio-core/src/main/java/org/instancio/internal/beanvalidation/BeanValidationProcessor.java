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

import org.instancio.generator.GeneratorSpec;
import org.instancio.internal.GeneratorSpecProcessor;
import org.instancio.internal.util.ReflectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is the entry point for processing Bean Validation annotations.
 *
 * <p>The main goals of this implementation are:
 * <ul>
 *   <li>support single annotations</li>
 *   <li>support combinations of annotations from different providers</li>
 *   <li>produce repeatable results regardless of declaration order</li>
 * </ul>
 *
 * <p>All of the above is done on a best-effort basis and is not guaranteed
 * to work in all circumstances.
 *
 * <p>When multiple annotations are declared, the processor first determines
 * and consumes the <i>primary</i> annotation. Primary annotations are those
 * that can only be applied to a certain type and have a certain meaning, e.g.
 * {@code  @Email, @URL, @UUID}. Currently, all supported primary annotations
 * target character sequences. Once a primary annotation has been consumed,
 * the processor will apply the remaining annotations.
 */
public class BeanValidationProcessor implements GeneratorSpecProcessor {

    private static final String JAVAX_VALIDATOR_CLASS = "javax.validation.Validation";
    private static final String JAKARTA_VALIDATOR_CLASS = "jakarta.validation.Validation";
    private static final String HIBERNATE_VALIDATOR_CLASS = "org.hibernate.validator.HibernateValidator";

    private final List<BeanValidationProvider> validationProviders;

    public BeanValidationProcessor() {
        validationProviders = initValidationProviders();
    }

    @Override
    public void process(@NotNull final GeneratorSpec<?> spec,
                        @NotNull final Class<?> targetClass,
                        @Nullable final Field field) {

        if (field == null) {
            return;
        }

        final Annotation[] annotations = field.getDeclaredAnnotations();
        final AnnotationMap map = new AnnotationMap(annotations);

        for (BeanValidationProvider provider : validationProviders) {
            for (Annotation annotation : annotations) {
                if (provider.isPrimary(annotation.annotationType())) {
                    map.setPrimary(annotation);
                    provider.consumeAnnotations(map, spec, targetClass, field);
                    break;
                }
            }
        }

        // consume remaining annotations, if any
        for (BeanValidationProvider provider : validationProviders) {
            provider.consumeAnnotations(map, spec, targetClass, field);
        }
    }

    private static List<BeanValidationProvider> initValidationProviders() {
        List<BeanValidationProvider> providers = new ArrayList<>();
        if (ReflectionUtils.loadClass(HIBERNATE_VALIDATOR_CLASS) != null) {
            providers.add(new HibernateBeanValidationProcessor());
        }
        if (ReflectionUtils.loadClass(JAKARTA_VALIDATOR_CLASS) != null) {
            providers.add(new JakartaBeanValidationProcessor());
        }
        if (ReflectionUtils.loadClass(JAVAX_VALIDATOR_CLASS) != null) {
            providers.add(new JavaxBeanValidationProcessor());
        }
        return Collections.unmodifiableList(providers);
    }
}

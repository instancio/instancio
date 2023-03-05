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

import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.domain.internet.EmailGenerator;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.instancio.internal.util.ExceptionHandler.runIgnoringTheNoClassDefFoundError;

/**
 * Contain generator(s) for primary annotation(s) from {@code javax.validation.constraints} package.
 *
 * <p>To get additional info about primary/non-primary annotations,
 * please read javadoc for {@link BeanValidationProcessor} class.
 */
final class JavaxBeanValidationProcessor extends AbstractBeanValidationProvider {

    private final JavaxBeanValidationHandlerResolver resolver =
            JavaxBeanValidationHandlerResolver.getInstance();

    JavaxBeanValidationProcessor() {
        super(buildMap());
    }

    private static Map<Class<? extends Annotation>, BiFunction<Annotation, GeneratorContext, Generator<?>>> buildMap() {
        Map<Class<? extends Annotation>, BiFunction<Annotation, GeneratorContext, Generator<?>>> map = new HashMap<>();
        runIgnoringTheNoClassDefFoundError(() ->
                map.put(javax.validation.constraints.Email.class, ((annotation, context) -> new EmailGenerator(context)))
        );
        return map;
    }

    @Override
    protected AnnotationHandlerResolver getAnnotationHandlerResolver() {
        return resolver;
    }
}

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

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.UUID;
import org.instancio.exception.InstancioException;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.generator.specs.StringGeneratorSpec;
import org.instancio.internal.generator.lang.AbstractRandomNumberGeneratorSpec;
import org.instancio.internal.generator.lang.LongGenerator;
import org.instancio.internal.generator.lang.StringGenerator;
import org.instancio.internal.generator.net.URLGenerator;
import org.instancio.internal.generator.util.UUIDGenerator;
import org.instancio.internal.util.BeanValidationUtils;
import org.instancio.internal.util.IntRange;
import org.instancio.internal.util.NumberUtils;
import org.instancio.internal.util.StringUtils;
import org.instancio.settings.Keys;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

final class HibernateBeanValidationHandlerResolver implements AnnotationHandlerResolver {

    private final Map<Class<?>, FieldAnnotationHandler> handlerMap;

    private HibernateBeanValidationHandlerResolver() {
        this.handlerMap = initHandlers();
    }

    static HibernateBeanValidationHandlerResolver getInstance() {
        return Holder.INSTANCE;
    }

    private static Map<Class<?>, FieldAnnotationHandler> initHandlers() {
        final Map<Class<?>, FieldAnnotationHandler> map = new HashMap<>();
        map.put(Length.class, new LengthHandler());
        map.put(Range.class, new RangeHandler());
        return Collections.unmodifiableMap(map);
    }

    @Override
    public FieldAnnotationHandler resolveHandler(final Annotation annotation) {
        return handlerMap.get(annotation.annotationType());
    }

    @Override
    public Generator<?> resolveGenerator(
            final Annotation annotation,
            final GeneratorContext context) {

        final Class<?> annotationType = annotation.annotationType();

        if (annotationType == UUID.class) {
            return new UUIDGenerator(context);
        }
        if (annotationType == URL.class) {
            final URL url = (URL) annotation;
            final URLGenerator urlGenerator = new URLGenerator(context)
                    .port(url.port());

            if (!StringUtils.isBlank(url.protocol())) {
                urlGenerator.protocol(url.protocol());
            }
            if (!StringUtils.isBlank(url.host())) {
                urlGenerator.host(random -> url.host());
            }
            return urlGenerator;
        }
        throw new InstancioException("Unmapped primary annotation:  " + annotationType.getName());
    }

    // Length is only applicable to character sequences
    private static class LengthHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Field field,
                            final Class<?> fieldType) {

            final Length length = (Length) annotation;
            final IntRange range = BeanValidationUtils.calculateRange(
                    length.min(), length.max(), Keys.STRING_MAX_LENGTH.defaultValue());

            if (spec instanceof StringGeneratorSpec) {
                final StringGeneratorSpec stringSpec = (StringGeneratorSpec) spec;
                stringSpec.length(range.min(), range.max());

                if (range.min() > 0) {
                    stringSpec.allowEmpty(false);
                }
            }
        }
    }

    private static class RangeHandler implements FieldAnnotationHandler {
        @Override
        public void process(final Annotation annotation,
                            final GeneratorSpec<?> spec,
                            final Field field,
                            final Class<?> fieldType) {

            final Range range = (Range) annotation;

            if (spec instanceof NumberGeneratorSpec<?>) {
                final Function<Long, Number> fromLongConverter = NumberUtils.longConverter(fieldType);

                final AbstractRandomNumberGeneratorSpec<Number> numSpec = (AbstractRandomNumberGeneratorSpec<Number>) spec;
                numSpec
                        .min(fromLongConverter.apply(range.min()))
                        .max(fromLongConverter.apply(range.max()));

                BeanValidationUtils.setNonNullablePrimitive(spec, field);

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

    private static class Holder {
        private static final HibernateBeanValidationHandlerResolver INSTANCE =
                new HibernateBeanValidationHandlerResolver();
    }
}

/*
 * Copyright 2022-2026 the original author or authors.
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

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.specs.StringGeneratorSpec;
import org.instancio.internal.generator.specs.InternalFractionalNumberGeneratorSpec;
import org.instancio.internal.util.Range;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

import java.lang.annotation.Annotation;

/**
 * Base class for {@code jakarta} persistence annotations.
 */
class CommonPersistenceAnnotationHandlerMap extends AnnotationHandlerMap {

    abstract static class AbstractColumnHandler implements FieldAnnotationHandler {

        abstract int getLength(Annotation annotation);

        abstract int getPrecision(Annotation annotation);

        abstract int getScale(Annotation annotation);

        @Override
        public final void process(final Annotation annotation,
                                  final GeneratorSpec<?> spec,
                                  final Class<?> targetClass,
                                  final GeneratorContext generatorContext) {

            if (spec instanceof final StringGeneratorSpec stringSpec) {
                final Settings settings = generatorContext.getSettings();
                final int maxLength = getLength(annotation);
                final int minLength = Math.min(maxLength, settings.get(Keys.STRING_MIN_LENGTH));

                // The default value of Column.length is 255. For this reason,
                // use STRING_MAX_LENGTH as the limit to avoid generating large strings.
                final Range<Integer> range = AnnotationUtils.calculateRange(
                        minLength, maxLength, settings.get(Keys.STRING_MAX_LENGTH));

                stringSpec.length(range.min(), range.max());
            } else if (spec instanceof final InternalFractionalNumberGeneratorSpec<?> fractionalSpec) {
                final int precision = getPrecision(annotation);
                final int scale = getScale(annotation);

                if (precision > 0) {
                    fractionalSpec.precision(precision).scale(scale);
                } else if (scale != 0) {
                    fractionalSpec.scale(scale);
                }
            }
        }
    }
}

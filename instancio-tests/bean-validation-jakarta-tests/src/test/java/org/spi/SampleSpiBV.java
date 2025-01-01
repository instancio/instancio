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
package org.spi;

import org.annotations.CollectionSizeOne;
import org.annotations.StringSuffix;
import org.instancio.Node;
import org.instancio.generator.Generator;
import org.instancio.generator.specs.CollectionGeneratorSpec;
import org.instancio.generator.specs.StringGeneratorSpec;
import org.instancio.spi.InstancioServiceProvider;

import java.lang.reflect.Field;

public class SampleSpiBV implements InstancioServiceProvider {

    @Override
    public GeneratorProvider getGeneratorProvider() {
        return (node, generators) -> {
            Field field = node.getField();

            if (field != null && field.getName().equals("foo")) {
                return (Generator<?>) random -> "foo";
            }
            return null;
        };
    }

    @Override
    public AnnotationProcessor getAnnotationProcessor() {
        return new AnnotationProcessor() {

            @AnnotationHandler
            void stringSuffix(StringSuffix annotation, StringGeneratorSpec spec, Node node) {
                spec.suffix(annotation.value());
            }

            @AnnotationHandler
            void collectionSizeOne(CollectionSizeOne annotation, CollectionGeneratorSpec<?> spec, Node node) {
                spec.size(1);
            }
        };
    }
}

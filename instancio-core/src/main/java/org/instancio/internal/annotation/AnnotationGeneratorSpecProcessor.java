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
package org.instancio.internal.annotation;

import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.internal.GeneratorSpecProcessor;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
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
 * @see AnnotationMap
 */
public final class AnnotationGeneratorSpecProcessor implements GeneratorSpecProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotationGeneratorSpecProcessor.class);

    private static final String JAVAX_PERSISTENCE_CLASS = "javax.persistence.Column";
    private static final String JAKARTA_PERSISTENCE_CLASS = "jakarta.persistence.Column";
    private static final String JAVAX_VALIDATOR_CLASS = "javax.validation.Validation";
    private static final String JAKARTA_VALIDATOR_CLASS = "jakarta.validation.Validation";
    private static final String HIBERNATE_VALIDATOR_CLASS = "org.hibernate.validator.HibernateValidator";

    private final List<AnnotationConsumer> annotationConsumers;
    private final AnnotationExtractor annotationExtractor;

    private AnnotationGeneratorSpecProcessor(
            final ModelContext<?> context,
            final List<AnnotationConsumer> annotationConsumers) {

        this.annotationConsumers = annotationConsumers;
        this.annotationExtractor = new AnnotationExtractor(context);
    }

    public static GeneratorSpecProcessor create(final ModelContext<?> context) {
        final List<AnnotationConsumer> consumers = getAnnotationConsumers(context);

        return consumers.isEmpty()
                ? new NoopGeneratorSpecProcessor()
                : new AnnotationGeneratorSpecProcessor(context, consumers);
    }

    @Override
    public void process(@NotNull final GeneratorSpec<?> spec,
                        @NotNull final InternalNode node) {

        final Annotation[] annotations = annotationExtractor.getAnnotations(node);

        if (annotations.length == 0) {
            return;
        }

        final Class<?> targetClass = node.getTargetClass();
        final AnnotationMap annotationMap = new AnnotationMap(annotations);

        for (AnnotationConsumer provider : annotationConsumers) {
            for (Annotation annotation : annotations) {
                if (provider.isPrimary(annotation.annotationType())) {
                    annotationMap.setPrimary(annotation);
                    provider.consumeAnnotations(annotationMap, spec, targetClass);
                    break;
                }
            }
        }

        // consume remaining annotations, if any
        for (AnnotationConsumer provider : annotationConsumers) {
            provider.consumeAnnotations(annotationMap, spec, targetClass);
        }
    }


    private static List<AnnotationConsumer> getAnnotationConsumers(final ModelContext<?> modelContext) {
        final Settings settings = modelContext.getSettings();
        final GeneratorContext generatorContext = new GeneratorContext(settings, modelContext.getRandom());
        final boolean jpaEnabled = settings.get(Keys.JPA_ENABLED);
        final boolean beanValidationEnabled = settings.get(Keys.BEAN_VALIDATION_ENABLED);

        LOG.trace("Keys.BEAN_VALIDATION_ENABLED={}, Keys.JPA_ENABLED={}",
                beanValidationEnabled, jpaEnabled);

        // List order matters - later entry may override earlier one
        final List<AnnotationConsumer> result = new ArrayList<>();

        // In theory, there shouldn't be javax and jakarta on the classpath
        // at the same time. In practice, it can happen, so we add both.

        if (jpaEnabled) {
            if (ReflectionUtils.loadClass(JAVAX_PERSISTENCE_CLASS) != null) {
                result.add(new JavaxPersistenceAnnotationConsumer(generatorContext));
            }
            if (ReflectionUtils.loadClass(JAKARTA_PERSISTENCE_CLASS) != null) {
                result.add(new JakartaPersistenceAnnotationConsumer(generatorContext));
            }
        }

        if (beanValidationEnabled) {
            if (ReflectionUtils.loadClass(HIBERNATE_VALIDATOR_CLASS) != null) {
                result.add(new HibernateBeanValidationAnnotationConsumer(generatorContext));
            }
            if (ReflectionUtils.loadClass(JAKARTA_VALIDATOR_CLASS) != null) {
                result.add(new JakartaBeanValidationAnnotationConsumer(generatorContext));
            }
            if (ReflectionUtils.loadClass(JAVAX_VALIDATOR_CLASS) != null) {
                result.add(new JavaxBeanValidationAnnotationConsumer(generatorContext));
            }
        }
        return Collections.unmodifiableList(result);
    }
}

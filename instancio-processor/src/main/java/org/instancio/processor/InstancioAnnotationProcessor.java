/*
 *  Copyright 2022-2023 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.processor;

import org.instancio.InstancioMetamodel;
import org.instancio.processor.util.Logger;
import org.instancio.internal.util.Sonar;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.BufferedWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SupportedOptions({"instancio.verbose", "instancio.suffix"})
@SupportedAnnotationTypes("org.instancio.InstancioMetamodel")
public final class InstancioAnnotationProcessor extends AbstractProcessor {
    private static final String CLASSES_ATTRIBUTE = "classes";
    private static final String TRUE = "true";
    private final MetamodelSourceGenerator sourceGenerator = new MetamodelSourceGenerator();

    private Types typeUtils;
    private Elements elementUtils;
    private Logger logger;
    private String classNameSuffix;

    @Override
    @SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();
        this.logger = new Logger(processingEnv.getMessager(),
                TRUE.equalsIgnoreCase(processingEnv.getOptions().get("instancio.verbose")));
        this.classNameSuffix = processingEnv.getOptions().get("instancio.suffix");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    @SuppressWarnings(Sonar.METHODS_RETURNS_SHOULD_NOT_BE_INVARIANT)
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver() || roundEnv.errorRaised()) {
            return true;
        }

        final Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(InstancioMetamodel.class);
        if (annotatedElements.isEmpty()) {
            logger.debug("No @InstancioMetamodel annotations to process");
            return true;
        }

        logger.debug("Preparing to process %s @InstancioMetamodel annotation(s)", annotatedElements.size());

        for (Element annotatedElement : annotatedElements) {
            final TypeElement rootType = (TypeElement) annotatedElement;

            getAnnotationValues(rootType, CLASSES_ATTRIBUTE)
                    .stream()
                    .filter(av -> av.getValue() instanceof TypeMirror)
                    .map(av -> typeUtils.asElement((TypeMirror) av.getValue()))
                    .filter(e -> e instanceof QualifiedNameable)
                    .forEach(e -> writeSourceFile(new MetamodelClass((QualifiedNameable) e, classNameSuffix), rootType));
        }

        return true;
    }

    private void writeSourceFile(final MetamodelClass metaModelClass, final Element element) {
        final Filer filer = processingEnv.getFiler();
        final String filename = metaModelClass.getMetamodelClassName();

        try (Writer writer = new BufferedWriter(filer.createSourceFile(filename, element).openWriter())) {
            logger.debug("Generating metamodel class: %s", filename);
            writer.write(sourceGenerator.getSource(metaModelClass));
        } catch (Exception ex) {
            logger.warn("Error generating metamodel for '%s'", metaModelClass, ex);
        }
    }

    private List<AnnotationValue> getAnnotationValues(final Element element, final String attributeName) {
        final TypeElement typeElement = elementUtils.getTypeElement(InstancioMetamodel.class.getCanonicalName());
        final List<AnnotationValue> annotationValues = new ArrayList<>();

        for (AnnotationMirror am : element.getAnnotationMirrors()) {
            if (typeUtils.isSameType(am.getAnnotationType(), typeElement.asType())) {
                am.getElementValues().forEach((executableElement, annotationValue) -> {
                    if (attributeName.equals(executableElement.getSimpleName().toString())) {
                        annotationValues.addAll((List<AnnotationValue>) annotationValue.getValue());
                    }
                });
            }
        }
        return annotationValues;
    }
}

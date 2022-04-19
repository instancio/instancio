/*
 *  Copyright 2022 the original author or authors.
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

import org.instancio.InstancioMetaModel;
import org.instancio.processor.util.Logger;

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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.BufferedWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SupportedOptions({"instancio.verbose", "instancio.suffix"})
@SupportedAnnotationTypes("org.instancio.InstancioMetaModel")
public final class InstancioAnnotationProcessor extends AbstractProcessor {

    private static final String MODEL_CLASSES_ATTRIBUTE = "classes";
    private static final MetaModelSourceGenerator sourceGenerator = new MetaModelSourceGenerator();
    private static final String TRUE = "true";

    private Types types;
    private Elements elements;
    private Logger logger;
    private String classNameSuffix;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.types = processingEnv.getTypeUtils();
        this.elements = processingEnv.getElementUtils();
        this.logger = new Logger(processingEnv.getMessager(),
                TRUE.equalsIgnoreCase(processingEnv.getOptions().get("instancio.verbose")));
        this.classNameSuffix = processingEnv.getOptions().get("instancio.suffix");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver() || roundEnv.errorRaised()) {
            return true;
        }

        final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(InstancioMetaModel.class);
        if (elements.isEmpty()) {
            logger.debug("No @InstancioMetaModel annotations to process");
            return true;
        }

        logger.debug("Preparing to process %s @InstancioMetaModel annotation(s)", elements.size());

        for (Element annotatedElement : elements) {
            final TypeElement rootType = (TypeElement) annotatedElement;

            final List<TypeMirror> modelClasses = getClassArrayValueFromAnnotation(
                    rootType, InstancioMetaModel.class, MODEL_CLASSES_ATTRIBUTE);

            for (TypeMirror typeMirror : modelClasses) {
                final Element element = types.asElement(typeMirror);
                if (element instanceof QualifiedNameable) {
                    writeSourceFile(new MetaModelClass((QualifiedNameable) element, classNameSuffix), annotatedElement);
                } else {
                    logger.debug("Not a QualifiedNameable: %s", typeMirror);
                }
            }
        }

        return true;
    }

    private void writeSourceFile(final MetaModelClass metaModelClass, final Element element) {
        final Filer filer = processingEnv.getFiler();
        final String filename = metaModelClass.getMetaModelClassName();

        try (Writer writer = new BufferedWriter(filer.createSourceFile(filename, element).openWriter())) {
            logger.debug("Generating metamodel class: %s", filename);
            writer.write(sourceGenerator.getSource(metaModelClass));
        } catch (Exception ex) {
            logger.warn("Error generating metamodel for '%s'", metaModelClass, ex);
        }
    }

    private List<TypeMirror> getClassArrayValueFromAnnotation(final Element element,
                                                              final Class<? extends Annotation> annotation,
                                                              final String attributeName) {
        final List<TypeMirror> values = new ArrayList<>();

        for (AnnotationMirror am : element.getAnnotationMirrors()) {
            if (types.isSameType(am.getAnnotationType(), elements.getTypeElement(annotation.getCanonicalName()).asType())) {

                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues().entrySet()) {
                    if (attributeName.equals(entry.getKey().getSimpleName().toString())) {
                        final List<AnnotationValue> classesTypes = (List<AnnotationValue>) entry.getValue().getValue();
                        for (AnnotationValue annotationValue : classesTypes) {
                            final Object value = annotationValue.getValue();
                            if (value instanceof TypeMirror) {
                                values.add((TypeMirror) value);
                            } else {
                                logger.warn("Unexpected annotation value: %s -> %s", annotationValue, value);
                            }
                        }
                    }
                }
            }
        }
        return values;
    }
}

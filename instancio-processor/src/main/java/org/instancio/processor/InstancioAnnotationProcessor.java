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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.instancio.InstancioMetaModel")
public class InstancioAnnotationProcessor extends AbstractProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(InstancioAnnotationProcessor.class);

    private static final String MODEL_CLASSES_ATTRIBUTE = "classes";
    private static final MetaModelSourceGenerator sourceGenerator = new MetaModelSourceGenerator();

    private Messager messager;
    private Types typeUtils;
    private Elements elementUtils;
    private Path buildDirectory;
    private MetaModelSourceWriter sourceWriter;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();
        this.buildDirectory = resolveBuildDirectory();
        this.sourceWriter = new MetaModelSourceWriter(buildDirectory);
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (buildDirectory == null) {
            LOG.warn("Could not resolve output directory");
            messager.printMessage(Kind.ERROR, "Could not resolve output directory");
            return false;
        }

        final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(InstancioMetaModel.class);
        messager.printMessage(Kind.NOTE, "Preparing to process " + elements.size() + " elements");

        for (Element element : elements) {
            final TypeElement rootType = (TypeElement) element;

            final List<TypeMirror> modelClasses = getClassArrayValueFromAnnotation(
                    rootType, InstancioMetaModel.class, MODEL_CLASSES_ATTRIBUTE);

            for (TypeMirror typeMirror : modelClasses) {
                final Element modelClass = typeUtils.asElement(typeMirror);
                final MetaModelClass metaModelClass = new MetaModelClass(
                        getClassName(modelClass), getFieldNames(modelClass));

                try {
                    messager.printMessage(Kind.NOTE, "Generating metamodel class: " + metaModelClass);
                    sourceWriter.writeSource(metaModelClass, sourceGenerator.getSource(metaModelClass));
                } catch (Exception ex) {
                    LOG.error("Error generating metamodel for class '{}'", metaModelClass, ex);
                    messager.printMessage(Kind.ERROR, "Exception occurred: " + ex.getMessage());
                }
            }
        }

        return true;
    }

    private Path resolveBuildDirectory() {
        try {
            final Filer filer = processingEnv.getFiler();
            final FileObject resource = filer.createResource(
                    StandardLocation.CLASS_OUTPUT, "", "anything", (Element[]) null);

            final Path projectPath = Paths.get(resource.toUri()).getParent().getParent();
            resource.delete();
            return projectPath.resolve("src").getParent();
        } catch (IOException ex) {
            LOG.error("Error resolving build directory", ex);
            messager.printMessage(Kind.ERROR, "Cannot get output directory");
        }
        return null;
    }

    private static List<String> getFieldNames(final Element element) {
        if (element == null) {
            return Collections.emptyList();
        }

        return element.getEnclosedElements()
                .stream()
                .filter(elem -> elem.getKind() == ElementKind.FIELD)
                .map(Object::toString)
                .collect(toList());
    }

    private static String getClassName(final Element element) {
        if (element instanceof QualifiedNameable) {
            return ((QualifiedNameable) element).getQualifiedName().toString();
        }
        return element == null ? null : element.getSimpleName().toString();
    }

    private List<TypeMirror> getClassArrayValueFromAnnotation(final Element element,
                                                              final Class<? extends Annotation> annotation,
                                                              final String attributeName) {
        final List<TypeMirror> values = new ArrayList<>();

        for (AnnotationMirror am : element.getAnnotationMirrors()) {
            if (typeUtils.isSameType(am.getAnnotationType(), elementUtils.getTypeElement(annotation.getCanonicalName()).asType())) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : am.getElementValues().entrySet()) {
                    if (attributeName.equals(entry.getKey().getSimpleName().toString())) {
                        final List<AnnotationValue> classesTypes = (List<AnnotationValue>) entry.getValue().getValue();

                        for (AnnotationValue next : classesTypes) {
                            values.add((TypeMirror) next.getValue());
                        }
                    }
                }
            }
        }
        return values;
    }
}

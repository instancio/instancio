/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.internal.schema;

import org.instancio.internal.util.Fail;
import org.instancio.internal.util.TypeUtils;
import org.instancio.schema.DataSpec;
import org.instancio.schema.DerivedSpec;
import org.instancio.schema.GeneratedSpec;
import org.instancio.schema.NullableSpec;
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaSpec;
import org.instancio.schema.TemplateSpec;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.instancio.internal.util.ErrorMessageUtils.invalidSpecMethod;

/**
 * A wrapper for methods that return a {@link SchemaSpec}
 * declared by user-defined {@link Schema} subclasses.
 */
public final class SpecMethod {

    private final Method method;
    private final Class<?> targetType;
    private final GeneratedSpec generatedSpec;
    private final DerivedSpec derivedSpec;
    private final TemplateSpec templateSpec;
    private final DataSpec dataSpec;
    private final String dataPropertyName;

    public SpecMethod(final Method method) {
        this.method = method;
        this.targetType = resolveTargetType();
        this.generatedSpec = getAnnotation(GeneratedSpec.class);
        this.derivedSpec = getAnnotation(DerivedSpec.class);
        this.templateSpec = getAnnotation(TemplateSpec.class);
        this.dataSpec = getAnnotation(DataSpec.class);
        this.dataPropertyName = dataSpec == null ? method.getName() : dataSpec.propertyName();
    }

    public Method getMethod() {
        return method;
    }

    public GeneratedSpec getGeneratedSpec() {
        return generatedSpec;
    }

    public DerivedSpec getDerivedSpec() {
        return derivedSpec;
    }

    public TemplateSpec getTemplateSpec() {
        return templateSpec;
    }

    public DataSpec getDataSpec() {
        return dataSpec;
    }

    /**
     * Returns the name of the property key in the data file
     * that maps to this spec method.
     *
     * <p>If {@link DataSpec} annotation is defined, the mapping is done
     * using the annotation's attributed, otherwise the mapping is done
     * using the name of the method that returns the {@link SchemaSpec}.
     */
    public String getDataPropertyName() {
        return dataPropertyName;
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> getTargetType() {
        return (Class<T>) targetType;
    }

    public boolean hasNullableAnnotation() {
        return method.getDeclaredAnnotation(NullableSpec.class) != null;
    }

    public <A extends Annotation> A getAnnotation(final Class<A> annotationClass) {
        return method.getDeclaredAnnotation(annotationClass);
    }

    public String getName() {
        return method.getName();
    }

    public boolean isDeclaredBySchemaInterface() {
        return method.getDeclaringClass() == Schema.class;
    }

    private Class<?> resolveTargetType() {
        // Base class has methods with parameters
        // and target type is resolved from the parameter type
        if (isDeclaredBySchemaInterface()) {
            return TypeUtils.getFirstTypeArg(method.getGenericReturnType());
        }

        // For subclasses (created by users) target type
        // is resolved from the method's return type
        final Type returnType = method.getGenericReturnType();
        final Class<?> rawType = TypeUtils.getRawType(returnType);

        if (SchemaSpec.class.isAssignableFrom(rawType)) {
            final Type[] typeArgs = ((ParameterizedType) returnType).getActualTypeArguments();
            final Type typeArg = typeArgs[0];
            if (typeArg instanceof Class<?>) {
                return (Class<?>) typeArg;
            }
        }
        throw Fail.withUsageError(invalidSpecMethod(method));
    }
}

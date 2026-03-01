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
package org.instancio.internal.feed;

import org.instancio.feed.Feed;
import org.instancio.feed.FeedSpec;
import org.instancio.feed.FeedSpecAnnotations.AliasSpec;
import org.instancio.feed.FeedSpecAnnotations.FunctionSpec;
import org.instancio.feed.FeedSpecAnnotations.GeneratedSpec;
import org.instancio.feed.FeedSpecAnnotations.NullableSpec;
import org.instancio.feed.FeedSpecAnnotations.TemplateSpec;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.TypeUtils;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.instancio.internal.util.ErrorMessageUtils.invalidSpecMethod;

/**
 * A wrapper for methods that return a {@link FeedSpec}
 * declared by user-defined {@link Feed} subclasses.
 */
public final class SpecMethod {

    private final Method method;
    private final Class<?> targetType;
    private final @Nullable GeneratedSpec generatedSpec;
    private final @Nullable FunctionSpec functionSpec;
    private final @Nullable TemplateSpec templateSpec;
    private final String dataPropertyName;

    public SpecMethod(final Method method) {
        this.method = method;
        this.targetType = resolveTargetType();
        this.generatedSpec = getAnnotation(GeneratedSpec.class);
        this.functionSpec = getAnnotation(FunctionSpec.class);
        this.templateSpec = getAnnotation(TemplateSpec.class);
        final AliasSpec aliasSpec = getAnnotation(AliasSpec.class);
        this.dataPropertyName = aliasSpec == null ? method.getName() : aliasSpec.value();
    }

    public Method getMethod() {
        return method;
    }

    @Nullable
    public GeneratedSpec getGeneratedSpec() {
        return generatedSpec;
    }

    @Nullable
    public FunctionSpec getFunctionSpec() {
        return functionSpec;
    }

    @Nullable
    public TemplateSpec getTemplateSpec() {
        return templateSpec;
    }

    /**
     * Returns the name of the property key in the data file
     * that maps to this spec method.
     *
     * <p>If {@link AliasSpec} annotation is defined, the mapping is done
     * using the annotation's attributed, otherwise the mapping is done
     * using the name of the method that returns the {@link FeedSpec}.
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

    @Nullable
    public <A extends Annotation> A getAnnotation(final Class<A> annotationClass) {
        return method.getDeclaredAnnotation(annotationClass);
    }

    public boolean isDeclaredByFeedInterface() {
        // Feed extends FeedSpecAccessors (which is package-private).
        // Therefore, we get it the reference using reflection.
        final Class<?> superclass = Feed.class.getInterfaces()[0];
        return method.getDeclaringClass() == superclass;
    }

    private Class<?> resolveTargetType() {
        // Base class has methods with parameters
        // and target type is resolved from the parameter type
        if (isDeclaredByFeedInterface()) {
            return TypeUtils.getFirstTypeArg(method.getGenericReturnType());
        }

        // For subclasses (created by users) target type
        // is resolved from the method's return type
        final Type returnType = method.getGenericReturnType();
        final Class<?> rawType = TypeUtils.getRawType(returnType);

        if (rawType == FeedSpec.class) {
            final Type[] typeArgs = ((ParameterizedType) returnType).getActualTypeArguments();
            return TypeUtils.getRawType(typeArgs[0]);
        }
        throw Fail.withUsageError(invalidSpecMethod(method));
    }
}

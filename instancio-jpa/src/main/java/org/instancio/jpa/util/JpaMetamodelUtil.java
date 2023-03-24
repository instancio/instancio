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
package org.instancio.jpa.util;

import com.blazebit.reflection.ReflectionUtils;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import org.jetbrains.annotations.Nullable;

public final class JpaMetamodelUtil {

    @Nullable
    public static Object resolveAttributeValue(Object entity, Attribute<?, ?> attribute) {
        try {
            // TODO: allow configuring field access instead of getter access
            Method getter = ReflectionUtils.getGetter(entity.getClass(), attribute.getName());
            if (!getter.isAccessible()) {
                getter.setAccessible(true);
            }
            return getter.invoke(entity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setAttributeValue(Object entity, Attribute<?, ?> attribute, @Nullable Object value) {
        try {
            // TODO: allow configuring field access instead of getter access
            Method setter = ReflectionUtils.getSetter(entity.getClass(), attribute.getName());
            if (!setter.isAccessible()) {
                setter.setAccessible(true);
            }
            setter.invoke(entity, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static String resolveMappedBy(Member member) {
        if (member instanceof Field) {
            return resolveMappedBy((Field) member);
        } else if (member instanceof Method) {
            return resolveMappedBy((Method) member);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static String resolveMappedBy(Field field) {
        OneToOne oneToOne = field.getAnnotation(OneToOne.class);
        if (oneToOne != null) {
            return oneToOne.mappedBy();
        }
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        if (oneToMany != null) {
            return oneToMany.mappedBy();
        }
        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
        return manyToMany == null || manyToMany.mappedBy().isEmpty() ? null : manyToMany.mappedBy();
    }

    private static String resolveMappedBy(Method method) {
        OneToOne oneToOne = method.getAnnotation(OneToOne.class);
        if (oneToOne != null) {
            return oneToOne.mappedBy();
        }
        OneToMany oneToMany = method.getAnnotation(OneToMany.class);
        if (oneToMany != null) {
            return oneToMany.mappedBy();
        }
        ManyToMany manyToMany = method.getAnnotation(ManyToMany.class);
        return manyToMany == null || manyToMany.mappedBy().isEmpty() ? null : manyToMany.mappedBy();
    }

    public static <T extends Annotation> T getAnnotation(Attribute<?, ?> attr, Class<T> annotationClass) {
        Member member = attr.getJavaMember();
        if (member instanceof Field) {
            return ((Field) member).getAnnotation(annotationClass);
        } else if (member instanceof Method) {
            return ((Method) member).getAnnotation(annotationClass);
        } else {
            return null;
        }
    }

    public static SingularAttribute<?, ?> resolveIdAttribute(EntityType<?> entityType, String attributeName) {
        if (entityType.hasSingleIdAttribute()) {
            SingularAttribute<?, ?> attr = entityType.getId(entityType.getIdType().getJavaType());
            return attr.getName().equals(attributeName) ? attr : null;
        } else {
            return entityType.getIdClassAttributes().stream()
                .filter(attr -> attr.getName().equals(attributeName))
                .findAny()
                .orElse(null);
        }
    }
}

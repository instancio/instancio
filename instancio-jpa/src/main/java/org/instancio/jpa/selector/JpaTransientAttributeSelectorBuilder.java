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
package org.instancio.jpa.selector;

import com.blazebit.reflection.ReflectionUtils;
import jakarta.persistence.Transient;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Predicate;
import org.instancio.Node;
import org.instancio.PredicateSelector;
import org.instancio.TargetSelector;
import org.instancio.internal.selectors.PredicateSelectorImpl;
import org.instancio.internal.selectors.SelectorBuilder;
import org.instancio.internal.selectors.SelectorTargetKind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaTransientAttributeSelectorBuilder implements TargetSelector, SelectorBuilder {
    private static Logger LOG = LoggerFactory.getLogger(JpaTransientAttributeSelectorBuilder.class);

    private final Metamodel metamodel;
    private static final Function<Metamodel, Predicate<Node>> JPA_TRANSIENT_PREDICATE = metamodel -> node -> {
        if (node.getParentTargetClass() != null && node.getField() != null) {
            try {
                ManagedType<?> managedType = metamodel.managedType(node.getParentTargetClass());
                return node.getField().getAnnotation(Transient.class) != null
                    || hasTransientGetter(managedType, node.getField());
            } catch (IllegalArgumentException e) {
                LOG.trace(null, e);
                return false;
            }
        }
        return false;
    };

    private static boolean hasTransientGetter(ManagedType<?> managedType, Field field) {
        Method getter = ReflectionUtils.getGetter(managedType.getJavaType(), field.getName());
        return getter != null && getter.getAnnotation(Transient.class) != null;
    }

    private JpaTransientAttributeSelectorBuilder(Metamodel metamodel) {
        this.metamodel = metamodel;
    }

    public static JpaTransientAttributeSelectorBuilder jpaTransient(Metamodel metamodel) {
        return new JpaTransientAttributeSelectorBuilder(metamodel);
    }

    @Override
    public PredicateSelector build() {
        return new PredicateSelectorImpl(
            SelectorTargetKind.NODE,
            null,
            null,
            JPA_TRANSIENT_PREDICATE.apply(metamodel),
            "jpaTransient()"
        );
    }
}

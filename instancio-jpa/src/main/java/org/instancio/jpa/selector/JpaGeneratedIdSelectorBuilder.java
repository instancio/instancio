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

import static org.instancio.jpa.util.JpaMetamodelUtil.getAnnotation;
import static org.instancio.jpa.util.JpaMetamodelUtil.resolveIdAttribute;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.SingularAttribute;
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

public class JpaGeneratedIdSelectorBuilder implements TargetSelector, SelectorBuilder {
    private static Logger LOG = LoggerFactory.getLogger(JpaGeneratedIdSelectorBuilder.class);

    private final Metamodel metamodel;
    private static final Function<Metamodel, Predicate<Node>> JPA_GENERATED_ID_PREDICATE
        = metamodel -> node -> {
        if (node.getParentTargetClass() != null && node.getField() != null) {
            try {
                EntityType<?> entityType = metamodel.entity(node.getParentTargetClass());
                SingularAttribute<?, ?> idAttr = resolveIdAttribute(entityType, node.getField().getName());
                return idAttr != null && getAnnotation(idAttr, GeneratedValue.class) != null;
            } catch (IllegalArgumentException e) {
                LOG.trace(null, e);
                return false;
            }
        }
        return false;
    };

    private JpaGeneratedIdSelectorBuilder(Metamodel metamodel) {
        this.metamodel = metamodel;
    }

    public static JpaGeneratedIdSelectorBuilder jpaGeneratedId(Metamodel metamodel) {
        return new JpaGeneratedIdSelectorBuilder(metamodel);
    }

    @Override
    public PredicateSelector build() {
        return new PredicateSelectorImpl(
            SelectorTargetKind.NODE,
            null,
            null,
            JPA_GENERATED_ID_PREDICATE.apply(metamodel),
            "jpaGeneratedId()"
        );
    }
}

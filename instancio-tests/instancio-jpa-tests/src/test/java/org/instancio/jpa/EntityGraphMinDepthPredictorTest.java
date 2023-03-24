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
package org.instancio.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Persistence;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class EntityGraphMinDepthPredictorTest {

    private static EntityManagerFactory emf;
    private static EntityGraphMinDepthPredictor entityGraphMinDepthPredictor;

    @BeforeAll
    static void setup() {
        emf = Persistence.createEntityManagerFactory("EntityGraphMinDepthPredictorTestPu");
        entityGraphMinDepthPredictor = new EntityGraphMinDepthPredictor(emf.getMetamodel());
    }

    @AfterAll
    static void tearDownEmf() {
        emf.close();
    }

    @Test
    void noAssociations() {
        // When
        int predictedMaxDepth = entityGraphMinDepthPredictor.predictRequiredDepth(SingleLevelEntity.class);

        // Then
        assertThat(predictedMaxDepth).isEqualTo(1);
    }

    @Test
    void oneToMany() {
        // When
        int predictedMaxDepth = entityGraphMinDepthPredictor.predictRequiredDepth(Order.class);

        // Then
        assertThat(predictedMaxDepth).isEqualTo(1);
    }

    @Test
    void manyToOne() {
        // When
        int predictedMaxDepth = entityGraphMinDepthPredictor.predictRequiredDepth(OrderItem.class);

        // Then
        assertThat(predictedMaxDepth).isEqualTo(2);
    }

    @Test
    void manyToOne_optional() {
        // When
        int predictedMaxDepth = entityGraphMinDepthPredictor.predictRequiredDepth(OrderItemWithOptionalOrder.class);

        // Then
        assertThat(predictedMaxDepth).isEqualTo(1);
    }

    @Test
    void embeddable_optionalComponent() {
        // When
        int predictedMaxDepth = entityGraphMinDepthPredictor.predictRequiredDepth(EmbeddableParent1.class);

        // Then
        assertThat(predictedMaxDepth).isEqualTo(1);
    }

    @Test
    void embeddable_mandatoryComponent() {
        // When
        int predictedMaxDepth = entityGraphMinDepthPredictor.predictRequiredDepth(EmbeddableParent2.class);

        // Then
        assertThat(predictedMaxDepth).isEqualTo(2);
    }

    @Test
    @Disabled("Need to implement support for attribute overrides")
    void embeddable_mandatoryComponentByOverride() {
        // When
        int predictedMaxDepth = entityGraphMinDepthPredictor.predictRequiredDepth(EmbeddableParent3.class);

        // Then
        assertThat(predictedMaxDepth).isEqualTo(2);
    }

    @Entity
    @Getter
    @Setter
    public static class SingleLevelEntity {
        @Id
        private Long id;
    }

    @Entity
    @Getter
    @Setter
    public static class Order {
        @Id
        private Long id;
        @OneToMany
        private Set<OrderItem> orderItems = new HashSet<>(0);
    }

    @Entity
    @Getter
    @Setter
    public static class OrderItem {
        @Id
        private Long id;
        @ManyToOne(optional = false)
        private Order order;
    }

    @Entity
    @Getter
    @Setter
    public static class OrderItemWithOptionalOrder {
        @Id
        private Long id;
        @ManyToOne
        private Order order;
    }

    @Entity
    @Getter
    @Setter
    public static class EmbeddableParent1 {
        @Id
        private Long id;
        @Embedded
        private EmbeddableWithOptionalComponent embeddable;
    }

    @Embeddable
    @Getter
    @Setter
    public static class EmbeddableWithOptionalComponent {
        private String name;
    }

    @Entity
    @Getter
    @Setter
    public static class EmbeddableParent2 {
        @Id
        private Long id;
        @Embedded
        private EmbeddableWithMandatoryComponent embeddable;
    }

    @Embeddable
    @Getter
    @Setter
    public static class EmbeddableWithMandatoryComponent {
        @Column(nullable = false)
        private String name;
    }

    @Entity
    @Getter
    @Setter
    public static class EmbeddableParent3 {
        @Id
        private Long id;
        @Embedded
        @AttributeOverride(name = "name", column = @Column(nullable = false))
        private EmbeddableWithOptionalComponent embeddable;
    }
}

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
import static org.instancio.Select.fields;
import static org.instancio.Select.root;
import static org.instancio.jpa.selector.JpaGeneratedIdSelectorBuilder.jpaGeneratedId;
import static org.instancio.jpa.selector.JpaOptionalAttributeSelectorBuilder.jpaOptionalAttribute;
import static org.instancio.jpa.selector.JpaTransientAttributeSelectorBuilder.jpaTransient;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Persistence;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import lombok.Getter;
import lombok.Setter;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Model;
import org.instancio.jpa.settings.JpaKeys;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InstancioExtension.class)
public class EntityGraphPersisterTest {

    private static EntityManagerFactory emf;
    private EntityManager entityManager;
    private EntityGraphPersister entityGraphPersister;

    @BeforeAll
    static void createEmf() {
        emf = Persistence.createEntityManagerFactory("EntityGraphPersisterTestPu");
    }

    @BeforeEach
    void setup() {
        entityManager = emf.createEntityManager();
        entityGraphPersister = new EntityGraphPersister(entityManager);
    }

    @Test
    public void testFlatOrder() {
        // Given
        FlatOrder order = Instancio.of(jpaModel(FlatOrder.class)).create();

        // When
        doInTransaction(() -> {
            entityGraphPersister.persist(order);
        });

        // Then
        FlatOrder actualOrder = doInTransaction(() -> entityManager.find(FlatOrder.class, order.getId()));
        assertThat(actualOrder).isNotNull();
    }

    @Test
    public void testOrder() {
        // Given
        Order order = Instancio.of(jpaModel(Order.class)).create();

        // When
        doInTransaction(() -> {
            entityGraphPersister.persist(order);
        });

        // Then
        Order actualOrder = doInTransaction(() -> entityManager.find(Order.class, order.getId()));
        assertThat(actualOrder).isNotNull();
    }

    @Test
    @Seed(738392784923318094L)
    public void testOrderWithDescription() {
        // Given
        OrderWithDescription1 order = Instancio.of(jpaModel(OrderWithDescription1.class))
            .set(fields().named("id"), null)
            .create();

        // When
        doInTransaction(() -> {
            entityGraphPersister.persist(order);
        });

        // Then
        OrderWithDescription1
            actualOrder = doInTransaction(() -> entityManager.find(OrderWithDescription1.class, order.getId()));
        assertThat(actualOrder).isNotNull();
    }

    private static <T> Model<T> jpaModel(Class<T> entityClass) {
        InstancioApi<T> instancioApi = Instancio.of(entityClass)
            .lenient()
            .withNullable(jpaOptionalAttribute(emf.getMetamodel()))
            .set(jpaTransient(emf.getMetamodel()), null)
            .set(jpaGeneratedId(emf.getMetamodel()), null)
            .withSettings(Settings.create().set(JpaKeys.METAMODEL, emf.getMetamodel()));

        EntityGraphMinDepthPredictor entityGraphMinDepthPredictor =
            new EntityGraphMinDepthPredictor(emf.getMetamodel());
        int minDepth = entityGraphMinDepthPredictor.predictRequiredDepth(entityClass);
        instancioApi.withMaxDepth(minDepth);

        return instancioApi
            .onComplete(root(), (root) -> {
                new EntityGraphShrinker(emf.getMetamodel()).shrink(root);
                new EntityGraphAssociationFixer(emf.getMetamodel()).fixAssociations(root);
            })
            .toModel();
    }

    private <V> V doInTransaction(Callable<V> callable) {
        EntityTransaction tx = null;
        try {
            tx = entityManager.getTransaction();
            tx.begin();
            V result = callable.call();
            tx.commit();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
        }
    }

    private void doInTransaction(Runnable runnable) {
        doInTransaction(() -> {
            runnable.run();
            return null;
        });
    }

    @Entity
    @Table(name = "service_order")
    @Getter
    @Setter
    public static class FlatOrder {

        @Id
        @GeneratedValue
        private Long id;

        @OneToMany
        private Set<OrderItem> orderItems = new HashSet<>(0);
    }

    @Entity
    @Table(name = "service_order")
    @Getter
    @Setter
    public static class Order extends AbstractEntity {

        private Set<OrderItem> orderItems = new HashSet<>(0);

        @Id
        @GeneratedValue
        public Long getId() {
            return super.getId();
        }

        @OneToMany
        public Set<OrderItem> getOrderItems() {
            return orderItems;
        }
    }

    @Getter
    @Setter
    public static abstract class AbstractEntity {

        private Long id;
    }

    @Entity
    @Table(name = "order_item")
    @Getter
    @Setter
    public static class OrderItem {
        @Id
        @GeneratedValue
        private Long id;
    }

    @Getter
    @Setter
    @Entity
    @AttributeOverride(name = "description", column = @Column(nullable = false))
    public static class OrderWithDescription1 extends AbstractOrder { }

    @Getter
    @Setter
    @MappedSuperclass
    public static abstract class AbstractOrder extends AbstractEntity {

        private String description;

        @Id
        @GeneratedValue
        public Long getId() {
            return super.getId();
        }

        @Column(nullable = true)
        public String getDescription() {
            return description;
        }
    }

    @Getter
    @Setter
    @Entity
    public static class OrderWithDescription2 extends AbstractOrder { }
}

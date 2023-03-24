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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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
import org.junit.jupiter.api.Test;

public class EntityGraphShrinkerTest {

    private static EntityManagerFactory emf;
    private static EntityGraphShrinker entityGraphShrinker;

    @BeforeAll
    static void setup() {
        emf = Persistence.createEntityManagerFactory("EntityGraphShrinkerTestPu");
        entityGraphShrinker = new EntityGraphShrinker(emf.getMetamodel());
    }

    @AfterAll
    static void tearDownEmf() {
        emf.close();
    }

    @Test
    void oneToMany_unset() {
        // Given
        Order order = new Order();

        // When / Then
        assertThatNoException().isThrownBy(() -> entityGraphShrinker.shrink(order));
    }

    @Test
    void mandatoryManyToOne_unset() {
        // Given
        OrderItem orderItem = new OrderItem();

        // When / Then
        assertThatThrownBy(() -> entityGraphShrinker.shrink(orderItem)).hasMessageContaining("Cannot shrink");
    }

    @Test
    public void mandatoryManyToOne_set() {
        // Given
        Order order = new Order();
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);

        // When / Then
        assertThatNoException().isThrownBy(() -> entityGraphShrinker.shrink(orderItem));
        assertThat(orderItem.getOrder()).isEqualTo(order);
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
}

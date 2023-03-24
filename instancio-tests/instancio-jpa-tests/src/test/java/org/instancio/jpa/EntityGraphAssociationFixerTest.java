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

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Persistence;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EntityGraphAssociationFixerTest {

    private static EntityManagerFactory emf;
    private static EntityGraphAssociationFixer entityGraphAssociationFixer;

    @BeforeAll
    static void setup() {
        emf = Persistence.createEntityManagerFactory("EntityGraphAssociationFixerTestPu");
        entityGraphAssociationFixer = new EntityGraphAssociationFixer(emf.getMetamodel());
    }

    @AfterAll
    static void tearDownEmf() {
        emf.close();
    }

    @Test
    void oneToMany() {
        // Given
        Order order1 = new Order();
        Order order2 = new Order();
        OrderItem orderItem = new OrderItem();
        order1.getOrderItems().add(orderItem);
        orderItem.setOrder(order2);

        // When
        entityGraphAssociationFixer.fixAssociations(order1);

        // Then
        assertThat(order1.getOrderItems()).containsExactly(orderItem);
        assertThat(orderItem.getOrder()).isEqualTo(order1);
    }

    @Test
    void oneToMany_oneSided() {
        // Given
        Order order1 = new Order();
        Order order2 = new Order();
        OrderItem orderItem = new OrderItem();
        order1.getOneSidedOrderItems().add(orderItem);
        orderItem.setOrder(order2);

        // When
        entityGraphAssociationFixer.fixAssociations(order1);

        // Then
        assertThat(order1.getOneSidedOrderItems()).containsExactly(orderItem);
        assertThat(orderItem.getOrder()).isEqualTo(order2);
    }

    @Test
    void oneToOne_ownedSide() {
        // Given
        Order order1 = new Order();
        Order order2 = new Order();
        order1.setPrevious(order2);

        // When
        entityGraphAssociationFixer.fixAssociations(order1);

        // Then
        assertThat(order1.getPrevious()).isEqualTo(order2);
        assertThat(order2.getNext()).isEqualTo(order1);
    }

    @Test
    void oneToOne_owningSide() {
        // Given
        Order order1 = new Order();
        Order order2 = new Order();
        order1.setNext(order2);

        // When
        entityGraphAssociationFixer.fixAssociations(order1);

        // Then
        assertThat(order1.getNext()).isEqualTo(order2);
        assertThat(order2.getPrevious()).isNull();
    }

    @Test
    void manyToOne() {
        // Given
        OrderItem orderItem1 = new OrderItem();
        OrderItem orderItem2 = new OrderItem();
        Order order = new Order();
        orderItem1.setOrder(order);
        order.getOrderItems().add(orderItem2);

        // When
        entityGraphAssociationFixer.fixAssociations(orderItem1);

        // Then
        assertThat(orderItem1.getOrder()).isEqualTo(order);
        assertThat(order.getOrderItems()).containsExactlyInAnyOrder(orderItem1, orderItem2);
    }

    @Test
    void manyToMany_owningSide() {
        // Given
        Order order1 = new Order();
        Order order2 = new Order();
        Person person = new Person();
        order1.getContacts().add(person);
        person.getOrders().add(order2);

        // When
        entityGraphAssociationFixer.fixAssociations(order1);

        // Then
        assertThat(order1.getContacts()).containsExactly(person);
        assertThat(person.getOrders()).containsExactlyInAnyOrder(order1, order2);
    }

    @Test
    void manyToMany_ownedSide() {
        // Given
        Person person1 = new Person();
        Person person2 = new Person();
        Order order = new Order();
        person1.getOrders().add(order);
        order.getContacts().add(person2);

        // When
        entityGraphAssociationFixer.fixAssociations(person1);
        // Then
        assertThat(person1.getOrders()).containsExactly(order);
        assertThat(order.getContacts()).containsExactlyInAnyOrder(person1, person2);
    }

    @Entity
    @Getter
    @Setter
    public static class Order {
        @Id
        private Long id;
        @OneToMany(mappedBy = "order")
        private Set<OrderItem> orderItems = new HashSet<>(0);
        @OneToMany
        private Set<OrderItem> oneSidedOrderItems = new HashSet<>(0);
        @OneToOne
        private Order next;
        @OneToOne(mappedBy = "next")
        private Order previous;
        @ManyToMany
        private Set<Person> contacts = new HashSet<>(0);
    }

    @Entity
    @Getter
    @Setter
    public static class OrderItem {
        @Id
        private Long id;
        @ManyToOne
        private Order order;
    }

    @Entity
    @Getter
    @Setter
    public static class Person {
        @Id
        private Long id;
        @ManyToMany(mappedBy = "contacts")
        private Set<Order> orders = new HashSet<>(0);
    }
}

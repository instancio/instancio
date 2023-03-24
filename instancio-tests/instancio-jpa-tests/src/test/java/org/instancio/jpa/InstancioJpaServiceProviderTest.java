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
import jakarta.persistence.Persistence;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.instancio.Instancio;
import org.instancio.jpa.settings.JpaKeys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class InstancioJpaServiceProviderTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    static void setup() {
        emf = Persistence.createEntityManagerFactory("InstancioJpaServiceProviderTestPu");
    }

    @Test
    public void testIdSequence() {
        // Given
        Settings settings = Settings.defaults().set(JpaKeys.METAMODEL, emf.getMetamodel());

        // When
        List<Order> orders = Instancio.ofList(Instancio.of(Order.class)
            .withSettings(settings)
            .toModel()).size(2).create();

        // Then
        assertThat(orders.get(0).getId()).isEqualTo(0L);
        assertThat(orders.get(1).getId()).isEqualTo(1L);
    }

    @Entity
    @Getter
    @Setter
    public static class Order {
        @Id
        private Long id;
    }
}

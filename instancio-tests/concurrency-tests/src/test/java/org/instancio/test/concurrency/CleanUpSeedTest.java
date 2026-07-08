package org.instancio.test.concurrency;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.assertj.core.api.Assertions.assertThat;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
class CleanUpSeedTest {
    private final long SEED = -1L;

    @Order(1)
    @Nested
    class InstancioExtensionAfter {

        @ExtendWith(InstancioExtension.class)
        @Order(1)
        @Test
        @Seed(SEED)
        void setSeedInCurrentThread() {
            var stringResult = Instancio.of(String.class).asResult();
            assertThat(stringResult.getSeed()).isEqualTo(SEED);
        }

        @ExtendWith(InstancioExtension.class)
        @Order(2)
        @Test
        void ignoreAlreadyPresentSeedInCurrentThread() {
            var stringResult = Instancio.of(String.class).asResult();
            assertThat(stringResult.getSeed()).isNotEqualTo(SEED);
        }
    }

    @Order(2)
    @Nested
    class SimpleCreationAfter {

        @ExtendWith(InstancioExtension.class)
        @Order(1)
        @Test
        @Seed(SEED)
        void setSeedInCurrentThread() {
            var stringResult = Instancio.of(String.class).asResult();
            assertThat(stringResult.getSeed()).isEqualTo(SEED);
        }

        @Order(2)
        @Test
        void ignoreAlreadyPresentSeedInCurrentThread() {
            var stringResult = Instancio.of(String.class).asResult();
            assertThat(stringResult.getSeed()).isNotEqualTo(SEED);
        }
    }
}

package org.instancio.test.concurrency;

import org.instancio.Instancio;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
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
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
class CleanUpSettingsTest {

    @Order(1)
    @Nested
    class InstancioSourceBefore {

        @Order(1)
        @Nested
        class InstancioSourceAfter {

            @Order(1)
            @Nested
            class BeforeWrapper {

                @WithSettings
                private static final Settings settings = Settings.create()
                        .set(Keys.STRING_MAX_LENGTH, 15)
                        .set(Keys.STRING_MIN_LENGTH, 15);

                @ParameterizedTest
                @InstancioSource(samples = 1)
                void setSettingsInCurrentThread(String string) {
                    assertThat(string).hasSize(15);
                }
            }

            @Order(2)
            @Nested
            class AfterWrapper {

                @ParameterizedTest
                @InstancioSource(samples = 1)
                void ignoreAlreadyPresentSettingsInCurrentThread(String string) {
                    assertThat(string).hasSizeLessThanOrEqualTo(10);
                }

            }
        }

        @Order(2)
        @Nested
        class InstancioExtensionAfter {

            @Order(1)
            @Nested
            class BeforeWrapper {

                @WithSettings
                private static final Settings settings = Settings.create()
                        .set(Keys.STRING_MAX_LENGTH, 15)
                        .set(Keys.STRING_MIN_LENGTH, 15);

                @ParameterizedTest
                @InstancioSource(samples = 1)
                void setSettingsInCurrentThread(String string) {
                    assertThat(string).hasSize(15);
                }
            }

            @Order(2)
            @Nested
            class AfterWrapper {

                @ExtendWith(InstancioExtension.class)
                @Test
                void ignoreAlreadyPresentSettingsInCurrentThread(@Given String string) {
                    assertThat(string).hasSizeLessThanOrEqualTo(10);
                }
            }
        }

        @Order(3)
        @Nested
        class SimpleCreationAfter {

            @Order(1)
            @Nested
            class BeforeWrapper {

                @WithSettings
                private static final Settings settings = Settings.create()
                        .set(Keys.STRING_MAX_LENGTH, 15)
                        .set(Keys.STRING_MIN_LENGTH, 15);

                @ParameterizedTest
                @InstancioSource(samples = 1)
                void setSettingsInCurrentThread(String string) {
                    assertThat(string).hasSize(15);
                }
            }

            @Order(2)
            @Nested
            class AfterWrapper {

                @Test
                void ignoreAlreadyPresentSettingsInCurrentThread() {
                    assertThat(Instancio.create(String.class)).hasSizeLessThanOrEqualTo(10);
                }
            }
        }
    }

    @Order(2)
    @Nested
    class InstancioExtensionBefore {

        @Order(1)
        @Nested
        class InstancioSourceAfter {

            @Order(1)
            @Nested
            class BeforeWrapper {

                @WithSettings
                private static final Settings settings = Settings.create()
                        .set(Keys.STRING_MAX_LENGTH, 15)
                        .set(Keys.STRING_MIN_LENGTH, 15);

                @ExtendWith(InstancioExtension.class)
                @Test
                void setSettingsInCurrentThread(@Given String string) {
                    assertThat(string).hasSize(15);
                }
            }

            @Order(2)
            @Nested
            class AfterWrapper {

                @ParameterizedTest
                @InstancioSource(samples = 1)
                void ignoreAlreadyPresentSettingsInCurrentThread(String string) {
                    assertThat(string).hasSizeLessThanOrEqualTo(10);
                }

            }
        }

        @Order(2)
        @Nested
        class InstancioExtensionAfter {

            @Order(1)
            @Nested
            class BeforeWrapper {

                @WithSettings
                private static final Settings settings = Settings.create()
                        .set(Keys.STRING_MAX_LENGTH, 15)
                        .set(Keys.STRING_MIN_LENGTH, 15);

                @ExtendWith(InstancioExtension.class)
                @Test
                void setSettingsInCurrentThread(@Given String string) {
                    assertThat(string).hasSize(15);
                }
            }

            @Order(2)
            @Nested
            class AfterWrapper {

                @ExtendWith(InstancioExtension.class)
                @Test
                void ignoreAlreadyPresentSettingsInCurrentThread(@Given String string) {
                    assertThat(string).hasSizeLessThanOrEqualTo(10);
                }
            }
        }

        @Order(3)
        @Nested
        class SimpleCreationAfter {

            @Order(1)
            @Nested
            class BeforeWrapper {

                @WithSettings
                private static final Settings settings = Settings.create()
                        .set(Keys.STRING_MAX_LENGTH, 15)
                        .set(Keys.STRING_MIN_LENGTH, 15);

                @ExtendWith(InstancioExtension.class)
                @Test
                void setSettingsInCurrentThread(@Given String string) {
                    assertThat(string).hasSize(15);
                }
            }

            @Order(2)
            @Nested
            class AfterWrapper {

                @Test
                void ignoreAlreadyPresentSettingsInCurrentThread() {
                    assertThat(Instancio.create(String.class)).hasSizeLessThanOrEqualTo(10);
                }
            }
        }
    }
}

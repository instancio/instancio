/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.junit;

import org.instancio.exception.InstancioApiException;
import org.instancio.internal.util.Sonar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.junit.platform.testkit.engine.Events;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.testkit.engine.EventConditions.event;
import static org.junit.platform.testkit.engine.EventConditions.finishedWithFailure;
import static org.junit.platform.testkit.engine.EventConditions.test;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.instanceOf;
import static org.junit.platform.testkit.engine.TestExecutionResultConditions.message;

/**
 * Verifies that annotating a static field with {@code @Given}
 * produces the expected error message.
 */
class InstancioExtensionStaticFieldAnnotatedWithGivenTest {

    @Test
    void verify() {
        final Events events = EngineTestKit.engine("junit-jupiter")
                .selectors(selectClass(SampleTest.class))
                .execute()
                .testEvents();

        events.assertThatEvents().haveExactly(1,
                event(test("testMethod"), finishedWithFailure(
                        instanceOf(InstancioApiException.class),
                        message(msg -> msg.contains(
                                "@Given annotation is not supported for static fields")))));
    }

    @ExtendWith(InstancioExtension.class)
    static class SampleTest {

        @Given
        private static String value;

        @Test
        @SuppressWarnings(Sonar.ADD_ASSERTION)
        void testMethod() {
            // intentionally empty: an exception should be
            // thrown before this test gets a chance to run
        }
    }
}
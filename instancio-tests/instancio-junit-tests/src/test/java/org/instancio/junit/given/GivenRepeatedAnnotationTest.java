/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.junit.given;

import org.instancio.junit.Given;
import org.instancio.junit.GivenProvider;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repeating {@code @Given} should be equivalent to listing all the providers
 * in a single declaration. A repeated annotation is stored in its container,
 * so this also verifies that annotated elements are still detected at all.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(InstancioExtension.class)
class GivenRepeatedAnnotationTest {

    private static final Set<String> fieldResults = new HashSet<>();
    private static final Set<String> parameterResults = new HashSet<>();

    @Given(FooProvider.class)
    @Given(BarProvider.class)
    private String repeatedField;

    @Order(1)
    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void repeatedOnField() {
        assertThat(repeatedField).isIn("foo", "bar");
        fieldResults.add(repeatedField);
    }

    @Order(1)
    @RepeatedTest(Constants.SAMPLE_SIZE_DD)
    void repeatedOnParameter(@Given(FooProvider.class) @Given(BarProvider.class) final String value) {
        assertThat(value).isIn("foo", "bar");
        parameterResults.add(value);
    }

    @Order(2)
    @Test
    void everyProviderShouldBeSelected() {
        assertThat(fieldResults).containsOnly("foo", "bar");
        assertThat(parameterResults).containsOnly("foo", "bar");
    }

    @Test
    void repeatedAnnotationShouldCombineWithMetaAnnotation(
            @Given(FooProvider.class) @MetaBar final String value) {

        assertThat(value).isIn("foo", "bar");
    }

    @Test
    void repeatedEmptyAnnotationShouldGenerateRandomValue(@Given @Given final String value) {
        assertThat(value).isNotBlank().isNotIn("foo", "bar");
    }

    @Given(BarProvider.class)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface MetaBar {}

    private static class FooProvider implements GivenProvider {
        @Override
        public Object provide(final ElementContext context) {
            return "foo";
        }
    }

    private static class BarProvider implements GivenProvider {
        @Override
        public Object provide(final ElementContext context) {
            return "bar";
        }
    }
}

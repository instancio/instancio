/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.test.support.asserts;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.StringAssert;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnusedReturnValue")
public class UnusedSelectorsAssert extends StringAssert {

    private UnusedSelectorsAssert(String actual) {
        super(actual);
    }

    public static UnusedSelectorsAssert assertUnusedSelectorMessage(String actual) {
        return new UnusedSelectorsAssert(actual);
    }

    public UnusedSelectorsAssert hasUnusedSelectorCount(final int expectedCount) {
        final int actualCount = StringUtils.countMatches(actual, "Selector[");
        assertThat(actualCount)
                .as("Expected %s selectors, but found %s. Actual message:%n%s",
                        expectedCount, actualCount, actual)
                .isEqualTo(expectedCount);
        return this;
    }

    public UnusedSelectorsAssert containsUnusedSelector(final Class<?> targetClass, final String field) {
        final String expected = format(targetClass, field);
        assertThat(StringUtils.countMatches(actual, expected))
                .as("Expected to match one '%s'. Actual message:%n%s", expected, actual)
                .isOne();
        return this;
    }

    public UnusedSelectorsAssert containsUnusedSelector(final Class<?> targetClass) {
        final String expected = format(targetClass);
        assertThat(StringUtils.countMatches(actual, expected))
                .as("Expected to match one '%s'. Actual message:%n%s", expected, actual)
                .isOne();
        return this;
    }

    public UnusedSelectorsAssert containsOnly(final ApiMethod... apiMethods) {
        final String expectedMethodStr = Arrays.stream(apiMethods)
                .map(it -> " - " + it.getDescription())
                .collect(joining(System.lineSeparator()));

        final int actualNumHeadings = StringUtils.countMatches(actual, " -> ");

        assertThat(actualNumHeadings)
                .as("Expected %s API method(s):%n%n%s%n%nbut found %s. Actual message:%n%s",
                        apiMethods.length, expectedMethodStr, actualNumHeadings, actual)
                .isEqualTo(apiMethods.length);

        for (ApiMethod apiMethod : apiMethods) {
            assertThat(StringUtils.countMatches(actual, apiMethod.getHeading()))
                    .as("Expected to match API method '%s'. Actual message:%n%s",
                            apiMethod.getDescription(), actual)
                    .isOne();
        }

        return this;
    }

    public UnusedSelectorsAssert print() {
        System.out.println(actual); // NOSONAR
        return this;
    }

    private static String format(final Class<?> targetClass) {
        // NOTE: unterminated '[' since we're not asserting scope(s)
        return String.format("Selector[(%s)", targetClass.getSimpleName());
    }

    private static String format(final Class<?> targetClass, final String field) {
        return String.format("Selector[(%s, \"%s\")]", targetClass.getSimpleName(), field);
    }

    public enum ApiMethod {
        IGNORE("ignore()"),
        WITH_NULLABLE("withNullable()"),
        GENERATE_SET_SUPPLY("generate(), set(), or supply()"),
        ON_COMPLETE("onComplete()");

        private final String description;

        ApiMethod(final String description) {
            this.description = description;
        }

        String getDescription() {
            return description;
        }

        String getHeading() {
            return String.format(" -> %s", description);
        }
    }
}
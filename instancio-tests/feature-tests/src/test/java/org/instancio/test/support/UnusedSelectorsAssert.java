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
package org.instancio.test.support;

import org.assertj.core.api.ThrowableAssert;
import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.internal.ApiMethodSelector;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Assertions for regular and predicate selectors.
 * <p>
 * - Regular selectors implement equals() and are matched by object equality.
 * - Predicate selectors do not implement equals() and are matched by toString representation.
 */
@SuppressWarnings({"NullAway", "UnusedReturnValue"})
public class UnusedSelectorsAssert extends ThrowableAssert<UnusedSelectorException> {

    private UnusedSelectorsAssert(final UnusedSelectorException actual) {
        super(actual);
    }

    public static UnusedSelectorsAssert assertUnusedSelectorMessage(final Throwable actual) {
        assertThat(actual).as("null Throwable").isExactlyInstanceOf(UnusedSelectorException.class);
        return new UnusedSelectorsAssert((UnusedSelectorException) actual);
    }

    public static UnusedSelectorsAssert assertThrowsUnusedSelectorException(final InstancioApi<?> api) {
        final Throwable throwable = catchThrowable(api::create);
        return assertUnusedSelectorMessage(throwable);
    }

    public UnusedSelectorsAssert hasUnusedSelectorCount(final int expectedCount) {
        int actualCount = actual.getUnusedSelectorMap().values()
                .stream()
                .mapToInt(Collection::size)
                .sum();

        assertThat(actualCount)
                .as("Expected %s selectors, but found %s. Actual message:%n%s",
                        expectedCount, actualCount, actual)
                .isEqualTo(expectedCount);
        return this;
    }

    public static String line(final Class<?> klass, final int line) {
        return klass.getSimpleName() + ".java:" + line;
    }

    public UnusedSelectorsAssert ignoreSelector(final TargetSelector selector, final String stackTraceLine) {
        return assertUnusedSelectorAt(selector.toString(), stackTraceLine,
                actual.getUnusedSelectorMap().get(ApiMethodSelector.IGNORE));
    }

    public UnusedSelectorsAssert withNullableSelector(final TargetSelector selector, final String stackTraceLine) {
        return assertUnusedSelectorAt(selector.toString(), stackTraceLine,
                actual.getUnusedSelectorMap().get(ApiMethodSelector.WITH_NULLABLE));
    }

    public UnusedSelectorsAssert generateSelector(final TargetSelector selector, final String stackTraceLine) {
        return assertUnusedSelectorAt(selector.toString(), stackTraceLine,
                actual.getUnusedSelectorMap().get(ApiMethodSelector.GENERATE));
    }

    public UnusedSelectorsAssert setSelector(final TargetSelector selector, final String stackTraceLine) {
        return assertUnusedSelectorAt(selector.toString(), stackTraceLine,
                actual.getUnusedSelectorMap().get(ApiMethodSelector.SET));
    }

    public UnusedSelectorsAssert supplySelector(final TargetSelector selector, final String stackTraceLine) {
        return assertUnusedSelectorAt(selector.toString(), stackTraceLine,
                actual.getUnusedSelectorMap().get(ApiMethodSelector.SUPPLY));
    }

    public UnusedSelectorsAssert onCompleteSelector(final TargetSelector selector, final String stackTraceLine) {
        return assertUnusedSelectorAt(selector.toString(), stackTraceLine,
                actual.getUnusedSelectorMap().get(ApiMethodSelector.ON_COMPLETE));
    }

    public UnusedSelectorsAssert subtypeSelector(final TargetSelector selector, final String stackTraceLine) {
        return assertUnusedSelectorAt(selector.toString(), stackTraceLine,
                actual.getUnusedSelectorMap().get(ApiMethodSelector.SUBTYPE));
    }

    //
    // asserts without stacktrace line
    //

    public UnusedSelectorsAssert ignoreSelector(final TargetSelector selector) {
        return assertUnused(selector.toString(), actual.getUnusedSelectorMap().get(ApiMethodSelector.IGNORE));
    }

    public UnusedSelectorsAssert withNullableSelector(final TargetSelector selector) {
        return assertUnused(selector.toString(), actual.getUnusedSelectorMap().get(ApiMethodSelector.WITH_NULLABLE));
    }

    public UnusedSelectorsAssert generateSelector(final TargetSelector selector) {
        return assertUnused(selector.toString(), actual.getUnusedSelectorMap().get(ApiMethodSelector.GENERATE));
    }

    public UnusedSelectorsAssert setSelector(final TargetSelector selector) {
        return assertUnused(selector.toString(), actual.getUnusedSelectorMap().get(ApiMethodSelector.SET));
    }

    public UnusedSelectorsAssert supplySelector(final TargetSelector selector) {
        return assertUnused(selector.toString(), actual.getUnusedSelectorMap().get(ApiMethodSelector.SUPPLY));
    }

    public UnusedSelectorsAssert onCompleteSelector(final TargetSelector selector) {
        return assertUnused(selector.toString(), actual.getUnusedSelectorMap().get(ApiMethodSelector.ON_COMPLETE));
    }

    public UnusedSelectorsAssert subtypeSelector(final TargetSelector selector) {
        return assertUnused(selector.toString(), actual.getUnusedSelectorMap().get(ApiMethodSelector.SUBTYPE));
    }

    private UnusedSelectorsAssert assertUnusedSelectorAt(
            final String selectorDescription,
            final String stackTraceLine,
            final List<TargetSelector> selectors) {

        assertThat(getActualStackTraceLine(selectorDescription)).containsSubsequence(stackTraceLine);
        return assertUnused(selectorDescription, selectors);
    }

    private UnusedSelectorsAssert assertUnused(
            final String selectorDescription,
            final List<TargetSelector> selectors) {

        // We can't look up the selector in the Set use contains() because
        // the passed in 'selector' is unprocessed, and equals() cannot be used
        // to compare processed and unprocessed selectors
        assertThat(selectors.stream()
                .map(Object::toString)
                .anyMatch(it -> it.equals(selectorDescription)))
                .as("Expected selector %s to be unused", selectorDescription)
                .isTrue();

        return this;
    }

    private String getActualStackTraceLine(final String expected) {
        final String message = extractUnusedSelectorsFromExceptionMessage(actual.getMessage());
        final String[] actualLines = message.split(System.lineSeparator());
        String actualStackTraceLine = null;
        for (int i = 0; i < actualLines.length; i++) {
            if (actualLines[i].contains(expected)) {
                actualStackTraceLine = actualLines[i + 1];
                break;
            }
        }

        assertThat(actualStackTraceLine)
                .as("""
                                Could not find a stacktrace line containing the expected string: '%s'
                                
                                The actual unused selector error message is:
                                ---
                                %s
                                ---
                                """,
                        expected, message.trim())
                .isNotNull();
        return actualStackTraceLine;
    }

    private static String extractUnusedSelectorsFromExceptionMessage(final String message) {
        // Remove everything after "possible causes" from the exception message.
        // This is to prevent examples selectors in the error message from being counted.
        return message.substring(0, message.indexOf("Possible causes"));
    }
}
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
package org.instancio.test.support;

import org.assertj.core.api.ThrowableAssert;
import org.instancio.InstancioApi;
import org.instancio.PredicateSelector;
import org.instancio.Selector;
import org.instancio.TargetSelector;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.internal.selectors.SelectorBuilder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Assertions for regular and predicate selectors.
 * <p>
 * - Regular selectors implement equals() and are matched by object equality.
 * - Predicate selectors do not implement equals() and are matched by toString representation.
 */
@SuppressWarnings("UnusedReturnValue")
public class UnusedSelectorsAssert extends ThrowableAssert<UnusedSelectorException> {

    private UnusedSelectorsAssert(final UnusedSelectorException actual) {
        super(actual);
    }

    public static UnusedSelectorsAssert assertUnusedSelectorMessage(final Throwable actual) {
        assertThat(actual).isExactlyInstanceOf(UnusedSelectorException.class);
        return new UnusedSelectorsAssert((UnusedSelectorException) actual);
    }

    public static UnusedSelectorsAssert assertThrowsUnusedSelectorException(final InstancioApi<?> api) {
        final Throwable throwable = catchThrowable(api::create);
        return assertUnusedSelectorMessage(throwable);
    }

    public UnusedSelectorsAssert hasUnusedSelectorCount(final int expectedCount) {
        int actualCount = actual.getIgnored().size()
                + actual.getNullable().size()
                + actual.getGenerators().size()
                + actual.getCallbacks().size()
                + actual.getSubtypes().size();

        assertThat(actualCount)
                .as("Expected %s selectors, but found %s. Actual message:%n%s",
                        expectedCount, actualCount, actual)
                .isEqualTo(expectedCount);
        return this;
    }


    public UnusedSelectorsAssert unusedIgnoreSelectorAt(final TargetSelector selector, final String stackTraceLine) {
        return assertUnusedSelectorAt(selector, stackTraceLine, actual.getIgnored());
    }

    public UnusedSelectorsAssert unusedWithNullableSelectorAt(final TargetSelector selector, final String stackTraceLine) {
        return assertUnusedSelectorAt(selector, stackTraceLine, actual.getNullable());
    }

    public UnusedSelectorsAssert unusedGeneratorSelectorAt(final TargetSelector selector, final String stackTraceLine) {
        return assertUnusedSelectorAt(selector, stackTraceLine, actual.getGenerators());
    }

    public UnusedSelectorsAssert unusedOnCompleteSelectorAt(final TargetSelector selector, final String stackTraceLine) {
        return assertUnusedSelectorAt(selector, stackTraceLine, actual.getCallbacks());
    }

    public UnusedSelectorsAssert unusedSubtypeSelectorAt(final TargetSelector selector, final String stackTraceLine) {
        return assertUnusedSelectorAt(selector, stackTraceLine, actual.getSubtypes());
    }

    //
    // asserts without stacktrace line
    //

    public UnusedSelectorsAssert unusedIgnoreSelector(final TargetSelector selector) {
        return assertUnused(selector, actual.getIgnored());
    }

    public UnusedSelectorsAssert unusedWithNullableSelector(final TargetSelector selector) {
        return assertUnused(selector, actual.getNullable());
    }

    public UnusedSelectorsAssert unusedGeneratorSelector(final TargetSelector selector) {
        return assertUnused(selector, actual.getGenerators());
    }


    public UnusedSelectorsAssert unusedOnCompleteSelector(final TargetSelector selector) {
        return assertUnused(selector, actual.getCallbacks());
    }


    public UnusedSelectorsAssert unusedSubtypeSelector(final TargetSelector selector) {
        return assertUnused(selector, actual.getSubtypes());
    }

    private UnusedSelectorsAssert assertUnusedSelectorAt(
            final TargetSelector selector,
            final String stackTraceLine,
            final Set<? super TargetSelector> selectors) {

        assertThat(getActualStackTraceLine(selector.toString())).containsSubsequence(stackTraceLine);
        return assertUnused(selector, selectors);
    }

    private UnusedSelectorsAssert assertUnused(
            final TargetSelector selector,
            final Set<? super TargetSelector> selectors) {

        assertThat(isUnusedSelector(selector, selectors))
                .as("Expected selector %s to be unused", selector)
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
        return actualStackTraceLine;
    }

    private static String extractUnusedSelectorsFromExceptionMessage(final String message) {
        // Remove everything after "possible causes" from the exception message.
        // This is to prevent the selectors in the examples in the error message examples
        // from being counted.
        return message.substring(0, message.indexOf("Possible causes"));
    }

    private boolean isUnusedSelector(final TargetSelector selector, final Set<? super TargetSelector> selectors) {
        if (selector instanceof Selector) {
            return selectors.contains(selector);
        } else if (selector instanceof PredicateSelector || selector instanceof SelectorBuilder) {
            return selectors.stream()
                    .map(Object::toString)
                    .anyMatch(it -> it.equals(selector.toString()));
        }
        throw new AssertionError("Unreachable");
    }
}
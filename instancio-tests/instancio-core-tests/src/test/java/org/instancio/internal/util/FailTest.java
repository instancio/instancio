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
package org.instancio.internal.util;

import org.instancio.exception.InstancioTerminatingException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.internal.util.Fail.SUBMIT_BUG_REPORT_MSG;

class FailTest {
    private static final RuntimeException CAUSE = new RuntimeException("expected error");

    @Test
    void withoutMessageArgs() {
        final InstancioTerminatingException result = Fail.withFataInternalError("foo");

        assertThat(result)
                .hasMessage(getExpectedMessage("foo"))
                .hasNoCause();
    }

    @Test
    void withMessageArgButNoThrowable() {
        final InstancioTerminatingException result = Fail.withFataInternalError("foo %s", 123);

        assertThat(result)
                .hasMessage(getExpectedMessage("foo 123"))
                .hasNoCause();
    }

    @Test
    void withOnlyThrowableArg() {
        final InstancioTerminatingException result = Fail.withFataInternalError("foo", CAUSE);

        assertThat(result)
                .hasMessage(getExpectedMessage("foo"))
                .hasCause(CAUSE);
    }

    @Test
    void withMessageArgsAndThrowable() {
        final InstancioTerminatingException result = Fail.withFataInternalError("foo %s %s %s", 1, 2, 3, CAUSE);

        assertThat(result)
                .hasMessage(getExpectedMessage("foo 1 2 3"))
                .hasCause(CAUSE);
    }

    private static String getExpectedMessage(final String msg) {
        return String.format("%n%s%n%n -> %s%n", SUBMIT_BUG_REPORT_MSG, msg);
    }
}

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
package org.external.errorhandling;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.test.support.pojo.person.Gender;
import org.instancio.test.support.pojo.record.StringsAbcRecord;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

/**
 * A type mismatch on a record component must produce the same usage error
 * as a type mismatch on a regular field, and not an internal error.
 *
 * <p>Note: this test does not extend {@link AbstractErrorMessageTestTemplate}
 * because the root cause reported by the canonical constructor varies with the
 * Java version (JDK 17 throws an {@code IllegalArgumentException} without a cause,
 * while JDK 18+ wraps a {@code ClassCastException}), so the entire message
 * cannot be asserted for equality.
 */
class TypeMismatchWithRecordComponentTest {

    @Test
    void verifyErrorMessage() {
        final InstancioApi<StringsAbcRecord> api = Instancio.of(StringsAbcRecord.class)
                .set(field(StringsAbcRecord::a), Gender.FEMALE);

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                // replace \r to prevent test failures when run on Windows
                .extracting(ex -> ex.getMessage().replace("\r", ""))
                .asString()
                .contains("""
                        Reason: error assigning value to: field StringsAbcRecord.a (depth=1)
                        
                         │ Path to root:
                         │   <1:StringsAbcRecord: String a>
                         │    └──<0:StringsAbcRecord>   <-- Root
                         │
                         │ Format: <depth:class: field>
                        
                        
                        Type mismatch:
                        
                         -> Target field .............: String a (in org.instancio.test.support.pojo.record.StringsAbcRecord)
                         -> Provided argument type ...: Gender
                         -> Provided argument value ..: FEMALE
                        
                        Root cause:
                        """);
    }
}

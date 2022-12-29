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
package org.instancio.test.features.assignment;

import org.instancio.Instancio;
import org.instancio.internal.util.SystemProperties;
import org.instancio.test.support.pojo.assignment.SetterStylePojo;
import org.instancio.test.support.pojo.assignment.SetterStyleSet;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.fields;

@ClearSystemProperty(key = SystemProperties.ASSIGNMENT_TYPE)
class AssignmentSystemPropertyTest {

    @Test
    @SetSystemProperty(key = SystemProperties.ASSIGNMENT_TYPE, value = "METHOD")
    void methodAssignment() {
        final SetterStylePojo result = Instancio.of(SetterStyleSet.class)
                .ignore(fields().matching("viaSetter.*"))
                .create();

        assertThat(result.isViaSetter_integerWrapper()).isTrue();
    }
}

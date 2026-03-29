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
package org.instancio.test.protobuf;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.test.prototobuf.Proto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@ExtendWith(InstancioExtension.class)
class ProtoAdhocTest {

    /**
     * This POJO is tailored to verify {@code ProtoSetterMethodResolver}
     * branches that return null.
     */
    private static class ProtoHolder {
        Proto.Person protoPerson;

        // Mimics proto fields that end with underscore
        String fieldWithTrailingUnderscore_; //NOSONAR

        // method used via reflection
        @SuppressWarnings("unused")
        public void setProtoPerson(final Proto.Person protoPerson) {
            this.protoPerson = protoPerson;
        }

        // method used via reflection
        @SuppressWarnings("unused")
        public void setFieldWithTrailingUnderscore_(final String fieldWithTrailingUnderscore_) {
            this.fieldWithTrailingUnderscore_ = fieldWithTrailingUnderscore_;
        }
    }

    @Test
    void createProtoHolder() {
        final ProtoHolder result = Instancio.of(ProtoHolder.class)
                .withSetting(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                .set(field(Proto.Person::getName), "foo")
                .create();

        assertThat(result.protoPerson).isNotNull();
        assertThat(result.fieldWithTrailingUnderscore_).isNotBlank();
        assertThat(result.protoPerson.getName()).isEqualTo("foo");
    }
}

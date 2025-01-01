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
package org.instancio.test.pojo.beanvalidation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

public class EmailComboBV {

    @Data
    public static class EmailWithLength {
        @Email
        @Length(min = 7, max = 12)
        private String emailThenLength;

        @Length(min = 10, max = 10)
        @Email
        private String lengthThenEmail;
    }

    @Data
    public static class NotNullEmailWithLength {
        @Email
        @NotNull
        @Length(min = 15, max = 20)
        private String value;
    }
}

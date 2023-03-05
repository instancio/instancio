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
package org.instancio.test.pojo.beanvalidation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

public class EmailComboBV {

    @Data
    public static class EmailWithSize {
        @NotNull
        @Email
        @Size(min = 7, max = 12)
        private String emailThenSize;

        @NotNull
        @Size(min = 10, max = 10)
        @Email
        private String sizeThenEmail;
    }

    @Data
    public static class NotNullEmailWithSize {
        @Email
        @NotNull
        @Size(min = 15, max = 20)
        private String value;
    }
}

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

import lombok.Data;
import org.hibernate.validator.constraints.URL;

public class UrlBV {

    public static final String PROTOCOL = "ftp";
    public static final String HOST = "example";
    public static final int PORT = 1234;

    @Data
    public static class WithDefaults {
        @URL
        private String value;
    }

    @Data
    public static class WithAttributes {
        @URL(protocol = PROTOCOL, host = HOST, port = PORT)
        private String value;
    }
}

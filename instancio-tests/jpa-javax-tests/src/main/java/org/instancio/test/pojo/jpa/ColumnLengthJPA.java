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
package org.instancio.test.pojo.jpa;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.Size;

public class ColumnLengthJPA {

    @Data
    public static class WithDefaultLength {
        // Column.length default is 255
        @Column
        private String s;
    }

    @Data
    public static class WithLength {
        @Column(length = 1)
        private String s1;
        @Column(length = 2)
        private String s2;
        @Column(length = 100)
        private String s3;
        @Column(length = 300)
        private String s4;
    }

    /**
     * Bean validation annotations, such as {@link Size}
     * should take precedence over JPA annotation.
     */
    @Data
    public static class WithLengthAndSize {

        @Column
        @Size(min = 1, max = 1)
        private String s1;

        @Column(length = 10)
        @Size(min = 2, max = 2)
        private String s2;
    }
}

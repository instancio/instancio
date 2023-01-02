/*
 *  Copyright 2022-2023 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.test.support.pojo.nested;

import lombok.Data;

@Data
public class OuterClass {
    private InnerClass innerClass;
    private InnerStaticClass innerStaticClass;
    private InnerStaticClass.InnermostStaticClass innermostStaticClass;

    @Data
    public static class InnerStaticClass {
        private String value;

        @Data
        public static class InnermostStaticClass {
            private String value;
        }
    }

    @Data
    @SuppressWarnings("InnerClassMayBeStatic")
    public class InnerClass { // intentionally not static
        private String value;
    }
}

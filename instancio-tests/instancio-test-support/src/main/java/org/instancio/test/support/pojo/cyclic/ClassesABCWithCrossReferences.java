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
package org.instancio.test.support.pojo.cyclic;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
public class ClassesABCWithCrossReferences {

    private ObjectA objectA;
    private ObjectB objectB;
    private ObjectC objectC;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE)
                .replace(getClass().getSimpleName() + ".", "");
    }

    @Data
    public static class ObjectA {
        private ObjectA objectA;
        private ObjectB objectB;
        private ObjectC objectC;
    }

    @Data
    public static class ObjectB {
        private ObjectA objectA;
        private ObjectB objectB;
        private ObjectC objectC;
    }

    @Data
    public static class ObjectC {
        private ObjectA objectA;
        private ObjectB objectB;
        private ObjectC objectC;
    }
}

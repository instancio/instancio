/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.test.support.pojo.misc;

import lombok.Data;

/**
 * <pre>
 *  Depth
 *    0     MultipleClassesWithId
 *                 |
 *    1            A
 *               / |  \
 *    2        ID  B    C
 *             |   |   /|\
 *    3     value  ID  B D ID
 *                /    |  \   \
 *    4        value  ID  ID  value
 *                    |    \
 *    5            value  value
 * </pre>
 */
@Data
public class MultipleClassesWithId<T> {

    private A<T> a;

    @Data
    public static class ID<T> {
        private T value;
    }

    @Data
    public static class A<T> {
        private ID<T> id;
        private B<T> b;
        private C<T> c;
    }

    @Data
    public static class B<T> {
        private ID<T> id;
    }

    @Data
    public static class C<T> {
        private ID<T> id;
        private B<T> b;
        private D<T> d;
    }

    @Data
    public static class D<T> {
        private ID<T> id;
    }
}
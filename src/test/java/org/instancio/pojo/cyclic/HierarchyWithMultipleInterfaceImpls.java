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
package org.instancio.pojo.cyclic;

import lombok.Getter;

public class HierarchyWithMultipleInterfaceImpls {

    interface Identifiable {
        ID getId();
    }

    @Getter
    public static class A implements Identifiable {
        ID id;
        B b;
        C c;
    }

    @Getter
    public static class B implements Identifiable {
        ID id;
    }

    @Getter
    public static class C implements Identifiable {
        ID id;
    }

    @Getter
    public static class ID {
        private String id;
        private Identifiable owner;
    }
}

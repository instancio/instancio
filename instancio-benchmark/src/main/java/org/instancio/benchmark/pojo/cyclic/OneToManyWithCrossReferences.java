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
package org.instancio.benchmark.pojo.cyclic;

import java.util.List;

public class OneToManyWithCrossReferences {

    public static class ObjectA {
        private List<ObjectB> objectB;
        private List<ObjectC> objectC;
        private List<ObjectD> objectD;
        private List<ObjectE> objectE;
        private List<ObjectF> objectF;
        private List<ObjectG> objectG;
    }

    public static class ObjectB {
        private ObjectA objectA;
        private ObjectC objectC;
        private ObjectD objectD;
        private ObjectE objectE;
        private ObjectF objectF;
        private ObjectG objectG;
    }

    public static class ObjectC {
        private ObjectA objectA;
        private ObjectB objectB;
        private ObjectD objectD;
        private ObjectE objectE;
        private ObjectF objectF;
        private ObjectG objectG;
    }

    public static class ObjectD {
        private ObjectA objectA;
        private ObjectB objectB;
        private ObjectC objectC;
        private ObjectE objectE;
        private ObjectF objectF;
        private ObjectG objectG;
    }

    public static class ObjectE {
        private ObjectA objectA;
        private ObjectB objectB;
        private ObjectC objectC;
        private ObjectD objectD;
        private ObjectF objectF;
        private ObjectG objectG;
    }

    public static class ObjectF {
        private ObjectA objectA;
        private ObjectB objectB;
        private ObjectC objectC;
        private ObjectD objectD;
        private ObjectE objectE;
        private ObjectG objectG;
    }

    public static class ObjectG {
        private ObjectA objectA;
        private ObjectB objectB;
        private ObjectC objectC;
        private ObjectD objectD;
        private ObjectE objectE;
        private ObjectF objectF;
    }
}

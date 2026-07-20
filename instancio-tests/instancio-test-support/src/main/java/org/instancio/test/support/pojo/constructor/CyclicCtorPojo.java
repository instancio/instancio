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
package org.instancio.test.support.pojo.constructor;

/**
 * A constructor POJO with a self-referencing (cyclic) field
 * that is not a constructor parameter.
 */
public class CyclicCtorPojo {

    private final String id;
    private CyclicCtorPojo self;
    private String value;

    public CyclicCtorPojo(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public CyclicCtorPojo getSelf() {
        return self;
    }

    public void setSelf(final CyclicCtorPojo self) {
        this.self = self;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
}

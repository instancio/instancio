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

import org.instancio.test.support.pojo.record.cyclic.ParentRecord;

import java.util.List;

/**
 * Parent/child pair where the child's back-reference to the parent
 * is a constructor parameter, creating a circular dependency when
 * both classes are instantiated via constructor.
 *
 * <p>Constructor-based analogue of {@link ParentRecord}.
 */
public class ParentCtorPojo {

    private final String name;
    private final List<ChildCtorPojo> children;

    public ParentCtorPojo(final String name, final List<ChildCtorPojo> children) {
        this.name = name;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public List<ChildCtorPojo> getChildren() {
        return children;
    }
}

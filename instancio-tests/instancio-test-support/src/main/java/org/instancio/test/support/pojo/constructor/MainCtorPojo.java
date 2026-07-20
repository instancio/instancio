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

import org.instancio.test.support.pojo.cyclic.onetomany.MainPojo;

import java.util.List;

/**
 * One-to-many parent/detail pair where the {@code id} fields are
 * constructor parameters, but the collection and the back-reference
 * are regular (settable) fields, so back-references can be assigned
 * after the objects have been constructed.
 *
 * <p>Constructor-based analogue of {@link MainPojo}.
 */
public class MainCtorPojo {

    private final Long id;
    private List<DetailCtorPojo> detailPojos;

    public MainCtorPojo(final Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public List<DetailCtorPojo> getDetailPojos() {
        return detailPojos;
    }

    public void setDetailPojos(final List<DetailCtorPojo> detailPojos) {
        this.detailPojos = detailPojos;
    }
}

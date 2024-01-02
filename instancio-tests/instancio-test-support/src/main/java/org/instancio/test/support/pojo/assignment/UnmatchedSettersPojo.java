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
package org.instancio.test.support.pojo.assignment;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A POJO containing setters that do not match any fields.
 */
@ToString
public class UnmatchedSettersPojo {

    @Getter
    private final List<String> invokedSetters = new ArrayList<>();

    @Getter
    private final List<UUID> values = new ArrayList<>();

    //
    // Setters with "set" prefix
    //

    public void setPublicFoo(UUID uuid) {
        addValue("setPublicFoo", uuid);
    }

    void setPackagePrivateFoo(UUID uuid) {
        addValue("setPackagePrivateFoo", uuid);
    }

    protected void setProtectedFoo(UUID uuid) {
        addValue("setProtectedFoo", uuid);
    }

    protected void setPrivateFoo(UUID uuid) {
        addValue("setPrivateFoo", uuid);
    }

    //
    // Setters with "with" prefix
    //

    public void withPublicFoo(UUID uuid) {
        addValue("withPublicFoo", uuid);
    }

    void withPackagePrivateFoo(UUID uuid) {
        addValue("withPackagePrivateFoo", uuid);
    }

    protected void withProtectedFoo(UUID uuid) {
        addValue("withProtectedFoo", uuid);
    }

    protected void withPrivateFoo(UUID uuid) {
        addValue("withPrivateFoo", uuid);
    }

    //
    // Setter without a prefix
    //

    public void publicFoo(UUID uuid) {
        addValue("publicFoo", uuid);
    }

    private void addValue(final String setterName, final UUID value) {
        invokedSetters.add(setterName);
        values.add(value);
    }

}

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
package org.instancio.test.support.pojo.immutable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jspecify.annotations.NullUnmarked;

@NullUnmarked
public class ImmutableFooBarBaz implements FooBarBazInterface {
    private final String foo;
    private final String bar;
    private final String baz;

    public ImmutableFooBarBaz(final String foo, final String bar, final String baz) {
        this.foo = foo;
        this.bar = bar;
        this.baz = baz;
    }

    @Override
    public String getFoo() {
        return foo;
    }

    @Override
    public String getBar() {
        return bar;
    }

    @Override
    public String getBaz() {
        return baz;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

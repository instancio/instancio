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
package org.instancio.test.support.pojo.basic;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class LongHolderWithDefaults {
    public static final int PRIMITIVE = -111;
    public static final long WRAPPER = -222L;

    private long primitive;
    private Long wrapper;

    public LongHolderWithDefaults() {
        this.primitive = PRIMITIVE;
        this.wrapper = WRAPPER;
    }

    public long getPrimitive() {
        return primitive;
    }

    public void setPrimitive(final long primitive) {
        this.primitive = primitive;
    }

    public Long getWrapper() {
        return wrapper;
    }

    public void setWrapper(final Long wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

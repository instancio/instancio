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
package org.instancio.test.support.pojo.misc.getters;

import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@EqualsAndHashCode
public class PropertyStylePojo {

    private String foo;
    private int bar;
    private boolean isBaz;
    private boolean haz;
    private boolean hasGaz;

    public String foo() {
        return foo;
    }

    public int bar() {
        return bar;
    }

    public boolean isBaz() {
        return isBaz;
    }

    public boolean haz() {
        return haz;
    }

    public boolean hasGaz() {
        return hasGaz;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

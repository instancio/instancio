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
package org.instancio.test.support.pojo.assignment;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@SuppressWarnings("unused") // false positive: setters are used via reflection
public class SetterStyleProperty implements SetterStylePojo {
    private int primitiveInt;
    private Integer integerWrapper;
    private String string;
    private boolean isBooleanProperty;
    private Boolean isBooleanWrapper;
    private boolean noIsPrefixBooleanProperty;
    private Boolean noIsPrefixBooleanPropertyWrapper;

    private boolean viaSetter_primitiveInt;
    private boolean viaSetter_integerWrapper;
    private boolean viaSetter_string;
    private boolean viaSetter_isBooleanProperty;
    private boolean viaSetter_isBooleanWrapper;
    private boolean viaSetter_noIsPrefixBooleanProperty;
    private boolean viaSetter_noIsPrefixBooleanPropertyWrapper;

    public void primitiveInt(final int primitiveInt) {
        this.primitiveInt = primitiveInt;
        viaSetter_primitiveInt = true;
    }

    public void integerWrapper(final Integer integerWrapper) {
        this.integerWrapper = integerWrapper;
        viaSetter_integerWrapper = true;
    }

    public void string(final String string) {
        this.string = string;
        viaSetter_string = true;
    }

    public void isBooleanProperty(final boolean booleanProperty) {
        isBooleanProperty = booleanProperty;
        viaSetter_isBooleanProperty = true;
    }

    public void isBooleanWrapper(final Boolean booleanWrapper) {
        isBooleanWrapper = booleanWrapper;
        viaSetter_isBooleanWrapper = true;
    }

    public void noIsPrefixBooleanProperty(final boolean noIsPrefixBooleanProperty) {
        this.noIsPrefixBooleanProperty = noIsPrefixBooleanProperty;
        viaSetter_noIsPrefixBooleanProperty = true;
    }

    public void noIsPrefixBooleanPropertyWrapper(final Boolean noIsPrefixBooleanPropertyWrapper) {
        this.noIsPrefixBooleanPropertyWrapper = noIsPrefixBooleanPropertyWrapper;
        viaSetter_noIsPrefixBooleanPropertyWrapper = true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}

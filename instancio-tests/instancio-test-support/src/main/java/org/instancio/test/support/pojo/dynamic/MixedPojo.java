/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.test.support.pojo.dynamic;

/**
 * A class containing different types of fields,
 * including regular and dynamic fields.
 */
public class MixedPojo extends DynPojoBase {

    private static final String DYNAMIC_FIELD = "dynamicField";

    //@formatter:off

    // field with matching setter
    private String regularField;
    public String getRegularField() { return regularField; }
    public void setRegularField(String s) { regularField = s; }

    // field with no setter (e.g. internal field)
    private String regularFieldWithNoSetter;
    public String getRegularFieldWithNoSetter() { return regularFieldWithNoSetter; }

    // field whose setter does not match the field name
    private String regularFieldWithNonMatchingSetter;
    public String getFoo() { return regularFieldWithNonMatchingSetter; }
    public void setFoo(String s) { this.regularFieldWithNonMatchingSetter = s; }

    // dynamic field
    public String getDynamicField() { return get(DYNAMIC_FIELD); }
    public void setDynamicField(String s) { set(DYNAMIC_FIELD, s); }

    //@formatter:on
}

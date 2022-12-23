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

public interface SetterStylePojo {

    int getPrimitiveInt();

    Integer getIntegerWrapper();

    String getString();

    boolean isBooleanProperty();

    Boolean getIsBooleanWrapper();

    boolean isNoIsPrefixBooleanProperty();

    Boolean getNoIsPrefixBooleanPropertyWrapper();

    boolean isViaSetter_primitiveInt();

    boolean isViaSetter_integerWrapper();

    boolean isViaSetter_string();

    boolean isViaSetter_isBooleanProperty();

    boolean isViaSetter_isBooleanWrapper();

    boolean isViaSetter_noIsPrefixBooleanProperty();

    boolean isViaSetter_noIsPrefixBooleanPropertyWrapper();

}

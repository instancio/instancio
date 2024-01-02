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

import lombok.ToString;

public class OverriddenSetterPojo {

    private OverriddenSetterPojo() {
        // non-instantiable
    }

    @ToString
    public static class Parent {
        private String parentSetterValue;

        public void setValue(final String value) {
            this.parentSetterValue = value;
        }

        public String getParentSetterValue() {
            return parentSetterValue;
        }
    }

    @ToString(callSuper = true)
    public static class Child extends Parent {
        private String childSetterValue;

        @Override
        public void setValue(final String value) {
            this.childSetterValue = value;
        }

        public String getChildSetterValue() {
            return childSetterValue;
        }
    }
}

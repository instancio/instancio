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

/**
 * A POJO for verifying that a setter with the correct parameter type is called.
 * Fields intentionally do not match the setter names.
 */
@Getter
@ToString
public class OverloadedUnmatchedSettersPojo {
    private String a;
    private int b;
    private Integer c;
    private long d;

    public void setValue(String a) {
        this.a = a;
    }

    public void setValue(int b) {
        this.b = b;
    }

    public void setValue(Integer c) {
        this.c = c;
    }

    public void setValue(long d) {
        this.d = d;
    }
}
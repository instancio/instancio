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

import lombok.Getter;
import org.instancio.test.support.pojo.interfaces.StringsDefInterface;

/**
 * This class must declare <b>only</b> the all-args constructor.
 *
 * @see StringsAbcCtor
 */
@Getter
@SuppressWarnings("ClassCanBeRecord")
public class StringsDefCtor implements StringsDefInterface {

    private final String d;
    private final String e;
    private final String f;
    private final StringsGhiCtor ghi;

    public StringsDefCtor(final String d, final String e, final String f, final StringsGhiCtor ghi) {
        this.d = d;
        this.e = e;
        this.f = f;
        this.ghi = ghi;
    }
}

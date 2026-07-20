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
import org.instancio.test.support.pojo.interfaces.StringsAbcInterface;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.record.StringsAbcRecord;

/**
 * A POJO whose fields are all final and assigned via constructor.
 * Mimics {@link StringsAbc} and {@link StringsAbcRecord}.
 *
 * <p>This class must declare <b>only</b> the all-args constructor.
 */
@Getter
@SuppressWarnings("ClassCanBeRecord")
public class StringsAbcCtor implements StringsAbcInterface {

    private final String a;
    private final String b;
    private final String c;
    private final StringsDefCtor def;

    public StringsAbcCtor(final String a, final String b, final String c, final StringsDefCtor def) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.def = def;
    }
}

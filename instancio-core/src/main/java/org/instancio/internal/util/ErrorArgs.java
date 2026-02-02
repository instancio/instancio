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
package org.instancio.internal.util;

import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public final class ErrorArgs {

    private final Object[] args;
    private final Throwable throwable;

    private ErrorArgs(final Object[] args, final Throwable throwable) {
        this.args = args;
        this.throwable = throwable;
    }

    public static ErrorArgs unpackArgs(final Object... args) {
        final int len = args.length;
        if (len > 0 && args[len - 1] instanceof Throwable throwable) {
            return new ErrorArgs(copyWithoutLastElement(args), throwable);
        }
        return new ErrorArgs(args, null);
    }

    @SuppressWarnings("PMD.MethodReturnsInternalArray")
    public Object[] getArgs() {
        return args;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    private static Object[] copyWithoutLastElement(final Object... args) {
        final List<Object> list = CollectionUtils.asArrayList(args);
        list.remove(args.length - 1);
        return list.toArray();
    }
}

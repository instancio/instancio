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
package org.instancio.internal.util;

public final class Sonar {

    public static final String ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED = "java:S3011";
    public static final String ADD_ASSERTION = "java:S2699";
    public static final String CATCH_EXCEPTION_INSTEAD_OF_THROWABLE = "java:S1181";
    public static final String DISABLED_TEST = "java:S1607";
    public static final String GENERIC_WILDCARD_TYPES_SHOULD_NOT_BE_USED_IN_RETURN_TYPES = "java:S1452";
    public static final String METHODS_RETURNS_SHOULD_NOT_BE_INVARIANT = "java:S3516";
    public static final String NUMBER_OF_PARENTS = "java:S110";
    public static final String RAW_USE_OF_PARAMETERIZED_CLASS = "java:S3740";
    public static final String RETURN_EMPTY_COLLECTION = "java:S1168";

    private Sonar() {
        // non-instantiable
    }
}

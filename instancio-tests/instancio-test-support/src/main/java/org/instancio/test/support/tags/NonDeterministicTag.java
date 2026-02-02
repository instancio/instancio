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
package org.instancio.test.support.tags;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * An annotation that indicates that a test makes assertions
 * on randomly generated values. While the intention is for the test to pass,
 * there is a very small chance it might fail.
 * <p>
 * For example asserting that a randomly generated {@code int} within
 * {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE} is not zero.
 */
@Tag("non-deterministic")
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface NonDeterministicTag {

    String value() default "";
}

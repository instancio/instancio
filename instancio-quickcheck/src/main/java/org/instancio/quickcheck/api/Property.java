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
package org.instancio.quickcheck.api;

import org.instancio.documentation.ExperimentalApi;
import org.junit.platform.commons.annotation.Testable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes an individual property based test case.
 *
 * @since 3.6.0
 * @deprecated the {@code instancio-quickcheck} module is deprecated
 *             and will be removed in version 5.
 */
@Deprecated
@Testable
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExperimentalApi
public @interface Property {
    /**
     * The number of samples to generate, default is 1000
     *
     * @since 3.6.0
     */
    int samples() default 1000;
}

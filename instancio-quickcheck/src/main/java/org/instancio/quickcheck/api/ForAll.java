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
import org.instancio.quickcheck.api.artbitrary.Arbitrary;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes the property to run the property based test case against. Optionally, the
 * generator could be specified to provide the samples.
 *
 * @since 3.6.0
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExperimentalApi
public @interface ForAll {
    /**
     * The generator to provide the samples (optional). The generator should be the
     * name of the method that is declared in the current test suite and returns the
     * instance of the {@link Arbitrary} that corresponds to the type of the property.
     *
     * @since 3.6.0
     */
    String value() default "";
}

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
package org.instancio.schema;

import org.instancio.documentation.ExperimentalApi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A spec that is derived from one or more other specs.
 *
 * @see CombinatorMethod
 * @see Schema
 * @since 5.0.0
 */
@ExperimentalApi
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DerivedSpec {

    /**
     * One or more spec methods declared by the schema
     * from which the derived spec is created.
     *
     * @return method names that provide inputs for the derived spec
     * @since 5.0.0
     */
    @ExperimentalApi
    String[] fromSpec();

    /**
     * A class that contains one or more methods annotated
     * with {@link CombinatorMethod}.
     *
     * @return a class that provides a combinator method(s)
     * @since 5.0.0
     */
    @ExperimentalApi
    Class<? extends CombinatorProvider> by();

    /**
     * Specifies the name of the combinator method to use.
     * This attribute is only required if the {@link CombinatorProvider}
     * contains more than one method annotated with {@link CombinatorMethod}.
     *
     * @return combinator method name
     * @since 5.0.0
     */
    @ExperimentalApi
    String method() default "";
}

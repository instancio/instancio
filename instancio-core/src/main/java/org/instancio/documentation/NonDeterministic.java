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
package org.instancio.documentation;

import org.instancio.generator.specs.TemporalSpec;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;

/**
 * This annotation marks APIs that produce data which cannot be
 * guaranteed to be reproducible.
 *
 * <p>For example, generating dates using methods such as:
 *
 * <ul>
 *   <li>{@link TemporalSpec#past()}</li>
 *   <li>{@link TemporalSpec#future()}</li>
 * </ul>
 *
 * <p>These methods rely on the current date as a reference point,
 * which changes over time. Consequently, the same seed value might
 * yield different results at different times.
 *
 * @since 5.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ANNOTATION_TYPE, CONSTRUCTOR, FIELD, METHOD, PACKAGE, TYPE})
public @interface NonDeterministic {}
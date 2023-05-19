/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio;

import org.instancio.documentation.ExperimentalApi;

/**
 * A marker for conditional expressions that can be passed
 * to {@link InstancioApi#when(Conditional)}.
 *
 * <p>A conditional expression consists of:
 *
 * <ul>
 *   <li><b>origin</b> - a selector that must satisfy the condition</li>
 *   <li><b>condition</b> (predicate) that must be satisfied by the origin</li>
 *   <li><b>action</b> - an action that is applied to destination selectors
 *       if the condition is satisfied</li>
 * </ul>
 *
 * <p>A conditional can be created using one of the {@code Select.valueOf()}
 * static factory methods:
 *
 * <pre>{@code
 *   when(valueOf(originSelector).satisfies(Predicate).set(destinationSelector, value))
 * }</pre>
 *
 * <p>For example, the following snippet sets country code of the
 * {@code Phone} class based on the generated country:
 *
 * <pre>{@code
 *   Address address = Instancio.of(Address.class)
 *       .generate(field(Address::getCountry), gen -> gen.oneOf("CA", "UK", "US"))
 *       .when(valueOf(Address::getCountry).isIn("CA", "US").set(field(Phone::getCountryCode), "+1"))
 *       .when(valueOf(Address::getCountry).is("UK").set(field(Phone::getCountryCode), "+44"))
 *       .create();
 * }</pre>
 *
 * @since 3.0.0
 */
@ExperimentalApi
public interface Conditional {
}

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
package org.instancio.test.support.pojo.generics;

import lombok.Data;
import org.instancio.test.support.pojo.person.Gender;

/**
 * The field {@code T value} should be populated when generating instances
 * of not only {@code GenericEnumHolder}, but also its subclasses.
 * <p>
 * Populating this field for subclasses requires traversing the inheritance
 * hierarchy in order to resolve the type. For this reason, we have
 * {@code GenderEnumHolder} classes 1, 2, and 3, in order to ensure this works
 * not only for the direct child {@code GenderEnumHolder1}, but also for
 * deeper descendants (2 and 3).
 */
@Data
public class GenericEnumHolder<T extends Enum<T>> {
    private T value;

    public static class GenderEnumHolder1 extends GenericEnumHolder<Gender> {}

    public static class GenderEnumHolder2 extends GenderEnumHolder1 {}

    public static class GenderEnumHolder3 extends GenderEnumHolder2 {}
}
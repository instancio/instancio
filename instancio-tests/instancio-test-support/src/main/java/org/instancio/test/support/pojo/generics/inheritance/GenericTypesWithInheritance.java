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
package org.instancio.test.support.pojo.generics.inheritance;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.person.Phone;

import java.io.Serializable;

@Value
public class GenericTypesWithInheritance {

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    public static class ItemHolder<K, T extends Item<K>> extends EntityWithId<Long> {
        T phone;
    }

    @Data
    @Getter
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    public static class PhoneHolder<T extends Phone> extends EntityWithId<Long> {
        T phone;
    }

    @Data
    @Getter
    public static class EntityWithId<T extends Serializable> {
        T id;
    }
}

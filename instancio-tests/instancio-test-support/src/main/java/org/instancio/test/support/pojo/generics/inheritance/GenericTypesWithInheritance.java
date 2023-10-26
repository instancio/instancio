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
package org.instancio.test.support.pojo.generics.inheritance;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.instancio.test.support.pojo.interfaces.ItemInterface;
import org.instancio.test.support.pojo.person.Phone;

import java.io.Serializable;

@Value
public class GenericTypesWithInheritance {

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    public static class GenericItemHolderWithInheritance<VALUE, ITEM extends ItemInterface<VALUE>> extends EntityWithId<Long> {
        ITEM item;
    }

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    public static class GenericPhoneHolderWithInheritance<PHONE extends Phone> extends EntityWithId<Long> {
        PHONE phone;
    }

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    public static class GenericLevel2<ID extends Serializable> extends EntityWithId<ID> {
    }

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    public static class GenericLevel3 extends GenericLevel2<Long> {
    }

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    public static class GenericLevel4 extends GenericLevel3 {
    }

    @Data
    public static class EntityWithId<ID extends Serializable> {
        ID id;
    }
}

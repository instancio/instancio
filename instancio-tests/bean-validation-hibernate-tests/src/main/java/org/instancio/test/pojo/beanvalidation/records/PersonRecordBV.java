/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.test.pojo.beanvalidation.records;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.UUID;
import org.instancio.test.pojo.beanvalidation.person.PetBV;
import org.instancio.test.support.pojo.person.PersonName;

import java.time.LocalDateTime;
import java.util.Date;

public record PersonRecordBV(
        @NotNull
        @UUID
        String uuid,

        @PersonName // this his not a validation annotation and should be ignored
        @NotNull
        @Length(min = 2)
        String name,

        AddressRecordBV address,

        @Range(min = 18, max = 65)
        int age,

        @Past
        LocalDateTime lastModified,

        @Future
        Date date,

        @NotNull
        @Size(max = 3)
        PetBV[] pets
) {}

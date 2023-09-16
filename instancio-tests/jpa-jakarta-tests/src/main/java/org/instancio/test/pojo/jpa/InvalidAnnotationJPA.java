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
package org.instancio.test.pojo.jpa;

import jakarta.persistence.Column;
import lombok.Data;
import org.instancio.test.support.pojo.person.Gender;

/**
 * POJO for verifying that invalid annotation declaration are ignored.
 */
@Data
public class InvalidAnnotationJPA {

    @Column(length = 10)
    private Gender d1;

    @Column(precision = 3, scale = -10)
    private Gender d2;
}

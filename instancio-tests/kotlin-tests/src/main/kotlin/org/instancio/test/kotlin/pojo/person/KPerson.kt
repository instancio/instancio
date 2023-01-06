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
package org.instancio.test.kotlin.pojo.person

import org.instancio.test.support.pojo.person.Gender
import java.time.LocalDateTime
import java.util.*

// ignore equals/hashCode warnings as they are not needed
@Suppress("ArrayInDataClass", "kotlin:S6218")
data class KPerson(
    var uuid: UUID,
    val name: String? = null,
    val address: KAddress? = null,
    val gender: Gender? = null,
    val age: Int,
    val lastModified: LocalDateTime? = null,
    val date: Date? = null,
    val pets: Array<KPet>
)
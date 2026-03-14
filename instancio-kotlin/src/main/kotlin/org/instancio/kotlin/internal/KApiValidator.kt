/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.kotlin.internal

import kotlin.reflect.KProperty1

internal object KApiValidator {

    internal fun noBackingFieldForProperty(
        property: KProperty1<*, *>, label: String, method: String
    ): String {
        return """
            cannot create ${label} for property '${property.name}'

             -> No backing field found

            Possible causes:

             -> The property is abstract or defined on an interface:
                interface MyInterface { val ${property.name}: Any }

             -> The property has a custom getter with no backing field:
                val ${property.name}: String get() = "computed"

             -> The property is a delegated property:
                val ${property.name}: String by lazy { "value" }

            To resolve this error:

             -> Reference the property on the concrete class instead:
                ${method}(MyConcreteClass::${property.name})

             -> Select by field name explicitly:
                ${method}(MyConcreteClass::class.java, "${property.name}")
            """.trimIndent()
    }
}
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
package org.external.errorhandling

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.instancio.exception.InstancioApiException
import org.instancio.kotlin.KSelect.field
import org.junit.jupiter.api.Test

class KSelectFieldWithoutBackingPropertyTest {

    internal abstract class Base {
        abstract val value: String
    }

    @Test
    fun fieldWithNoBackingProperty() {
        assertThatThrownBy {
            field(Base::value)
        }.isExactlyInstanceOf(InstancioApiException::class.java)
            .hasMessage(
                """


                Error creating an object
                 -> at org.external.errorhandling.KSelectFieldWithoutBackingPropertyTest.fieldWithNoBackingProperty${'$'}lambda$0(KSelectFieldWithoutBackingPropertyTest.kt:32)
                
                Reason: cannot create field selector for property 'value'
                
                 -> No backing field found
                
                Possible causes:
                
                 -> The property is abstract or defined on an interface:
                    interface MyInterface { val value: Any }
                
                 -> The property has a custom getter with no backing field:
                    val value: String get() = "computed"
                
                 -> The property is a delegated property:
                    val value: String by lazy { "value" }
                
                To resolve this error:
                
                 -> Reference the property on the concrete class instead:
                    field(MyConcreteClass::value)
                
                 -> Select by field name explicitly:
                    field(MyConcreteClass::class.java, "value")


           """.trimIndent()
            )
    }
}
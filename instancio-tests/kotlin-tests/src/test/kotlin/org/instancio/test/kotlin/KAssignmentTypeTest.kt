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
package org.instancio.test.kotlin

import org.assertj.core.api.Assertions.assertThat
import org.instancio.Instancio
import org.instancio.Model
import org.instancio.Select.field
import org.instancio.Select.fields
import org.instancio.junit.InstancioExtension
import org.instancio.junit.WithSettings
import org.instancio.settings.AssignmentType
import org.instancio.settings.Keys
import org.instancio.settings.OnSetMethodError
import org.instancio.settings.OnSetMethodNotFound
import org.instancio.settings.Settings
import org.instancio.test.support.tags.Feature
import org.instancio.test.support.tags.FeatureTag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@FeatureTag(Feature.ASSIGNMENT_TYPE)
@ExtendWith(InstancioExtension::class)
class KAssignmentTypeTest {

    @WithSettings
    internal val settings: Settings = Settings.create()
        .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.FAIL)
        .set(Keys.ON_SET_METHOD_ERROR, OnSetMethodError.FAIL)
        .lock()

    private fun baseModel(): Model<SamplePojo> =
        Instancio.of(SamplePojo::class.java)
            .ignore(fields().matching("viaSetter_.*")) // flags to verify that setters were called
            .set(field("booleanProperty"), true)
            .withSettings(Settings.create().set(Keys.ASSIGNMENT_TYPE, AssignmentType.FIELD))
            .toModel()

    @Test
    fun assignmentViaField() {
        val result = Instancio.create(baseModel())

        assertThat(result.stringProperty).isNotBlank
        assertThat(result.booleanProperty).isTrue
        // flags
        assertThat(result.viaSetter_stringProperty).isFalse
        assertThat(result.viaSetter_booleanProperty).isFalse
    }

    @Test
    fun assignmentViaMethod() {
        val result = Instancio.of(baseModel())
            .withSettings(Settings.create().set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD))
            .create()

        assertThat(result.stringProperty).isNotBlank
        assertThat(result.booleanProperty).isTrue
        // flags
        assertThat(result.viaSetter_stringProperty).isTrue
        assertThat(result.viaSetter_booleanProperty).isTrue
    }

    @Suppress("PropertyName")
    private class SamplePojo {
        var viaSetter_stringProperty: Boolean = false
        var viaSetter_booleanProperty: Boolean = false

        var stringProperty: String? = null
            set(value) {
                field = value
                viaSetter_stringProperty = true
            }

        var booleanProperty: Boolean? = null
            set(value) {
                field = value
                viaSetter_booleanProperty = true
            }
    }
}
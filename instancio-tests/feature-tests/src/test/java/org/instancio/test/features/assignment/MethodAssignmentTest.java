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
package org.instancio.test.features.assignment;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.assignment.AssignmentType;
import org.instancio.assignment.SetterStyle;
import org.instancio.internal.util.SystemProperties;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.assignment.SetterStylePojo;
import org.instancio.test.support.pojo.assignment.SetterStyleProperty;
import org.instancio.test.support.pojo.assignment.SetterStyleSet;
import org.instancio.test.support.pojo.assignment.SetterStyleWith;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Field;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;
import static org.instancio.Select.fields;

@FeatureTag(Feature.ASSIGNMENT)
@ExtendWith(InstancioExtension.class)
@DisabledIfSystemProperty(named = SystemProperties.ASSIGNMENT_TYPE, matches = "FIELD")
class MethodAssignmentTest {

    private static <T extends SetterStylePojo> Model<T> pojoModel(Class<T> pojoClass) {
        // These are used for verification; we don't want to generate values for these flags
        final Predicate<Field> viaSetterFlags = field -> field.getName().startsWith("viaSetter");

        return Instancio.of(pojoClass)
                .ignore(fields(viaSetterFlags))
                .withSettings(Settings.create().set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD))
                .toModel();
    }

    @Test
    void setterStyleSet() {
        final SetterStylePojo result = Instancio.of(pojoModel(SetterStyleSet.class))
                .withSettings(Settings.create().set(Keys.SETTER_STYLE, SetterStyle.SET))
                .create();

        assertResult(result);
    }

    @Test
    void styleWith() {
        final SetterStylePojo result = Instancio.of(pojoModel(SetterStyleWith.class))
                .withSettings(Settings.create().set(Keys.SETTER_STYLE, SetterStyle.WITH))
                .create();

        assertResult(result);
    }

    @Test
    void styleProperty() {
        final SetterStylePojo result = Instancio.of(pojoModel(SetterStyleProperty.class))
                .withSettings(Settings.create().set(Keys.SETTER_STYLE, SetterStyle.PROPERTY))
                .create();

        assertResult(result);
    }

    private static void assertResult(final SetterStylePojo result) {
        assertThatObject(result).hasNoNullFieldsOrProperties();

        assertThat(result.getPrimitiveInt()).isPositive();
        assertThat(result.getIsBooleanWrapper()).isNotNull();
        assertThat(result.getNoIsPrefixBooleanPropertyWrapper()).isNotNull();

        assertThat(result.isViaSetter_primitiveInt()).isTrue();
        assertThat(result.isViaSetter_integerWrapper()).isTrue();
        assertThat(result.isViaSetter_string()).isTrue();
        assertThat(result.isViaSetter_isBooleanProperty()).isTrue();
        assertThat(result.isViaSetter_isBooleanWrapper()).isTrue();
        assertThat(result.isViaSetter_noIsPrefixBooleanProperty()).isTrue();
        assertThat(result.isViaSetter_noIsPrefixBooleanPropertyWrapper()).isTrue();
    }
}

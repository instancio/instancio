/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.test.jooq;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.MethodModifier;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.OnSetMethodUnmatched;
import org.instancio.settings.Settings;

public final class InstancioJooq {

    // Populate objects using public setters only
    private static final int SETTER_EXCLUSIONS = MethodModifier.PACKAGE_PRIVATE
            | MethodModifier.PROTECTED
            | MethodModifier.PRIVATE;

    private static final Settings SETTINGS = Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
            .set(Keys.SETTER_EXCLUDE_MODIFIER, SETTER_EXCLUSIONS)
            .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE)
            .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE)
            .lock();

    public static <T> Model<T> jooqModel(final Class<T> klass) {
        return Instancio.of(klass)
                .withSettings(SETTINGS)
                .toModel();
    }

}

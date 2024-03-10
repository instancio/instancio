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
package org.instancio.test.features.setmodel;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag({Feature.MODEL, Feature.SET_MODEL, Feature.ON_COMPLETE})
@ExtendWith(InstancioExtension.class)
class SetModelOnCompleteTest {

    /**
     * Callback attached to the model.
     */
    @Test
    void onCompleteOnModel() {
        final AtomicBoolean callbackInvoked = new AtomicBoolean();

        final Model<StringsDef> model = Instancio.of(StringsDef.class)
                .onComplete(all(StringsGhi.class), res -> callbackInvoked.set(true))
                .toModel();

        assertThat(callbackInvoked).isFalse();

        Instancio.of(StringsAbc.class)
                .setModel(all(StringsDef.class), model)
                .create();

        assertThat(callbackInvoked).isTrue();
    }

    /**
     * Callback for a model's property, but attached to the object being created.
     */
    @Test
    void onCompleteOnObject() {
        final AtomicBoolean callbackInvoked = new AtomicBoolean();

        final Model<StringsDef> model = Instancio.of(StringsDef.class)
                .toModel();

        Instancio.of(StringsAbc.class)
                .setModel(all(StringsDef.class), model)
                .onComplete(field(StringsDef::getD), res -> callbackInvoked.set(true))
                .create();

        assertThat(callbackInvoked).isTrue();
    }
}

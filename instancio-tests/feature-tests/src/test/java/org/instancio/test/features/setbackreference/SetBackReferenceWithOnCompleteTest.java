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
package org.instancio.test.features.setbackreference;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.cyclic.onetomany.MainPojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag({Feature.SET_BACK_REFERENCES, Feature.ON_COMPLETE})
@ExtendWith(InstancioExtension.class)
class SetBackReferenceWithOnCompleteTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.SET_BACK_REFERENCES, true);

    @Test
    void shouldInvokeCallbackOnBackReferences() {
        final AtomicInteger mainPojoCount = new AtomicInteger();
        final int numOfDetailPojos = 10;

        Instancio.of(MainPojo.class)
                .withSettings(Settings.create().set(Keys.SET_BACK_REFERENCES, true))
                .generate(field(MainPojo::getDetailPojos), gen -> gen.collection().size(numOfDetailPojos))
                .onComplete(all(MainPojo.class), o -> mainPojoCount.incrementAndGet())
                .create();

        // Callback is invoked on the root object + each back reference to the root
        assertThat(mainPojoCount.get()).isEqualTo(1 + numOfDetailPojos);
    }
}

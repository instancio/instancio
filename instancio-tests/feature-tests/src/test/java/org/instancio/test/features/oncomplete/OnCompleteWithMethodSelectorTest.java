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
package org.instancio.test.features.oncomplete;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.OnSetMethodUnmatched;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.dynamic.DynAddress;
import org.instancio.test.support.pojo.dynamic.DynPerson;
import org.instancio.test.support.pojo.dynamic.DynPhone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.setter;

@RunWith.MethodAssignmentOnly
@FeatureTag({Feature.ASSIGNMENT_TYPE_METHOD, Feature.ON_COMPLETE})
@ExtendWith(InstancioExtension.class)
class OnCompleteWithMethodSelectorTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
            .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE)
            .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE);

    private final AtomicInteger callbacksCount = new AtomicInteger();

    @Test
    void onComplete() {
        final int expectedSize = 3;

        final DynPerson result = Instancio.of(DynPerson.class)
                .generate(setter(DynAddress::setPhoneNumbers), gen -> gen.collection().size(expectedSize))
                .onComplete(setter(DynPhone::setNumber), number -> callbacksCount.incrementAndGet())
                .create();

        assertThat(callbacksCount.get()).isEqualTo(expectedSize);
        assertThat(result.getAddress().getPhoneNumbers()).hasSize(expectedSize);
    }

}

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
package org.instancio.api;

import org.instancio.InstancioApi;
import org.instancio.feed.Feed;
import org.junit.jupiter.api.Test;

import static org.instancio.test.support.asserts.ClassAssert.assertThatSuperInterface;

class PackagePrivateClassesTest {

    @Test
    void instancioApis_shouldNotBePublic() {
        assertThatSuperInterface(InstancioApi.class, "BaseApi").isNotPublic();
        assertThatSuperInterface(InstancioApi.class, "LenientModeApi").isNotPublic();
        assertThatSuperInterface(InstancioApi.class, "SettingsApi").isNotPublic();
        assertThatSuperInterface(InstancioApi.class, "VerboseModeApi").isNotPublic();
    }

    @Test
    void feedApis_shouldNotBePublic() {
        assertThatSuperInterface(Feed.class, "FeedSpecAccessors").isNotPublic();
    }
}

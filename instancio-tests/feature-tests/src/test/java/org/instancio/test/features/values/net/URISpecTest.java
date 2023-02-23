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
package org.instancio.test.features.values.net;

import org.instancio.Gen;
import org.instancio.generator.specs.URISpec;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Gen.net;

@FeatureTag(Feature.VALUE_SPEC)
class URISpecTest extends AbstractValueSpecTestTemplate<URI> {

    @Override
    protected URISpec spec() {
        return Gen.net().uri();
    }

    @Test
    void scheme() {
        assertThat(net().uri().scheme("scheme").get()).hasScheme("scheme");
    }

    @Test
    void userInfo() {
        assertThat(net().uri().userInfo("foo").get()).hasUserInfo("foo");
    }

    @Test
    void host() {
        assertThat(net().uri().host(r -> "foo").get()).hasHost("foo");
    }

    @Test
    void port() {
        assertThat(net().uri().port(1234).get()).hasPort(1234);
    }

    @Test
    void query() {
        assertThat(net().uri().query(r -> "foo").get()).hasQuery("foo");
    }

    @Test
    void fragment() {
        assertThat(net().uri().fragment(r -> "foo").get()).hasFragment("foo");
    }
}

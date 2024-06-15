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
package org.instancio.test.features.values.net;

import org.instancio.Instancio;
import org.instancio.generator.specs.URISpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.VALUE_SPEC)
@ExtendWith(InstancioExtension.class)
class URISpecTest extends AbstractValueSpecTestTemplate<URI> {

    @Override
    protected URISpec spec() {
        return Instancio.gen().net().uri();
    }

    @Test
    void scheme() {
        assertThat(spec().scheme("scheme").get()).hasScheme("scheme");
    }

    @Test
    void userInfo() {
        assertThat(spec().userInfo("foo").get()).hasUserInfo("foo");
    }

    @Test
    void host() {
        assertThat(spec().host(r -> "foo").get()).hasHost("foo");
    }

    @Test
    void port() {
        assertThat(spec().port(1234).get()).hasPort(1234);
    }

    @Test
    void query() {
        assertThat(spec().query(r -> "foo").get()).hasQuery("foo");
    }

    @Test
    void fragment() {
        assertThat(spec().fragment(r -> "foo").get()).hasFragment("foo");
    }
}

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
package org.instancio.test.beanvalidation;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.pojo.beanvalidation.UrlBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.util.HibernateValidatorUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class UrlBVTest {

    @Test
    void withDefaults() {
        final UrlBV.WithDefaults result = Instancio.create(UrlBV.WithDefaults.class);

        HibernateValidatorUtil.assertValid(result);
    }

    @Test
    void withAttributes() throws MalformedURLException {
        final UrlBV.WithAttributes result = Instancio.create(UrlBV.WithAttributes.class);

        HibernateValidatorUtil.assertValid(result);

        final URL url = new URL(result.getValue());

        assertThat(url).isNotNull()
                .hasProtocol(UrlBV.PROTOCOL)
                .hasHost(UrlBV.HOST)
                .hasPort(UrlBV.PORT);
    }
}

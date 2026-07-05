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
package org.instancio.tests.jpms.kotlin;

import org.instancio.GetMethodSelector;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Select;
import org.instancio.Selector;
import org.instancio.exception.InstancioApiException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * These tests use <b>Java</b>-authored {@link GetMethodSelector} references
 * when the Kotlin runtime ({@code kotlin.stdlib}) is on the module path.
 */
class JavaMethodSelectorInteropTest {

    static class Bean {
        private String value;

        public String getValue() {
            return value;
        }
    }

    @Test
    void javaMethodReferenceResolvesWithKotlinOnModulePath() {
        // Java method reference -> REF_invokeVirtual (not a static adapter)
        final GetMethodSelector<Bean, String> selector = Bean::getValue;

        final Bean result = Instancio.of(Bean.class)
                .set(Select.field(selector), "foo")
                .create();

        assertThat(result.getValue()).isEqualTo("foo");
    }

    @Test
    @SuppressWarnings({"Convert2MethodRef", "java:S1612"})
    void javaLambdaSelectorIsRejected() {
        // Java lambda -> REF_invokeStatic in a class without Kotlin @Metadata
        final GetMethodSelector<Bean, String> getMethodSelector = bean -> bean.getValue();
        final Selector select = Select.field(getMethodSelector);

        final InstancioApi<Bean> api = Instancio.of(Bean.class);

        assertThatThrownBy(() -> api.set(select, "foo"))
                .isInstanceOf(InstancioApiException.class);
    }
}

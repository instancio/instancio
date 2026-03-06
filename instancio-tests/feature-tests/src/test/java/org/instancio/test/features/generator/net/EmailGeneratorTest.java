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
package org.instancio.test.features.generator.net;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.Select.root;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class EmailGeneratorTest {

    @Test
    void create() {
        final String result = Instancio.of(String.class)
                .generate(root(), gen -> gen.net().email())
                .create();

        assertThat(result).contains("@");
    }

    @SuppressWarnings("NullAway")
    @Test
    void asEmailObject() {
        final Contact result = Instancio.of(Contact.class)
                .generate(field("email"), gen -> gen.net().email().as(Email::new))
                .create();

        final Email email = result.email;
        assertThat(email).isNotNull();
        assertThat(email.email).contains("@");
    }

    private static class Email {
        final @Nullable String email;

        Email(@Nullable String email) {
            this.email = email;
        }
    }

    private static class Contact {
        @Nullable Email email;
    }
}
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
package org.example.spi;

import org.instancio.spi.InstancioServiceProvider;
import org.jspecify.annotations.Nullable;

import static org.instancio.Select.field;
import static org.instancio.Select.fields;

public class CustomTemplateProvider implements InstancioServiceProvider {

    public record TemplateId(String value) {}

    public record TemplateRecord(String name, @Nullable String nickname, @Nullable TemplateId id) {}

    public static class TemplatePojo {
        public String initialised = "initial";
        public String other;
    }

    @Override
    public TemplateProvider getTemplateProvider() {
        return t -> t
                .withNullable(fields(f -> f.getDeclaringClass() == TemplateRecord.class
                        && f.getAnnotatedType().isAnnotationPresent(Nullable.class)))
                .generate(field(TemplateRecord::name), gen -> gen.string().prefix("template_"))
                .ignore(field(TemplatePojo.class, "initialised"));
    }
}

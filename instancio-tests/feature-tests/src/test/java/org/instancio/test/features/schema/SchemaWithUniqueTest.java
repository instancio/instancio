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
package org.instancio.test.features.schema;

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaResource;
import org.instancio.schema.SchemaSpec;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag({Feature.SCHEMA, Feature.VALUE_SPEC, Feature.GENERATE})
@ExtendWith(InstancioExtension.class)
class SchemaWithUniqueTest {

    @SchemaResource(data = "id,value\n1,v1\n2,v2\n3,v3\n4,v4\n5,v5")
    private interface SampleSchema extends Schema {
        SchemaSpec<String> id();

        SchemaSpec<String> value();
    }

    @FeatureTag(Feature.UNSUPPORTED)
    @Disabled("TODO: support for unique data records")
    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void withUnique() {
        @Data
        class Pojo {
            String id, value;
        }

        final SampleSchema schema = Instancio.createSchema(SampleSchema.class);

        final List<Pojo> results = Instancio.ofList(Pojo.class)
                .size(5)
                .withUnique(field(Pojo::getValue))
                .generate(field(Pojo::getId), schema.id())
                .generate(field(Pojo::getValue), schema.value())
                .create();

        assertThat(results).hasSize(5).allSatisfy(pojo -> {
            assertThat(pojo.value).endsWith(pojo.id);
        });
    }
}

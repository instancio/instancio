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

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaResource;
import org.instancio.schema.SchemaSpec;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.SCHEMA, Feature.VALUE_SPEC})
@ExtendWith(InstancioExtension.class)
class SchemaInheritanceTest {

    @SchemaResource(data = "parentSpec\np1")
    private interface ParentSchema extends Schema {
        SchemaSpec<String> parentSpec();
    }

    @SchemaResource(data = "parentSpec,childSpec\np1,c1")
    private interface ChildSchema extends ParentSchema {
        SchemaSpec<String> childSpec();
    }

    @Test
    void inlineData() {
        final ChildSchema result = Instancio.createSchema(ChildSchema.class);

        assertThat(result.parentSpec().get()).isEqualTo("p1");
        assertThat(result.childSpec().get()).isEqualTo("c1");
    }
}

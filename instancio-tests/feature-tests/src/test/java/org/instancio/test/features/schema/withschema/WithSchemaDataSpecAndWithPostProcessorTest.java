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
package org.instancio.test.features.schema.withschema;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.junit.InstancioExtension;
import org.instancio.schema.DataSpec;
import org.instancio.schema.PostProcessor;
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaResource;
import org.instancio.schema.SchemaSpec;
import org.instancio.schema.WithPostProcessor;
import org.instancio.settings.Keys;
import org.instancio.settings.SchemaDataEndStrategy;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag({Feature.SCHEMA, Feature.WITH_SCHEMA})
@ExtendWith(InstancioExtension.class)
class WithSchemaDataSpecAndWithPostProcessorTest {

    @SuppressWarnings("unused")
    @SchemaResource(data = "countryCode,_number_\nc1,n1\nc2,n2\nc3,n3\n")
    private interface PhoneSchema extends Schema {

        @WithPostProcessor(ZeroAppender.class)
        SchemaSpec<String> countryCode();

        // The mapping should be done based on the SchemaSpec method name,
        // which takes precedence over the data property key.
        @DataSpec(propertyName = "_number_")
        @WithPostProcessor(ZeroAppender.class)
        SchemaSpec<String> number();

        class ZeroAppender implements PostProcessor<String> {
            @Override
            public String process(final String input, final Random random) {
                return input + "0";
            }
        }
    }

    /**
     * Should use
     *
     * <ul>
     *   <li>{@link PhoneSchema#countryCode()}</li>
     *   <li>{@link PhoneSchema#number()}</li>
     * </ul>
     *
     * <p>methods when populating the objects, taking into account
     * {@link DataSpec} and {@link WithPostProcessor} annotations.
     */
    @RepeatedTest(5)
    void shouldPopulateObjectsUsingDeclaredSchemaSpecMethods() {
        final Schema schema = Instancio.ofSchema(PhoneSchema.class)
                .withSetting(Keys.SCHEMA_DATA_END_STRATEGY, SchemaDataEndStrategy.RECYCLE)
                .create();

        final int numPersons = 2;
        final int numPhonesPerPerson = 3;

        final List<Person> results = Instancio.ofList(Person.class)
                .size(numPersons)
                .generate(field(Address::getPhoneNumbers), gen -> gen.collection().size(numPhonesPerPerson))
                .withSchema(all(Phone.class), schema)
                .create();

        final List<Phone> phones = results.stream()
                .flatMap(p -> p.getAddress().getPhoneNumbers().stream())
                .collect(Collectors.toList());

        assertThat(phones)
                .hasSize(numPersons * numPhonesPerPerson)
                .extracting(Phone::getCountryCode)
                .containsExactly("c10", "c20", "c30", "c10", "c20", "c30");

        assertThat(phones)
                .extracting(Phone::getNumber)
                .containsExactly("n10", "n20", "n30", "n10", "n20", "n30");
    }
}

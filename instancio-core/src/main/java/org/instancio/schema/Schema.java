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
package org.instancio.schema;

import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.documentation.ExperimentalApi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.function.Function;

/**
 * This class provides an abstraction for mapping an external
 * data source to a collection of methods that return a {@link SchemaSpec}.
 * This is an experimental feature. Currently, data sources are limited
 * to CSV files only.
 *
 * <p>For example, given the following CSV file:
 *
 * <pre>
 * firstName, lastName, age
 * John,      Doe,      21
 * Alice,     Smith,    34
 * # ... snip
 * </pre>
 *
 * <p>we can define a schema interface as:
 *
 * <pre>{@code
 * @SchemaResource(path = "persons.csv")
 * interface PersonSchema extends Schema {
 *     SchemaSpec<String> firstName();
 *     SchemaSpec<String> lastName();
 *     SchemaSpec<Integer> age();
 * }
 * }</pre>
 *
 * <p>With this setup in place, schemas support the following use cases.
 *
 * <h2>1. Using the schema directly to get values</h2>
 *
 * <pre>{@code
 * PersonSchema schema = Instancio.createSchema(PersonSchema.class);
 *
 * // invoking schema methods will select a random record from the file:
 * schema.firstName().get(); // Alice
 * schema.lastName().get();  // Smith
 * schema.lastName().get();  // 34
 *
 * // subsequent invocations will select a new random record from the file:
 * schema.firstName().get(); // John
 * schema.lastName().get();  // Doe
 * schema.lastName().get();  // 21
 * }</pre>
 *
 * <h2>2. Using the schema with the {@code generate()} method</h2>
 *
 * <p>Assuming we have a {@code Person} POJO, we can populate it with
 * data from the schema as follows:
 *
 * <pre>{@code
 * PersonSchema schema = Instancio.createSchema(PersonSchema.class);
 *
 * List<Person> person = Instancio.ofList(Person.class)
 *     .size(10)
 *     .generate(field(Person::getFirstName), schema.firstName())
 *     .generate(field(Person::getLastName), schema.lastName())
 *     .create();
 * }</pre>
 *
 * <h2>3. Mapping schema properties to objects</h2>
 * <p>
 * This can be done using {@link InstancioApi#withSchema(TargetSelector, Schema)}
 * (see the method's Javadoc for more details):
 *
 * <pre>{@code
 * Schema personSchema = Instancio.createSchema(PersonSchema.class);
 *
 * List<Person> person = Instancio.ofList(Person.class)
 *     .size(10)
 *     .withSchema(all(Person.class), personSchema)
 *     .create();
 * }</pre>
 *
 * @since 5.0.0
 */
@ExperimentalApi
public interface Schema {

    /**
     * Returns a spec for the given property name.
     * The value will be converted using
     * the specified {@code converter} function.
     *
     * @param propertyName to return as a spec
     * @param converter    for mapping the string value to the target type
     * @param <T>          the target type to convert the value to
     * @return spec for the given property name
     */
    <T> SchemaSpec<T> spec(String propertyName, Function<String, T> converter);

    /**
     * Returns a spec for the given property name.
     * The value will be converted to the specified {@code targetType}
     * using built-in converters.
     *
     * @param propertyName to return as a spec
     * @param targetType   the type that the spec should return
     * @param <T>          the target type of values returned by the spec
     * @return spec for the given property name
     */
    <T> SchemaSpec<T> spec(String propertyName, Class<T> targetType);

    /**
     * Returns a {@code String} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<String> stringSpec(String propertyName);

    /**
     * Returns a {@code Boolean} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<Boolean> booleanSpec(String propertyName);

    /**
     * Returns a {@code Character} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<Character> characterSpec(String propertyName);

    /**
     * Returns a {@code Byte} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<Byte> byteSpec(String propertyName);

    /**
     * Returns a {@code Short} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<Short> shortSpec(String propertyName);

    /**
     * Returns a {@code Integer} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<Integer> integerSpec(String propertyName);

    /**
     * Returns a {@code Long} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<Long> longSpec(String propertyName);

    /**
     * Returns a {@code Double} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<Double> doubleSpec(String propertyName);

    /**
     * Returns a {@code Float} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<Float> floatSpec(String propertyName);

    /**
     * Returns a {@code BigInteger} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<BigInteger> bigIntegerSpec(String propertyName);

    /**
     * Returns a {@code BigDecimal} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<BigDecimal> bigDecimalSpec(String propertyName);

    /**
     * Returns an {@code Instant} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<Instant> instantSpec(String propertyName);

    /**
     * Returns a {@code LocalTime} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<LocalTime> localTimeSpec(String propertyName);

    /**
     * Returns a {@code LocalDate} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<LocalDate> localDateSpec(String propertyName);

    /**
     * Returns a {@code LocalDateTime} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<LocalDateTime> localDateTimeSpec(String propertyName);

    /**
     * Returns an {@code OffsetTime} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<OffsetTime> offsetTimeSpec(String propertyName);

    /**
     * Returns an {@code offsetDateTime} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<OffsetDateTime> offsetDateTimeSpec(String propertyName);

    /**
     * Returns a {@code ZonedDateTime} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<ZonedDateTime> zonedDateTimeSpec(String propertyName);

    /**
     * Returns a {@code YearMonth} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<YearMonth> yearMonthSpec(String propertyName);

    /**
     * Returns a {@code Year} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<Year> yearSpec(String propertyName);

    /**
     * Returns a {@code UUID} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    SchemaSpec<UUID> uuidSpec(String propertyName);
}

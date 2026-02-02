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
package org.instancio.feed;

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
 * Defines built-in accessors for a {@link Feed}.
 *
 * @since 5.0.0
 */
@ExperimentalApi
interface FeedSpecAccessors {

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
    <T> FeedSpec<T> spec(String propertyName, Function<String, T> converter);

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
    <T> FeedSpec<T> spec(String propertyName, Class<T> targetType);

    /**
     * Returns a {@code String} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<String> stringSpec(String propertyName);

    /**
     * Returns a {@code Boolean} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<Boolean> booleanSpec(String propertyName);

    /**
     * Returns a {@code Character} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<Character> characterSpec(String propertyName);

    /**
     * Returns a {@code Byte} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<Byte> byteSpec(String propertyName);

    /**
     * Returns a {@code Short} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<Short> shortSpec(String propertyName);

    /**
     * Returns a {@code Integer} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<Integer> intSpec(String propertyName);

    /**
     * Returns a {@code Long} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<Long> longSpec(String propertyName);

    /**
     * Returns a {@code Double} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<Double> doubleSpec(String propertyName);

    /**
     * Returns a {@code Float} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<Float> floatSpec(String propertyName);

    /**
     * Returns a {@code BigInteger} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<BigInteger> bigIntegerSpec(String propertyName);

    /**
     * Returns a {@code BigDecimal} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<BigDecimal> bigDecimalSpec(String propertyName);

    /**
     * Returns an {@code Instant} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<Instant> instantSpec(String propertyName);

    /**
     * Returns a {@code LocalTime} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<LocalTime> localTimeSpec(String propertyName);

    /**
     * Returns a {@code LocalDate} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<LocalDate> localDateSpec(String propertyName);

    /**
     * Returns a {@code LocalDateTime} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<LocalDateTime> localDateTimeSpec(String propertyName);

    /**
     * Returns an {@code OffsetTime} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<OffsetTime> offsetTimeSpec(String propertyName);

    /**
     * Returns an {@code offsetDateTime} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<OffsetDateTime> offsetDateTimeSpec(String propertyName);

    /**
     * Returns a {@code ZonedDateTime} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<ZonedDateTime> zonedDateTimeSpec(String propertyName);

    /**
     * Returns a {@code YearMonth} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<YearMonth> yearMonthSpec(String propertyName);

    /**
     * Returns a {@code Year} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<Year> yearSpec(String propertyName);

    /**
     * Returns a {@code UUID} spec for the given property name.
     *
     * @param propertyName the name of the property to return as a spec
     * @return spec for the given property name
     */
    FeedSpec<UUID> uuidSpec(String propertyName);
}

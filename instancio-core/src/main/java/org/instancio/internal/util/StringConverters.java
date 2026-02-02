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
package org.instancio.internal.util;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public final class StringConverters {
    private static final Map<Class<?>, Function<String, ?>> MAPPERS = Collections.unmodifiableMap(getFunctionMap());

    private static Map<Class<?>, Function<String, ?>> getFunctionMap() {
        final Map<Class<?>, Function<String, ?>> map = new HashMap<>();
        map.put(String.class, Function.identity());
        map.put(boolean.class, Boolean::valueOf);
        map.put(char.class, s -> s.charAt(0));
        map.put(int.class, Integer::valueOf);
        map.put(long.class, Long::valueOf);
        map.put(byte.class, Byte::valueOf);
        map.put(short.class, Short::valueOf);
        map.put(float.class, Float::valueOf);
        map.put(double.class, Double::valueOf);
        map.put(Boolean.class, Boolean::valueOf);
        map.put(Character.class, s -> s.charAt(0));
        map.put(Integer.class, Integer::valueOf);
        map.put(Long.class, Long::valueOf);
        map.put(Byte.class, Byte::valueOf);
        map.put(Short.class, Short::valueOf);
        map.put(Float.class, Float::valueOf);
        map.put(Double.class, Double::valueOf);
        map.put(BigInteger.class, BigInteger::new);
        map.put(BigDecimal.class, BigDecimal::new);
        map.put(Instant.class, Instant::parse);
        map.put(LocalTime.class, LocalTime::parse);
        map.put(LocalDate.class, LocalDate::parse);
        map.put(LocalDateTime.class, LocalDateTime::parse);
        map.put(OffsetTime.class, OffsetTime::parse);
        map.put(OffsetDateTime.class, OffsetDateTime::parse);
        map.put(ZonedDateTime.class, ZonedDateTime::parse);
        map.put(YearMonth.class, YearMonth::parse);
        map.put(Year.class, Year::parse);
        map.put(UUID.class, UUID::fromString);
        return map;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Function<String, T> getConverter(final Class<T> targetType) {
        final Function<String, T> mappingFn = (Function<String, T>) MAPPERS.get(targetType);

        if (mappingFn != null) {
            return mappingFn;
        }
        if (targetType.isEnum()) {
            return value -> (T) Enum.valueOf((Class) targetType, value);
        }

        return source -> {
            throw Fail.withUsageError("cannot map value '%s' (unsupported target type: %s)",
                    source, targetType.getTypeName());
        };
    }

    private StringConverters() {
        // non-instantiable
    }
}

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
package org.instancio.internal.generator;

import org.instancio.internal.generator.io.FileGenerator;
import org.instancio.internal.generator.lang.BooleanGenerator;
import org.instancio.internal.generator.lang.ByteGenerator;
import org.instancio.internal.generator.lang.CharacterGenerator;
import org.instancio.internal.generator.lang.ClassGenerator;
import org.instancio.internal.generator.lang.DoubleGenerator;
import org.instancio.internal.generator.lang.FloatGenerator;
import org.instancio.internal.generator.lang.IntegerGenerator;
import org.instancio.internal.generator.lang.LongGenerator;
import org.instancio.internal.generator.lang.ShortGenerator;
import org.instancio.internal.generator.lang.StringBuilderGenerator;
import org.instancio.internal.generator.lang.StringGenerator;
import org.instancio.internal.generator.math.BigDecimalGenerator;
import org.instancio.internal.generator.math.BigIntegerGenerator;
import org.instancio.internal.generator.net.InetAddressGenerator;
import org.instancio.internal.generator.net.URIGenerator;
import org.instancio.internal.generator.net.URLGenerator;
import org.instancio.internal.generator.nio.file.PathGenerator;
import org.instancio.internal.generator.time.DurationGenerator;
import org.instancio.internal.generator.time.InstantGenerator;
import org.instancio.internal.generator.time.LocalDateGenerator;
import org.instancio.internal.generator.time.LocalDateTimeGenerator;
import org.instancio.internal.generator.time.LocalTimeGenerator;
import org.instancio.internal.generator.time.MonthDayGenerator;
import org.instancio.internal.generator.time.OffsetDateTimeGenerator;
import org.instancio.internal.generator.time.OffsetTimeGenerator;
import org.instancio.internal.generator.time.PeriodGenerator;
import org.instancio.internal.generator.time.TimeZoneGenerator;
import org.instancio.internal.generator.time.YearGenerator;
import org.instancio.internal.generator.time.YearMonthGenerator;
import org.instancio.internal.generator.time.ZoneIdGenerator;
import org.instancio.internal.generator.time.ZoneOffsetGenerator;
import org.instancio.internal.generator.time.ZonedDateTimeGenerator;
import org.instancio.internal.generator.util.CalendarGenerator;
import org.instancio.internal.generator.util.CollectionGenerator;
import org.instancio.internal.generator.util.DateGenerator;
import org.instancio.internal.generator.util.EnumSetGenerator;
import org.instancio.internal.generator.util.LocaleGenerator;
import org.instancio.internal.generator.util.MapEntryGenerator;
import org.instancio.internal.generator.util.MapGenerator;
import org.instancio.internal.generator.util.OptionalGenerator;
import org.instancio.internal.generator.util.UUIDGenerator;
import org.instancio.internal.generator.util.concurrent.atomic.AtomicBooleanGenerator;
import org.instancio.internal.generator.util.concurrent.atomic.AtomicIntegerGenerator;
import org.instancio.internal.generator.util.concurrent.atomic.AtomicLongGenerator;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.ExcessiveImports", "PMD.LooseCoupling", "PMD.NcssCount"})
final class GeneratorResolverMaps {
    private static final Map<Class<?>, Class<?>> SUBTYPES = initSubtypes();
    private static final Map<Class<?>, Class<?>> GENERATORS = initGeneratorMap();

    static Class<?> getSubtype(final Class<?> klass) {
        return SUBTYPES.get(klass);
    }

    static Class<?> getGenerator(final Class<?> klass) {
        return GENERATORS.get(klass);
    }

    private static Map<Class<?>, Class<?>> initSubtypes() {
        final Map<Class<?>, Class<?>> map = new HashMap<>(32);
        map.put(BlockingDeque.class, LinkedBlockingDeque.class);
        map.put(BlockingQueue.class, LinkedBlockingQueue.class);
        map.put(Collection.class, ArrayList.class);
        map.put(Deque.class, ArrayDeque.class);
        map.put(Iterable.class, ArrayList.class);
        map.put(List.class, ArrayList.class);
        map.put(NavigableSet.class, TreeSet.class);
        map.put(Set.class, HashSet.class);
        map.put(SortedSet.class, TreeSet.class);
        map.put(TransferQueue.class, LinkedTransferQueue.class);
        map.put(Queue.class, ArrayDeque.class);
        // Maps
        map.put(ConcurrentMap.class, ConcurrentHashMap.class);
        map.put(ConcurrentNavigableMap.class, ConcurrentSkipListMap.class);
        map.put(EnumMap.class, HashMap.class);
        map.put(Map.class, HashMap.class);
        map.put(NavigableMap.class, TreeMap.class);
        map.put(SortedMap.class, TreeMap.class);
        return Collections.unmodifiableMap(map);
    }

    private static Map<Class<?>, Class<?>> initGeneratorMap() {
        final Map<Class<?>, Class<?>> map = new HashMap<>(128);

        // Core types
        map.put(byte.class, ByteGenerator.class);
        map.put(short.class, ShortGenerator.class);
        map.put(int.class, IntegerGenerator.class);
        map.put(long.class, LongGenerator.class);
        map.put(float.class, FloatGenerator.class);
        map.put(double.class, DoubleGenerator.class);
        map.put(boolean.class, BooleanGenerator.class);
        map.put(char.class, CharacterGenerator.class);
        map.put(Byte.class, ByteGenerator.class);
        map.put(Short.class, ShortGenerator.class);
        map.put(Integer.class, IntegerGenerator.class);
        map.put(Long.class, LongGenerator.class);
        map.put(Float.class, FloatGenerator.class);
        map.put(Double.class, DoubleGenerator.class);
        map.put(Boolean.class, BooleanGenerator.class);
        map.put(Character.class, CharacterGenerator.class);
        map.put(String.class, StringGenerator.class);

        // java.lang
        map.put(Class.class, ClassGenerator.class);
        map.put(Number.class, IntegerGenerator.class);
        map.put(CharSequence.class, StringGenerator.class);  // default CharSequence
        map.put(StringBuilder.class, StringBuilderGenerator.class);

        // java.math
        map.put(BigDecimal.class, BigDecimalGenerator.class);
        map.put(BigInteger.class, BigIntegerGenerator.class);

        // java.net
        map.put(InetAddress.class, InetAddressGenerator.class);
        map.put(Inet4Address.class, InetAddressGenerator.class);
        map.put(URI.class, URIGenerator.class);
        map.put(URL.class, URLGenerator.class);

        // java.io
        map.put(File.class, FileGenerator.class);

        // java.nio
        map.put(Path.class, PathGenerator.class);

        // java.time
        map.put(Instant.class, InstantGenerator.class);
        map.put(LocalDate.class, LocalDateGenerator.class);
        map.put(LocalDateTime.class, LocalDateTimeGenerator.class);
        map.put(LocalTime.class, LocalTimeGenerator.class);
        map.put(Duration.class, DurationGenerator.class);
        map.put(MonthDay.class, MonthDayGenerator.class);
        map.put(OffsetTime.class, OffsetTimeGenerator.class);
        map.put(OffsetDateTime.class, OffsetDateTimeGenerator.class);
        map.put(Period.class, PeriodGenerator.class);
        map.put(Temporal.class, LocalDateGenerator.class); // default Temporal
        map.put(Year.class, YearGenerator.class);
        map.put(YearMonth.class, YearMonthGenerator.class);
        map.put(ZonedDateTime.class, ZonedDateTimeGenerator.class);
        map.put(ZoneId.class, ZoneIdGenerator.class);
        map.put(ZoneOffset.class, ZoneOffsetGenerator.class);

        // java.util
        map.put(AbstractMap.SimpleEntry.class, MapEntryGenerator.class);
        map.put(Calendar.class, CalendarGenerator.class);
        map.put(Date.class, DateGenerator.class);
        map.put(Locale.class, LocaleGenerator.class);
        map.put(Map.Entry.class, MapEntryGenerator.class);
        map.put(Optional.class, OptionalGenerator.class);
        map.put(TimeZone.class, TimeZoneGenerator.class);
        map.put(UUID.class, UUIDGenerator.class);

        // java.util
        // Collections
        map.put(Collection.class, CollectionGenerator.class);
        map.put(EnumSet.class, EnumSetGenerator.class);
        map.put(Iterable.class, CollectionGenerator.class);
        map.put(List.class, CollectionGenerator.class);
        map.put(ArrayList.class, CollectionGenerator.class);
        map.put(BlockingDeque.class, CollectionGenerator.class);
        map.put(BlockingQueue.class, CollectionGenerator.class);
        map.put(Deque.class, CollectionGenerator.class);
        map.put(NavigableSet.class, CollectionGenerator.class);
        map.put(Set.class, CollectionGenerator.class);
        map.put(SortedSet.class, CollectionGenerator.class);
        map.put(TransferQueue.class, CollectionGenerator.class);
        map.put(Queue.class, CollectionGenerator.class);
        // Maps
        map.put(Map.class, MapGenerator.class);
        map.put(ConcurrentMap.class, MapGenerator.class);
        map.put(ConcurrentNavigableMap.class, MapGenerator.class);
        map.put(EnumMap.class, MapGenerator.class);
        map.put(NavigableMap.class, MapGenerator.class);
        map.put(SortedMap.class, MapGenerator.class);

        // java.util.concurrent
        map.put(AtomicBoolean.class, AtomicBooleanGenerator.class);
        map.put(AtomicInteger.class, AtomicIntegerGenerator.class);
        map.put(AtomicLong.class, AtomicLongGenerator.class);
        return Collections.unmodifiableMap(map);
    }

    private GeneratorResolverMaps() {
        // non-instantiable
    }
}

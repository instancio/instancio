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

import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.array.ArrayGenerator;
import org.instancio.internal.generator.io.FileGenerator;
import org.instancio.internal.generator.lang.BooleanGenerator;
import org.instancio.internal.generator.lang.ByteGenerator;
import org.instancio.internal.generator.lang.CharacterGenerator;
import org.instancio.internal.generator.lang.DoubleGenerator;
import org.instancio.internal.generator.lang.EnumGenerator;
import org.instancio.internal.generator.lang.FloatGenerator;
import org.instancio.internal.generator.lang.IntegerGenerator;
import org.instancio.internal.generator.lang.LongGenerator;
import org.instancio.internal.generator.lang.ShortGenerator;
import org.instancio.internal.generator.lang.StringBuilderGenerator;
import org.instancio.internal.generator.lang.StringGenerator;
import org.instancio.internal.generator.math.BigDecimalGenerator;
import org.instancio.internal.generator.math.BigIntegerGenerator;
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
import org.instancio.internal.generator.util.MapGenerator;
import org.instancio.internal.generator.util.OptionalGenerator;
import org.instancio.internal.generator.util.UUIDGenerator;
import org.instancio.internal.generator.util.concurrent.atomic.AtomicIntegerGenerator;
import org.instancio.internal.generator.util.concurrent.atomic.AtomicLongGenerator;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.ServiceLoaders;
import org.instancio.spi.GeneratorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Collection;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.CouplingBetweenObjects"})
public class GeneratorResolver {
    private static final Logger LOG = LoggerFactory.getLogger(GeneratorResolver.class);

    private static final List<GeneratorProvider> PROVIDERS = ServiceLoaders.loadAll(GeneratorProvider.class);
    private final Map<Class<?>, Generator<?>> generators = new HashMap<>();
    private final GeneratorContext context;
    private final GeneratorProviderFacade generatorProviderFacade;

    public GeneratorResolver(final GeneratorContext context) {
        this.context = context;
        this.generatorProviderFacade = new GeneratorProviderFacade(context, PROVIDERS);
        initGeneratorMap();
    }

    @SuppressWarnings({"PMD.NcssCount", "PMD.LooseCoupling", "PMD.ExcessiveMethodLength"})
    private void initGeneratorMap() {
        // Core types
        generators.put(byte.class, new ByteGenerator(context));
        generators.put(short.class, new ShortGenerator(context));
        generators.put(int.class, new IntegerGenerator(context));
        generators.put(long.class, new LongGenerator(context));
        generators.put(float.class, new FloatGenerator(context));
        generators.put(double.class, new DoubleGenerator(context));
        generators.put(boolean.class, new BooleanGenerator(context));
        generators.put(char.class, new CharacterGenerator(context));
        generators.put(Byte.class, new ByteGenerator(context));
        generators.put(Short.class, new ShortGenerator(context));
        generators.put(Integer.class, new IntegerGenerator(context));
        generators.put(Long.class, new LongGenerator(context));
        generators.put(Float.class, new FloatGenerator(context));
        generators.put(Double.class, new DoubleGenerator(context));
        generators.put(Boolean.class, new BooleanGenerator(context));
        generators.put(Character.class, new CharacterGenerator(context));
        generators.put(String.class, new StringGenerator(context));

        // java.lang
        generators.put(Number.class, new IntegerGenerator(context));
        generators.put(CharSequence.class, new StringGenerator(context));  // default CharSequence
        generators.put(StringBuilder.class, new StringBuilderGenerator(context));

        // java.math
        generators.put(BigDecimal.class, new BigDecimalGenerator(context));
        generators.put(BigInteger.class, new BigIntegerGenerator(context));

        // java.net
        generators.put(URI.class, new URIGenerator(context));
        generators.put(URL.class, new URLGenerator(context));

        // java.io
        generators.put(File.class, new FileGenerator(context));

        // java.nio
        generators.put(Path.class, new PathGenerator(context));

        // java.time
        generators.put(Instant.class, new InstantGenerator(context));
        generators.put(LocalDate.class, new LocalDateGenerator(context));
        generators.put(LocalDateTime.class, new LocalDateTimeGenerator(context));
        generators.put(LocalTime.class, new LocalTimeGenerator(context));
        generators.put(Duration.class, new DurationGenerator(context));
        generators.put(MonthDay.class, new MonthDayGenerator(context));
        generators.put(OffsetTime.class, new OffsetTimeGenerator(context));
        generators.put(OffsetDateTime.class, new OffsetDateTimeGenerator(context));
        generators.put(Period.class, new PeriodGenerator(context));
        generators.put(Temporal.class, new LocalDateGenerator(context)); // default Temporal
        generators.put(Year.class, new YearGenerator(context));
        generators.put(YearMonth.class, new YearMonthGenerator(context));
        generators.put(ZonedDateTime.class, new ZonedDateTimeGenerator(context));
        generators.put(ZoneId.class, new ZoneIdGenerator(context));
        generators.put(ZoneOffset.class, new ZoneOffsetGenerator(context));

        // java.util
        generators.put(Calendar.class, new CalendarGenerator(context));
        generators.put(Date.class, new DateGenerator(context));
        generators.put(Locale.class, new LocaleGenerator(context));
        generators.put(Optional.class, new OptionalGenerator<>(context));
        generators.put(UUID.class, new UUIDGenerator(context));

        // java.util collections
        generators.put(Collection.class, new CollectionGenerator<>(context));
        generators.put(EnumSet.class, new EnumSetGenerator<>(context));
        generators.put(List.class, new CollectionGenerator<>(context));
        generators.put(Map.class, new MapGenerator<>(context));
        mapCollection(BlockingDeque.class, LinkedBlockingDeque.class);
        mapCollection(BlockingQueue.class, LinkedBlockingQueue.class);
        mapCollection(Deque.class, ArrayDeque.class);
        mapCollection(NavigableSet.class, TreeSet.class);
        mapCollection(Set.class, HashSet.class);
        mapCollection(SortedSet.class, TreeSet.class);
        mapCollection(TransferQueue.class, LinkedTransferQueue.class);
        mapCollection(Queue.class, ArrayDeque.class);
        mapMap(ConcurrentMap.class, ConcurrentHashMap.class);
        mapMap(ConcurrentNavigableMap.class, ConcurrentSkipListMap.class);
        // EnumMap is created from HashMap using InternalContainerFactoryProvider
        mapMap(EnumMap.class, HashMap.class);
        mapMap(NavigableMap.class, TreeMap.class);
        mapMap(SortedMap.class, TreeMap.class);

        // java.util.concurrent
        generators.put(AtomicInteger.class, new AtomicIntegerGenerator(context));
        generators.put(AtomicLong.class, new AtomicLongGenerator(context));

        // Java modules will not have these legacy classes unless explicitly
        // imported via e.g. "requires java.sql". In case the classes are not
        // available, load them by name to avoid class not found error
        mapByClassName(context, generators,
                "java.sql.Date",
                "org.instancio.internal.generator.sql.SqlDateGenerator");
        mapByClassName(context, generators,
                "java.sql.Timestamp",
                "org.instancio.internal.generator.sql.TimestampGenerator");
        mapByClassName(context, generators,
                "javax.xml.datatype.XMLGregorianCalendar",
                "org.instancio.internal.generator.xml.XMLGregorianCalendarGenerator");
    }

    private void mapCollection(final Class<?> type, final Class<?> subtype) {
        final CollectionGenerator<?> generator = new CollectionGenerator<>(context);
        generator.subtype(subtype);
        generators.put(type, generator);
    }

    private void mapMap(final Class<?> type, final Class<?> subtype) {
        final MapGenerator<?, ?> generator = new MapGenerator<>(context);
        generator.subtype(subtype);
        generators.put(type, generator);
    }

    private static void mapByClassName(
            final GeneratorContext context,
            final Map<Class<?>, Generator<?>> generators,
            final String generateTypeClassName,
            final String generatorClassName) {

        final Class<?> targetClass = ReflectionUtils.loadClass(generateTypeClassName);
        final Class<?> generatorClass = ReflectionUtils.loadClass(generatorClassName);

        if (targetClass == null || generatorClass == null) {
            return;
        }

        try {
            final Constructor<?> constructor = generatorClass.getConstructor(GeneratorContext.class);
            final Generator<?> generator = (Generator<?>) constructor.newInstance(context);
            generators.put(targetClass, generator);
        } catch (Exception ex) {
            LOG.trace("Failed loading generator class: '{}'", generatorClassName, ex);
        }
    }

    @SuppressWarnings("all")
    public Optional<Generator<?>> get(final Class<?> klass) {
        // Generators provided by SPI take precedence over built-in generators
        Generator<?> generator = generatorProviderFacade.getGenerator(klass)
                .orElse(generators.get(klass));

        if (generator == null) {
            if (klass.isArray()) {
                generator = new ArrayGenerator<>(context, klass);
            } else if (klass.isEnum()) {
                generator = new EnumGenerator(context, klass);
            }
        }
        return Optional.ofNullable(generator);
    }
}

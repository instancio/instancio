package org.instancio.internal;

import org.instancio.Generator;
import org.instancio.generators.ArrayGenerator;
import org.instancio.generators.BigDecimalGenerator;
import org.instancio.generators.coretypes.BooleanGenerator;
import org.instancio.generators.coretypes.ByteGenerator;
import org.instancio.generators.coretypes.CharacterGenerator;
import org.instancio.generators.collections.CollectionGenerator;
import org.instancio.generators.collections.ConcurrentHashMapGenerator;
import org.instancio.generators.collections.ConcurrentSkipListMapGenerator;
import org.instancio.generators.coretypes.DoubleGenerator;
import org.instancio.generators.EnumGenerator;
import org.instancio.generators.coretypes.FloatGenerator;
import org.instancio.generators.collections.HashSetGenerator;
import org.instancio.generators.coretypes.IntegerGenerator;
import org.instancio.generators.LocalDateGenerator;
import org.instancio.generators.LocalDateTimeGenerator;
import org.instancio.generators.coretypes.LongGenerator;
import org.instancio.generators.collections.MapGenerator;
import org.instancio.generators.coretypes.ShortGenerator;
import org.instancio.generators.coretypes.StringGenerator;
import org.instancio.generators.collections.TreeMapGenerator;
import org.instancio.generators.collections.TreeSetGenerator;
import org.instancio.generators.UUIDGenerator;
import org.instancio.generators.XMLGregorianCalendarGenerator;
import org.instancio.internal.random.RandomProvider;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;

class GeneratorMap {

    private final Map<Class<?>, Generator<?>> generatorMap = new HashMap<>();
    private final RandomProvider random;

    GeneratorMap(final RandomProvider random) {
        this.random = random;

        // Core types
        generatorMap.put(byte.class, new ByteGenerator(random));
        generatorMap.put(short.class, new ShortGenerator(random));
        generatorMap.put(int.class, new IntegerGenerator(random));
        generatorMap.put(long.class, new LongGenerator(random));
        generatorMap.put(float.class, new FloatGenerator(random));
        generatorMap.put(double.class, new DoubleGenerator(random));
        generatorMap.put(boolean.class, new BooleanGenerator(random));
        generatorMap.put(char.class, new CharacterGenerator(random));
        generatorMap.put(Byte.class, new ByteGenerator(random));
        generatorMap.put(Short.class, new ShortGenerator(random));
        generatorMap.put(Integer.class, new IntegerGenerator(random));
        generatorMap.put(Long.class, new LongGenerator(random));
        generatorMap.put(Float.class, new FloatGenerator(random));
        generatorMap.put(Double.class, new DoubleGenerator(random));
        generatorMap.put(Boolean.class, new BooleanGenerator(random));
        generatorMap.put(Character.class, new CharacterGenerator(random));
        generatorMap.put(String.class, new StringGenerator(random));

        generatorMap.put(Number.class, new IntegerGenerator(random));
        generatorMap.put(BigDecimal.class, new BigDecimalGenerator(random));
        generatorMap.put(LocalDate.class, new LocalDateGenerator(random));
        generatorMap.put(LocalDateTime.class, new LocalDateTimeGenerator(random));
        generatorMap.put(UUID.class, new UUIDGenerator(random));
        generatorMap.put(XMLGregorianCalendar.class, new XMLGregorianCalendarGenerator());

        // Collections
        generatorMap.put(Collection.class, new CollectionGenerator());
        generatorMap.put(List.class, new CollectionGenerator());
        generatorMap.put(Map.class, new MapGenerator());
        generatorMap.put(ConcurrentMap.class, new ConcurrentHashMapGenerator());
        generatorMap.put(ConcurrentNavigableMap.class, new ConcurrentSkipListMapGenerator());
        generatorMap.put(SortedMap.class, new TreeMapGenerator());
        generatorMap.put(NavigableMap.class, new TreeMapGenerator());
        generatorMap.put(Set.class, new HashSetGenerator());
        generatorMap.put(SortedSet.class, new TreeSetGenerator());
        generatorMap.put(NavigableSet.class, new TreeSetGenerator());
    }

    Generator<?> get(Class<?> klass) {
        Generator<?> generator = generatorMap.get(klass);

        if (generator == null) {
            if (klass.isEnum()) {
                generator = new EnumGenerator(random, klass);
                generatorMap.put(klass, generator);
            } else if (klass.isArray()) {
                throw new IllegalArgumentException("Should be calling getArrayGenerator(Class)!");
            }
        }

        return generator;
    }

    Generator<?> getArrayGenerator(Class<?> componentType) {
        return new ArrayGenerator(componentType);
    }
}

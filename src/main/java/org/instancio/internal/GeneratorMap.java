package org.instancio.internal;

import org.instancio.Generator;
import org.instancio.generators.ArrayGenerator;
import org.instancio.generators.BigDecimalGenerator;
import org.instancio.generators.BooleanGenerator;
import org.instancio.generators.ByteGenerator;
import org.instancio.generators.CharacterGenerator;
import org.instancio.generators.CollectionGenerator;
import org.instancio.generators.ConcurrentHashMapGenerator;
import org.instancio.generators.ConcurrentSkipListMapGenerator;
import org.instancio.generators.DoubleGenerator;
import org.instancio.generators.EnumGenerator;
import org.instancio.generators.FloatGenerator;
import org.instancio.generators.HashSetGenerator;
import org.instancio.generators.IntegerGenerator;
import org.instancio.generators.LocalDateGenerator;
import org.instancio.generators.LocalDateTimeGenerator;
import org.instancio.generators.LongGenerator;
import org.instancio.generators.MapGenerator;
import org.instancio.generators.ShortGenerator;
import org.instancio.generators.StringGenerator;
import org.instancio.generators.TreeMapGenerator;
import org.instancio.generators.TreeSetGenerator;
import org.instancio.generators.UUIDGenerator;
import org.instancio.generators.XMLGregorianCalendarGenerator;

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

    GeneratorMap() {
        // Core types
        generatorMap.put(byte.class, new ByteGenerator());
        generatorMap.put(short.class, new ShortGenerator());
        generatorMap.put(int.class, new IntegerGenerator());
        generatorMap.put(long.class, new LongGenerator());
        generatorMap.put(float.class, new FloatGenerator());
        generatorMap.put(double.class, new DoubleGenerator());
        generatorMap.put(boolean.class, new BooleanGenerator());
        generatorMap.put(char.class, new CharacterGenerator());
        generatorMap.put(Byte.class, new ByteGenerator());
        generatorMap.put(Short.class, new ShortGenerator());
        generatorMap.put(Integer.class, new IntegerGenerator());
        generatorMap.put(Long.class, new LongGenerator());
        generatorMap.put(Float.class, new FloatGenerator());
        generatorMap.put(Double.class, new DoubleGenerator());
        generatorMap.put(Boolean.class, new BooleanGenerator());
        generatorMap.put(Character.class, new CharacterGenerator());
        generatorMap.put(String.class, new StringGenerator());

        generatorMap.put(Number.class, new IntegerGenerator());
        generatorMap.put(BigDecimal.class, new BigDecimalGenerator());
        generatorMap.put(LocalDate.class, new LocalDateGenerator());
        generatorMap.put(LocalDateTime.class, new LocalDateTimeGenerator());
        generatorMap.put(UUID.class, new UUIDGenerator());
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
                generator = new EnumGenerator(klass);
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

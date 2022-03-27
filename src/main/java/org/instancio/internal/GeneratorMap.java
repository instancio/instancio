/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.internal;

import org.instancio.Generator;
import org.instancio.generators.ArrayGenerator;
import org.instancio.generators.BigDecimalGenerator;
import org.instancio.generators.EnumGenerator;
import org.instancio.generators.LocalDateGenerator;
import org.instancio.generators.LocalDateTimeGenerator;
import org.instancio.generators.UUIDGenerator;
import org.instancio.generators.XMLGregorianCalendarGenerator;
import org.instancio.generators.collections.CollectionGenerator;
import org.instancio.generators.collections.ConcurrentHashMapGenerator;
import org.instancio.generators.collections.ConcurrentSkipListMapGenerator;
import org.instancio.generators.collections.HashSetGenerator;
import org.instancio.generators.collections.MapGenerator;
import org.instancio.generators.collections.TreeMapGenerator;
import org.instancio.generators.collections.TreeSetGenerator;
import org.instancio.generators.coretypes.BooleanGenerator;
import org.instancio.generators.coretypes.ByteGenerator;
import org.instancio.generators.coretypes.CharacterGenerator;
import org.instancio.generators.coretypes.DoubleGenerator;
import org.instancio.generators.coretypes.FloatGenerator;
import org.instancio.generators.coretypes.IntegerGenerator;
import org.instancio.generators.coretypes.LongGenerator;
import org.instancio.generators.coretypes.ShortGenerator;
import org.instancio.generators.coretypes.StringGenerator;
import org.instancio.internal.model.ModelContext;

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

public class GeneratorMap {

    private final Map<Class<?>, Generator<?>> generatorMap = new HashMap<>();
    private final ModelContext<?> context;

    GeneratorMap(final ModelContext<?> context) {
        this.context = context;

        // Core types
        generatorMap.put(byte.class, new ByteGenerator(context));
        generatorMap.put(short.class, new ShortGenerator(context));
        generatorMap.put(int.class, new IntegerGenerator(context));
        generatorMap.put(long.class, new LongGenerator(context));
        generatorMap.put(float.class, new FloatGenerator(context));
        generatorMap.put(double.class, new DoubleGenerator(context));
        generatorMap.put(boolean.class, new BooleanGenerator(context));
        generatorMap.put(char.class, new CharacterGenerator(context));
        generatorMap.put(Byte.class, new ByteGenerator(context));
        generatorMap.put(Short.class, new ShortGenerator(context));
        generatorMap.put(Integer.class, new IntegerGenerator(context));
        generatorMap.put(Long.class, new LongGenerator(context));
        generatorMap.put(Float.class, new FloatGenerator(context));
        generatorMap.put(Double.class, new DoubleGenerator(context));
        generatorMap.put(Boolean.class, new BooleanGenerator(context));
        generatorMap.put(Character.class, new CharacterGenerator(context));
        generatorMap.put(String.class, new StringGenerator(context));

        generatorMap.put(Number.class, new IntegerGenerator(context));
        generatorMap.put(BigDecimal.class, new BigDecimalGenerator(context));
        generatorMap.put(LocalDate.class, new LocalDateGenerator(context));
        generatorMap.put(LocalDateTime.class, new LocalDateTimeGenerator(context));
        generatorMap.put(UUID.class, new UUIDGenerator(context));
        generatorMap.put(XMLGregorianCalendar.class, new XMLGregorianCalendarGenerator(context));

        // Collections
        generatorMap.put(Collection.class, new CollectionGenerator<>(context));
        generatorMap.put(List.class, new CollectionGenerator<>(context));
        generatorMap.put(Map.class, new MapGenerator<>(context));
        generatorMap.put(ConcurrentMap.class, new ConcurrentHashMapGenerator<>(context));
        generatorMap.put(ConcurrentNavigableMap.class, new ConcurrentSkipListMapGenerator<>(context));
        generatorMap.put(SortedMap.class, new TreeMapGenerator<>(context));
        generatorMap.put(NavigableMap.class, new TreeMapGenerator<>(context));
        generatorMap.put(Set.class, new HashSetGenerator<>(context));
        generatorMap.put(SortedSet.class, new TreeSetGenerator<>(context));
        generatorMap.put(NavigableSet.class, new TreeSetGenerator<>(context));
    }

    public Generator<?> get(Class<?> klass) {
        Generator<?> generator = generatorMap.get(klass);

        if (generator == null) {
            if (klass.isEnum()) {
                generator = new EnumGenerator(context, klass);
                generatorMap.put(klass, generator);
            } else if (klass.isArray()) {
                throw new IllegalArgumentException("Should be calling getArrayGenerator(Class)!");
            }
        }

        return generator;
    }

    public Generator<?> getArrayGenerator(Class<?> componentType) {
        return new ArrayGenerator<>(context, componentType);
    }
}

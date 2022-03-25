package org.instancio.api.features.generators;

import org.instancio.Instancio;
import org.instancio.pojo.collections.maps.MapByteDouble;
import org.instancio.pojo.collections.sets.SetLong;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Bindings.all;
import static org.instancio.Bindings.allBytes;
import static org.instancio.Bindings.allDoubles;
import static org.instancio.Bindings.allLongs;

class BuiltInNumberGeneratorTest {

    private static final int SAMPLE_SIZE = 500;

    @Test
    @DisplayName("When nullable specified for a number, should generate an occasional null value in Collection")
    void nullableNumberInCollection() {
        final SetLong result = Instancio.of(SetLong.class)
                .generate(allLongs(), gen -> gen.longs().nullable())
                .generate(all(Set.class), gen -> gen.collection().minSize(SAMPLE_SIZE).type(HashSet.class))
                .create();

        assertThat(result.getSet()).containsNull();
    }

    @Test
    @DisplayName("When nullable specified for a number, should generate an occasional null value in Map keys")
    void nullableNumberAsMapKey() {
        final MapByteDouble result = Instancio.of(MapByteDouble.class)
                .generate(allBytes(), gen -> gen.bytes().nullable())
                .generate(all(Map.class), gen -> gen.map().minSize(SAMPLE_SIZE).type(HashMap.class))
                .create();

        assertThat(result.getMap().keySet()).containsNull();
    }

    @Test
    @DisplayName("When nullable specified for a number, should generate an occasional null value in Map values")
    void nullableNumberAsMapValue() {
        final MapByteDouble result = Instancio.of(MapByteDouble.class)
                .generate(allDoubles(), gen -> gen.doubles().nullable())
                .generate(all(Map.class), gen -> gen.map().minSize(SAMPLE_SIZE).type(HashMap.class))
                .create();

        assertThat(result.getMap().values()).containsNull();
    }

}

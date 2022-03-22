package org.instancio.api.features;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.testsupport.Constants;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WithSeedTest {

    private static final int SEED = 123;
    private static final int SAMPLE_SIZE = 10;

    @Test
    void generateValuesWithTheSameSeed() {
        final Set<UUID> allResults = new HashSet<>();

        Set<UUID> uuids = null;
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            uuids = Instancio.of(new TypeToken<Set<UUID>>() {})
                    .withSeed(SEED)
                    .create();

            assertThat(uuids).hasSize(Constants.COLLECTION_SIZE);
            allResults.addAll(uuids);
        }
        assertThat(allResults).isEqualTo(uuids);
    }

    @Test
    void generateValuesWithRandomSeed() {
        final Set<UUID> allResults = new HashSet<>();

        for (int i = 0; i < SAMPLE_SIZE; i++) {
            Set<UUID> uuids = Instancio.of(new TypeToken<Set<UUID>>() {}).create();
            assertThat(uuids).hasSize(Constants.COLLECTION_SIZE);
            allResults.addAll(uuids);
        }

        assertThat(allResults).hasSize(SAMPLE_SIZE * Constants.COLLECTION_SIZE);
    }
}

package org.instancio.test.concurrency;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class ChildThreadTest {

    private final long SEED = -1L;
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.STRING_MAX_LENGTH, 15)
            .set(Keys.STRING_MIN_LENGTH, 15);

    @Seed(SEED)
    @Test
    void childThreadShouldInheritSeed() throws ExecutionException, InterruptedException, TimeoutException {
        var stringResult = executorService.submit(() -> Instancio.of(String.class).asResult()).get(10, TimeUnit.SECONDS);
        var expected = Instancio.of(String.class).withSeed(SEED).create();
        assertThat(stringResult.getSeed()).isEqualTo(SEED);
        assertThat(stringResult.get()).isEqualTo(expected);
    }

    @Test
    void childThreadShouldInheritSettings() throws ExecutionException, InterruptedException, TimeoutException {
        var string = executorService.submit(() -> Instancio.create(String.class)).get(10, TimeUnit.SECONDS);
        assertThat(string).hasSize(15);
    }
}

package org.instancio.test.features.generator.id.bra;

import org.instancio.GeneratorSpecProvider;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
public class CpfGeneratorTest {

    private static String create(final GeneratorSpecProvider<String> spec) {
        return Instancio.of(String.class)
                .generate(root(), spec)
                .create();
    }

    @Test
    void cpf() {
        final String result = create(gen -> gen.id().bra().cpf());

        assertThat(result)
                .hasSize(11)
                .containsOnlyDigits();
    }

    @Test
    void nullable() {
        final Stream<String> result = Stream.generate(() -> create(gen -> gen.id().bra().cpf().nullable()))
                .limit(Constants.SAMPLE_SIZE_DDD);

        assertThat(result).containsNull();
    }
}

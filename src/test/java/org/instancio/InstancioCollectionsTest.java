package org.instancio;

import org.junit.jupiter.api.Test;
import org.instancio.pojo.sets.WithSet;

import static org.assertj.core.api.Assertions.assertThat;

public class InstancioCollectionsTest {

    @Test
    void withSet() {
        WithSet result = Instancio.of(WithSet.class).create();

        System.out.println(result);
        assertThat(result.getSet()).isNotEmpty();
        assertThat(result.getSortedSet()).isNotEmpty();
        assertThat(result.getNavigableSet()).isNotEmpty();
        assertThat(result.getHashSet()).isNotEmpty();
        assertThat(result.getTreeSet()).isNotEmpty();
        assertThat(result.getConcurrentSkipListSet()).isNotEmpty();
    }
}

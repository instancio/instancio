package org.instancio.generation;

import org.instancio.Instancio;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Bar;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Baz;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Foo;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.FooBarBazContainer;
import org.instancio.testsupport.tags.CreateTag;
import org.instancio.testsupport.tags.GenericsTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@CreateTag
@GenericsTag
public class FooBarBazContainerGenerationTest {

    @Test
    void generate() {
        FooBarBazContainer result = Instancio.of(FooBarBazContainer.class).create();
        assertThat(result).isNotNull();

        final Foo<Bar<Baz<String>>> item = result.getItem();
        assertThat(item).isNotNull();
        assertThat(item.getOtherFooValue()).isExactlyInstanceOf(Object.class);

        final Bar<Baz<String>> fooValue = item.getFooValue();
        assertThat(fooValue).isExactlyInstanceOf(Bar.class);
        assertThat(fooValue.getOtherBarValue()).isExactlyInstanceOf(Object.class);

        final Baz<String> barValue = fooValue.getBarValue();
        assertThat(barValue).isExactlyInstanceOf(Baz.class);
        assertThat(barValue.getBazValue()).isInstanceOf(String.class);
    }
}

package org.instancio.creation.generics;

import org.instancio.pojo.generics.foobarbaz.itemcontainer.Bar;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Baz;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Foo;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.FooBarBazContainer;
import org.instancio.testsupport.templates.CreationTestTemplate;
import org.instancio.testsupport.tags.GenericsTag;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class FooBarBazContainerCreationTest extends CreationTestTemplate<FooBarBazContainer> {

    @Override
    protected void verify(FooBarBazContainer result) {
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

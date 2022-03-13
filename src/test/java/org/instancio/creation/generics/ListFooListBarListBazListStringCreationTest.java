package org.instancio.creation.generics;

import org.instancio.pojo.generics.foobarbaz.Bar;
import org.instancio.pojo.generics.foobarbaz.Baz;
import org.instancio.pojo.generics.foobarbaz.Foo;
import org.instancio.pojo.generics.foobarbaz.ListFooListBarListBazListString;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class ListFooListBarListBazListStringCreationTest extends CreationTestTemplate<ListFooListBarListBazListString> {

    @Override
    protected void verify(ListFooListBarListBazListString result) {
        // List<Foo<List<Bar<List<Baz<List<String>>>>>>>

        final List<Foo<List<Bar<List<Baz<List<String>>>>>>> list = result.getListOfFoo_ListOfBar_ListOfBaz_ListOfString();
        assertThat(list)
                // List<Foo>
                .isInstanceOf(List.class)
                .hasSize(Constants.COLLECTION_SIZE)
                .hasOnlyElementsOfType(Foo.class)
                .allSatisfy(foo -> assertThat(foo.getFooValue())
                        // List<Bar>
                        .isInstanceOf(List.class)
                        .hasSize(Constants.COLLECTION_SIZE)
                        .hasOnlyElementsOfType(Bar.class)
                        .allSatisfy(bar -> assertThat(bar.getBarValue())
                                // List<Baz>
                                .isInstanceOf(List.class)
                                .hasSize(Constants.COLLECTION_SIZE)
                                .hasOnlyElementsOfType(Baz.class)
                                .allSatisfy(str -> assertThat(str)
                                        .isInstanceOf(String.class))));
    }

}

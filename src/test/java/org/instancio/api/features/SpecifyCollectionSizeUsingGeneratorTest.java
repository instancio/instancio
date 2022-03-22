package org.instancio.api.features;

import org.apache.commons.lang3.RandomUtils;
import org.instancio.Instancio;
import org.instancio.pojo.collections.lists.TwoListsOfItemString;
import org.instancio.pojo.collections.maps.TwoMapsOfIntegerItemString;
import org.instancio.pojo.generics.basic.Item;
import org.instancio.testsupport.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Bindings.all;
import static org.instancio.Bindings.field;

// TODO test nested lists/maps
class SpecifyCollectionSizeUsingGeneratorTest {
}

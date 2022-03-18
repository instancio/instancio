package org.instancio.pojo.collections;

import lombok.Getter;
import lombok.ToString;

import java.util.Collection;

@Getter
@ToString
public class TwoStringCollections {

    private Collection<String> one;

    private Collection<String> two;
}

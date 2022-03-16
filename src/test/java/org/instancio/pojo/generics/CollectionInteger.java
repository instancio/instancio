package org.instancio.pojo.generics;

import lombok.Getter;
import lombok.ToString;

import java.util.Collection;

@Getter
@ToString
public class CollectionInteger {

    private Collection<Integer> collection;
}

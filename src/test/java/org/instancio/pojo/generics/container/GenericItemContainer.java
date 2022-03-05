package org.instancio.pojo.generics.container;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class GenericItemContainer<X, Y> {
    // item L
    private GenericItem<X> itemValueL;
    private GenericItem<X>[] itemArrayL;
    private List<GenericItem<X>> itemListL;

    // item R
    private GenericItem<X> itemValueR;
    private GenericItem<X>[] itemArrayR;
    private List<GenericItem<X>> itemListR;

    // pair
    private Pair<X, Y> pairValue;
    private Pair<X, Y>[] pairArray;
    private List<Pair<X, Y>> pairList;
}

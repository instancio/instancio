package org.instancio.pojo.generics.container;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class GenericItemContainer<L, R> {
    // item L
    private GenericItem<L> itemValueL;
    private GenericItem<L>[] itemArrayL;
    private List<GenericItem<L>> itemListL;

    // item R
    private GenericItem<L> itemValueR;
    private GenericItem<L>[] itemArrayR;
    private List<GenericItem<L>> itemListR;

    // pair
    private Pair<L, R> pairValue;
    private Pair<L, R>[] pairArray;
    private List<Pair<L, R>> pairList;
}

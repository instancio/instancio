package org.instancio.pojo.cyclic;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class CyclicList {

    private List<CyclicList> items;
}

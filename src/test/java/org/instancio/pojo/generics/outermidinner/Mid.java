package org.instancio.pojo.generics.outermidinner;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class Mid<T> {
    private List<T> midList = new ArrayList<>();
}

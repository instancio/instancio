package org.instancio.pojo.generics.foobarbaz.itemcontainer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Bar<Y> {
    private Y barValue;
    private Object otherBarValue;
}

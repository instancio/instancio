package org.instancio.pojo.generics.foobarbaz;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Baz<Z> {
    private Z bazValue;
}
